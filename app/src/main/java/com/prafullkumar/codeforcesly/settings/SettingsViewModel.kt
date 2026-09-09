package com.prafullkumar.codeforcesly.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.common.ThemeMode
import com.prafullkumar.codeforcesly.friends.data.local.FriendsDatabase
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDao
import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
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
    private val friendsDatabase: FriendsDatabase,
    private val userDao: UserDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    val themeMode: StateFlow<ThemeMode> = pref.themeMode

    init {
        setState()
    }

    private fun setState() {
        val handle = pref.getHandle().orEmpty()
        if (handle.isNotEmpty()) {
            _uiState.value = uiState.value.copy(handle = handle)
        }
    }

    fun updateHandle(newHandle: String) {
        if (_uiState.value.isLoading) return
        val normalizedHandle = newHandle.trim()
        val validationError = when {
            normalizedHandle.isEmpty() -> "Handle cannot be empty"
            normalizedHandle.length > 24 -> "Codeforces handles are limited to 24 characters"
            normalizedHandle.any { it.isWhitespace() } -> "Handle cannot contain spaces"
            else -> null
        }
        if (validationError != null) {
            _uiState.value = _uiState.value.copy(error = validationError)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                if (!verifyHandle(normalizedHandle)) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@launch
                }
                pref.setHandle(normalizedHandle)
                _uiState.value = _uiState.value.copy(
                    handle = normalizedHandle, isLoading = false, isHandleChangeSuccess = true
                )
            } catch (e: CancellationException) {
                throw e
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
                val profile = userInfo.result.firstOrNull()
                if (userInfo.status == "OK" && profile != null) {
                    userDao.insertUser(profile.toUser())
                    return true
                } else {
                    _uiState.value = _uiState.value.copy(error = "User not found")
                    return false
                }
            } else {
                _uiState.value = _uiState.value.copy(error = "Handle cannot be empty")
                return false
            }
        } catch (e: CancellationException) {
            throw e
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
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = e.message ?: "Failed to logout"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setThemeMode(mode: ThemeMode) {
        pref.setThemeMode(mode)
    }

    fun resetState() {
        _uiState.value = SettingsUiState()
    }
}
