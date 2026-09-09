package com.prafullkumar.codeforcesly.friends.ui.friendDetailScren

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo
import com.prafullkumar.codeforcesly.common.model.userrating.Rating
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerData
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerDataGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FriendsDetailState<out T> {

    data object Loading : FriendsDetailState<Nothing>
    data class Success<T>(val data: T) : FriendsDetailState<T>
    data class Error(val message: String) : FriendsDetailState<Nothing>
    data object Empty : FriendsDetailState<Nothing>

}

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FriendDetailRepository
) : ViewModel() {
    val handle = savedStateHandle.get<String>("handle")

    private val _friendInfoState =
        MutableStateFlow<FriendsDetailState<UserInfo>>(FriendsDetailState.Loading)
    val uiState = _friendInfoState.asStateFlow()

    private val _submissions =
        MutableStateFlow<FriendsDetailState<List<SubmissionDto>>>(FriendsDetailState.Empty)
    val submissions = _submissions.asStateFlow()


    private val _visualizerData =
        MutableStateFlow<FriendsDetailState<VisualizerData>>(FriendsDetailState.Empty)
    val visualizerData = _visualizerData.asStateFlow()

    fun loadData() {
        if (visualizerData.value !is FriendsDetailState.Empty && visualizerData.value !is FriendsDetailState.Error) return

        viewModelScope.launch {
            _submissions.update { FriendsDetailState.Loading }
            _visualizerData.update { FriendsDetailState.Loading }
            try {
                val (ratingHistory, submissions) = coroutineScope {
                    val ratings = async { repository.getFriendRatings(handle ?: "") }
                    val submissions = async { repository.getFriendSubmissions(handle ?: "") }
                    ratings.await() to submissions.await()
                }
                _submissions.update { FriendsDetailState.Success(submissions) }
                _visualizerData.update {
                    FriendsDetailState.Success(
                        VisualizerDataGenerator.getVisualizerData(
                            ratings = ratingHistory,
                            submissions = submissions
                        )
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val message = e.message ?: "Could not load friend activity"
                _submissions.update { FriendsDetailState.Error(message) }
                _visualizerData.update { FriendsDetailState.Error(message) }
            }
        }
    }

    init {
        getFriendInfo()
    }

    fun getFriendInfo() {
        viewModelScope.launch {
            if (!handle.isNullOrEmpty()) {
                _friendInfoState.value = FriendsDetailState.Loading
                try {
                    val friendData = repository.getFriendData(handle)
                    _friendInfoState.value = FriendsDetailState.Success(friendData)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _friendInfoState.value = FriendsDetailState.Error(
                        e.message ?: "Could not load friend profile"
                    )
                }
            } else {
                _friendInfoState.value = FriendsDetailState.Error("Friend handle unavailable")
            }
        }
    }
}

interface FriendDetailRepository {
    suspend fun getFriendData(handle: String): UserInfo
    suspend fun getFriendRatings(handle: String): List<Rating>
    suspend fun getFriendSubmissions(handle: String): List<SubmissionDto>
}

class FriendDetailRepositoryImpl @Inject constructor(
    private val apiService: FriendsApiService
) : FriendDetailRepository {
    override suspend fun getFriendData(handle: String): UserInfo {
        try {
            val friendInfo = coroutineScope {
                async { getFriendInfo(handle) }
            }
            return friendInfo.await()
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getFriendInfo(handle: String): UserInfo {
        val response = apiService.getUsersInfo(handle)
        if (response.status == "OK") {
            return response.result.firstOrNull()
                ?: throw Exception("Public profile unavailable")
        } else {
            throw Exception("Codeforces response: ${response.status.ifBlank { "UNKNOWN" }}")
        }
    }

    override suspend fun getFriendSubmissions(handle: String): List<SubmissionDto> {
        val response = apiService.getUserSubmissions(handle)
        if (response.status == "OK") {
            return response.result
        } else {
            throw Exception("Codeforces submissions unavailable")
        }
    }

    override suspend fun getFriendRatings(handle: String): List<Rating> {
        val response = apiService.getUserRating(handle)
        if (response.status == "OK") {
            return response.result
        } else {
            throw Exception("Codeforces rating history unavailable")
        }
    }

}
