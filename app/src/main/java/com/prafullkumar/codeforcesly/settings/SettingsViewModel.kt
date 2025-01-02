package com.prafullkumar.codeforcesly.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.friends.data.local.FriendsDatabase
import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val handle: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isHandleChangeSuccess: Boolean = false,
    val isLogoutSuccess: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val pref: SharedPrefManager,
    private val profileApiService: ProfileApiService,
    private val friendsDatabase: FriendsDatabase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        setState()
    }

    private fun setState() {
        if (!pref.getHandle().isNullOrEmpty()) {
            _uiState.value = uiState.value.copy(handle = pref.getHandle()!!)
        }
    }

    fun updateHandle(newHandle: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                if (!verifyHandle(newHandle)) return@launch
                else {
                    pref.setHandle(newHandle)
                    _uiState.value = _uiState.value.copy(
                        handle = newHandle, isLoading = false, isHandleChangeSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message ?: "Failed to update handle"
                )
            }
        }
    }

    private suspend fun verifyHandle(handle: String): Boolean {
        try {
            if (handle.isNotBlank()) {
                val userInfo = profileApiService.getUserInfo(handle)
                if (userInfo.status == "OK") {
                    return true
                } else {
                    _uiState.value = _uiState.value.copy(error = "User not found")
                    return false
                }
            } else {
                _uiState.value = _uiState.value.copy(error = "Handle cannot be empty")
                return false
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                pref.setLoggedIn(false)
                pref.setHandle("")
                friendsDatabase.clearAllTables()
                _uiState.value = _uiState.value.copy(
                    isLoading = false, isLogoutSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message ?: "Failed to logout"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = SettingsUiState()
    }
}