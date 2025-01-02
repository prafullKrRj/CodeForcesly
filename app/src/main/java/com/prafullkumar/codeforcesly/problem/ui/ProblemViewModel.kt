package com.prafullkumar.codeforcesly.problem.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.problem.domain.ProblemsRepository
import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    var isRefreshing by mutableStateOf(false)

    init {
        _uiState.update { it.copy(problems = allProblems) }
        if (allProblems.isEmpty()) fetchProblems()
    }

    private fun fetchProblems() {
        if (allProblems.isNotEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            loadData()
        }
    }

    private suspend fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        try {
            val response = repository.getAllProblems()
            if (response.status == "OK") {
                val problemList = response.result.problems
                allProblems = problemList
                _uiState.update {
                    it.copy(
                        problems = allProblems,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        problems = emptyList(),
                        error = response.status
                    )
                }
            }
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

    fun refreshState() {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true
            loadData()
            applyFiltersAndSort()
            isRefreshing = false
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

    private fun applyFiltersAndSort() {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val currentState = _uiState.value
            val filteredProblems = allProblems
                .filter { (it.rating?.toInt() ?: 0) in currentState.selectedRatingRange }
                .filter { currentState.selectedTags.isEmpty() || currentState.selectedTags.all { tag -> tag in it.tags } }
                .filter { it.name.contains(currentState.searchQuery, ignoreCase = true) }

            val sortedProblems = when (currentState.sortOrder) {
                SortOrder.RATING_ASC -> filteredProblems.sortedBy { it.rating }
                SortOrder.RATING_DESC -> filteredProblems.sortedByDescending { it.rating }
                SortOrder.NAME_ASC -> filteredProblems.sortedBy { it.name }
                SortOrder.NAME_DESC -> filteredProblems.sortedByDescending { it.name }
            }

            _uiState.update { it.copy(problems = sortedProblems) }
        }
    }
}