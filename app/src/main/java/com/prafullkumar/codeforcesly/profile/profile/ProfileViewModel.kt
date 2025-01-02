package com.prafullkumar.codeforcesly.profile.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    var isRefreshing by mutableStateOf(false)

    private var hasLoaded = false

    init {
        if (!hasLoaded) {
            getUserInformation()
        }
    }

    fun getUserInformation() {
        if (hasLoaded) return
        viewModelScope.launch {
            loadContent()
        }
    }

    private suspend fun loadContent() {
        try {
            _uiState.value = ProfileUiState.Loading
            val userInfo = profileRepository.getUserInfo()
            if (userInfo.status == "OK") {
                _uiState.value = ProfileUiState.Success(userInfo.result[0])
                hasLoaded = true
            } else {
                _uiState.value = ProfileUiState.Error("User not found")
            }
        } catch (e: Exception) {
            _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
        }
    }

    fun refreshState() {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true
            loadContent()
            isRefreshing = false
        }
    }
}