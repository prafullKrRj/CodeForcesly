package com.prafullkumar.codeforcesly.problem

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.problem.model.Problem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
class ProblemsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProblemsUiState())
    val uiState: StateFlow<ProblemsUiState> = _uiState.asStateFlow()

    init {
        fetchProblems()
    }

    companion object {
        private val apiService = Retrofit.Builder()
            .baseUrl("https://codeforces.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ProblemsApiService::class.java)
    }

    private fun fetchProblems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = apiService.getProblems("https://codeforces.com/api/problemset.problems?tags=")
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
                Log.d("ProblemsViewModel", "Problems: ${response.result.problems}")
            } catch (e: Exception) {
                Log.d("ProblemsViewModel", "Error: ${e.message}")
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