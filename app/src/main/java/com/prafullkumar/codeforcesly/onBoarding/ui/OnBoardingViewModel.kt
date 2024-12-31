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
        if (handle.isBlank()) {
            _uiState.value = OnboardingState.Error("Handle cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = OnboardingState.Loading
            repository.fetchAndStoreUser(handle)
                .onSuccess {
                    _uiState.value = OnboardingState.Success(it)
                }
                .onFailure { _uiState.value = OnboardingState.Error(it.message ?: "Unknown error") }
        }
    }
}
