package com.prafullkumar.codeforcesly.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val handle = context.getSharedPreferences("codeforces_prefs", Context.MODE_PRIVATE)
                    .getString("user_handle", "") ?: ""
                val userInfo = profileRepository.getUserInfo(handle)
                if (userInfo.status == "OK") {
                    _uiState.value = ProfileUiState.Success(userInfo.result[0])
                } else {
                    _uiState.value = ProfileUiState.Error("User not found")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}