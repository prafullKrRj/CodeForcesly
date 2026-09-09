package com.prafullkumar.codeforcesly.onBoarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserEntity
import com.prafullkumar.codeforcesly.onBoarding.domain.OnBoardingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OnboardingState {
    data object Initial : OnboardingState()
    data object Loading : OnboardingState()
    data class Success(val userEntity: UserEntity) : OnboardingState()
    data class Error(val message: String) : OnboardingState()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: OnBoardingRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow<OnboardingState>(OnboardingState.Initial)
    val uiState: StateFlow<OnboardingState> = _uiState

    fun validateAndStoreHandle(handle: String) {
        if (_uiState.value is OnboardingState.Loading) return
        val normalizedHandle = handle.trim()
        val validationError = when {
            normalizedHandle.isEmpty() -> "Enter your Codeforces handle"
            normalizedHandle.length > 24 -> "Codeforces handles are limited to 24 characters"
            normalizedHandle.any { it.isWhitespace() } -> "Handle cannot contain spaces"
            else -> null
        }
        if (validationError != null) {
            _uiState.value = OnboardingState.Error(validationError)
            return
        }

        viewModelScope.launch {
            _uiState.value = OnboardingState.Loading
            repository.fetchAndStoreUser(normalizedHandle)
                .onSuccess {
                    _uiState.value = OnboardingState.Success(it)
                }
                .onFailure { _uiState.value = OnboardingState.Error(it.message ?: "Unknown error") }
        }
    }

    fun clearError() {
        if (_uiState.value is OnboardingState.Error) {
            _uiState.value = OnboardingState.Initial
        }
    }
}
