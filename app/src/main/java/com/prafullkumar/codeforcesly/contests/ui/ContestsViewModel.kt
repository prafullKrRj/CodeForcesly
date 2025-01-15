package com.prafullkumar.codeforcesly.contests.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.contests.data.ContestsApiService
import com.prafullkumar.codeforcesly.contests.domain.models.contest.Contest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ContestsViewModel @Inject constructor(
    private val apiService: ContestsApiService
) : ViewModel() {
    private val _contests = MutableStateFlow<List<Contest>>(emptyList())
    val contests: StateFlow<List<Contest>> = _contests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        viewModelScope.launch {
            fetchContests()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchContests()
            delay(1000)
            _isRefreshing.update { false }
        }
    }

    private suspend fun fetchContests() {
        _isLoading.value = true
        try {
            val contests = apiService.getContests()
            _contests.update {
                contests.contests
            }
        } catch (e: Exception) {
            Log.d("ContestsViewModel", "Error fetching contests: $e")
        } finally {
            _isLoading.value = false
        }
    }
}