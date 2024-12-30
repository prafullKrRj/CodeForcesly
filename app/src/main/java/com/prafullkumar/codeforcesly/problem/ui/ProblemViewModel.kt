package com.prafullkumar.codeforcesly.problem.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.problem.data.ProblemsRepositoryImpl
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

    init {
        if (_uiState.value.problems.isEmpty()) fetchProblems()
    }

    private fun fetchProblems() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.getAllProblems()
                if (response.status == "OK") {
                    val problemList = response.result.problems
                    _uiState.update {
                        it.copy(
                            problems = problemList,
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
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateRatingRange(range: ClosedRange<Int>) {
        _uiState.update { it.copy(selectedRatingRange = range) }
    }

    fun updateSortOrder(order: SortOrder) {
        _uiState.update { it.copy(sortOrder = order) }
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
    }
}