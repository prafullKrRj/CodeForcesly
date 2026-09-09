package com.prafullkumar.codeforcesly.problem.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.runCatchingCancellable
import com.prafullkumar.codeforcesly.problem.domain.ProblemsRepository
import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortOrder {
    RATING_ASC, RATING_DESC, NAME_ASC, NAME_DESC
}

data class ProblemsUiState(
    val problems: List<Problem> = emptyList(),
    val recommendedProblems: List<Problem> = emptyList(),
    val solvedProblemKeys: Set<String> = emptySet(),
    val isRecommendationsLoading: Boolean = false,
    val popularTags: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedRatingRange: ClosedRange<Int> = 800..3500,
    val selectedTags: Set<String> = emptySet(),
    val sortOrder: SortOrder = SortOrder.RATING_DESC
)

@HiltViewModel
class ProblemsViewModel @Inject constructor(
    private val repository: ProblemsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProblemsUiState())
    val uiState: StateFlow<ProblemsUiState> = _uiState.asStateFlow()
    private var allProblems: List<Problem> = emptyList()
    private var loadJob: Job? = null
    private var filterJob: Job? = null
    private var recommendationJob: Job? = null

    var isRefreshing by mutableStateOf(false)

    init {
        _uiState.update { it.copy(problems = allProblems) }
        if (allProblems.isEmpty()) fetchProblems()
    }

    private fun fetchProblems() {
        if (allProblems.isNotEmpty()) return
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            loadData(forceRefresh = false)
        }
    }

    private suspend fun loadData(forceRefresh: Boolean) {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val response = if (forceRefresh) {
                repository.refreshAllProblems()
            } else {
                repository.getAllProblems()
            }
            if (response.status == "OK") {
                val problemList = response.result.problems
                allProblems = problemList
                _uiState.update {
                    it.copy(
                        problems = filterAndSort(allProblems, it),
                        isRecommendationsLoading = true,
                        popularTags = problemList.asSequence()
                            .flatMap { problem -> problem.tags.orEmpty().asSequence() }
                            .groupingBy { tag -> tag }
                            .eachCount()
                            .entries
                            .sortedByDescending { entry -> entry.value }
                            .take(MAX_VISIBLE_TAGS)
                            .map { entry -> entry.key },
                        error = null
                    )
                }
                loadRecommendations(problemList)
            } else {
                _uiState.update {
                    it.copy(
                        problems = emptyList(),
                        error = response.status
                    )
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadRecommendations(problems: List<Problem>) {
        recommendationJob?.cancel()
        recommendationJob = viewModelScope.launch(Dispatchers.IO) {
            val solvedProblemKeys = runCatchingCancellable {
                repository.getSolvedProblemKeys()
            }.getOrDefault(emptySet())
            val recommendations = runCatchingCancellable {
                repository.getRecommendedProblems(problems)
            }.getOrDefault(emptyList())
            _uiState.update {
                it.copy(
                    solvedProblemKeys = solvedProblemKeys,
                    recommendedProblems = recommendations,
                    isRecommendationsLoading = false
                )
            }
        }
    }

    fun refreshState() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true
            try {
                loadData(forceRefresh = true)
            } finally {
                isRefreshing = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFiltersAndSort()
    }

    fun updateRatingRange(range: ClosedRange<Int>) {
        _uiState.update { it.copy(selectedRatingRange = range) }
        applyFiltersAndSort()
    }

    fun updateSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
        applyFiltersAndSort()
    }

    fun toggleTag(tag: String) {
        _uiState.update {
            val currentTags = it.selectedTags
            val newTags = if (currentTags.contains(tag)) {
                currentTags - tag
            } else {
                currentTags + tag
            }
            it.copy(selectedTags = newTags)
        }
        applyFiltersAndSort()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedRatingRange = 800..3500,
                selectedTags = emptySet(),
                sortOrder = SortOrder.RATING_DESC
            )
        }
        applyFiltersAndSort()
    }

    private fun applyFiltersAndSort() {
        filterJob?.cancel()
        filterJob = viewModelScope.launch(Dispatchers.Default) {
            delay(120)
            val currentState = _uiState.value
            val filteredProblems = filterAndSort(allProblems, currentState)

            _uiState.update { it.copy(problems = filteredProblems) }
        }
    }

    private fun filterAndSort(
        problems: List<Problem>,
        state: ProblemsUiState,
    ): List<Problem> {
        val query = state.searchQuery.trim()
        val filteredProblems = problems
            .filter { (it.rating?.toInt() ?: 0) in state.selectedRatingRange }
            .filter {
                state.selectedTags.isEmpty() || state.selectedTags.all { tag -> tag in it.tags.orEmpty() }
            }
            .filter {
                query.isBlank() ||
                    it.name.contains(query, ignoreCase = true) ||
                    it.index.contains(query, ignoreCase = true) ||
                    it.tags.orEmpty().any { tag -> tag.contains(query, ignoreCase = true) }
            }

        return when (state.sortOrder) {
            SortOrder.RATING_ASC -> filteredProblems.sortedBy { it.rating }
            SortOrder.RATING_DESC -> filteredProblems.sortedByDescending { it.rating }
            SortOrder.NAME_ASC -> filteredProblems.sortedBy { it.name }
            SortOrder.NAME_DESC -> filteredProblems.sortedByDescending { it.name }
        }
    }

    private companion object {
        const val MAX_VISIBLE_TAGS = 12
    }
}
