package com.prafullkumar.codeforcesly.profile.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.runCatchingCancellable
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    var isRefreshing by mutableStateOf(false)
    var recentSubmissions by mutableStateOf<List<SubmissionDto>>(emptyList())

    private var hasLoaded = false

    fun clearData() {
        _uiState.update { ProfileUiState.Loading }
        recentSubmissions = emptyList()
    }

    init {
        if (!hasLoaded) {
            getUserInformation()
        }
    }

    fun getUserInformation() {
        if (hasLoaded) return
        viewModelScope.launch(Dispatchers.IO) {
            loadContent(forceRefresh = false)
        }
    }

    private suspend fun loadContent(forceRefresh: Boolean) = coroutineScope {
        try {
            if (!hasLoaded) _uiState.value = ProfileUiState.Loading
            val userInfoRequest = async {
                runCatchingCancellable { profileRepository.getUserInfo(forceRefresh) }
            }
            val submissionsRequest = async {
                runCatchingCancellable { profileRepository.getRecentSubmissions(forceRefresh) }
            }
            val userInfoResult = userInfoRequest.await()
            val userInfo = userInfoResult.getOrNull()
            if (userInfo?.status == "OK" && userInfo.result.isNotEmpty()) {
                _uiState.value = ProfileUiState.Success(userInfo.result[0])
                hasLoaded = true
            } else {
                _uiState.value = ProfileUiState.Error(
                    userInfoResult.exceptionOrNull()?.message ?: "User not found"
                )
            }

            submissionsRequest.await().getOrNull()
                ?.takeIf { it.status == "OK" }
                ?.let { recentSubmissions = it.result.take(5) }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
        }
    }

    fun refreshState() {
        viewModelScope.launch(Dispatchers.IO) {
            isRefreshing = true
            loadContent(forceRefresh = true)
            isRefreshing = false
        }
    }
}
