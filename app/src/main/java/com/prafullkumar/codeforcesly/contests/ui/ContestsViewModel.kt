package com.prafullkumar.codeforcesly.contests.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.contests.domain.ContestsRepository
import com.prafullkumar.codeforcesly.contests.domain.models.contest.Contest
import kotlinx.coroutines.CancellationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ContestsViewModel @Inject constructor(
    private val repository: ContestsRepository
) : ViewModel() {
    private val _contests = MutableStateFlow<List<Contest>>(emptyList())
    val contests: StateFlow<List<Contest>> = _contests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            fetchContests(forceRefresh = false)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                fetchContests(forceRefresh = true)
            } finally {
                _isRefreshing.update { false }
            }
        }
    }

    private suspend fun fetchContests(forceRefresh: Boolean) {
        _isLoading.value = true
        try {
            val contests = repository.getContests(forceRefresh).contests.orEmpty()
            _contests.update {
                contests
            }
            _error.value = null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _error.value = e.message ?: "Could not load contests"
        } finally {
            _isLoading.value = false
        }
    }
}
