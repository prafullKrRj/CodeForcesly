package com.prafullkumar.codeforcesly.contests

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ContestsViewModel : ViewModel() {
    private val _contests = MutableStateFlow<List<Contest>>(emptyList())
    val contests: StateFlow<List<Contest>> = _contests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchContests()
    }

    private fun fetchContests() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val contests =
                    ApiService.getService(ContestsApiService::class.java)
                        .getContests()
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
}