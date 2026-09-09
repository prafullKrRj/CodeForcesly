package com.prafullkumar.codeforcesly.friends.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.friends.data.local.Friend
import com.prafullkumar.codeforcesly.friends.domain.FriendsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddFriendUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendsRepository
) : ViewModel() {


    val allFriends = repository.getAllFriends().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _dialogState = MutableStateFlow(false)
    val dialogState = _dialogState.asStateFlow()
    private val _addFriendState = MutableStateFlow(AddFriendUiState())
    val addFriendState = _addFriendState.asStateFlow()

    fun addFriend(handle: String, name: String) {
        viewModelScope.launch {
            if (_addFriendState.value.isLoading) return@launch
            _addFriendState.value = AddFriendUiState(isLoading = true)
            repository.addFriend(handle.trim(), name.trim())
                .onSuccess {
                    _addFriendState.value = AddFriendUiState()
                    _dialogState.value = false
                }
                .onFailure { error ->
                    _addFriendState.value = AddFriendUiState(
                        error = error.message ?: "Could not verify profile"
                    )
                }
        }
    }

    fun deleteFriend(friend: Friend) {
        viewModelScope.launch {
            repository.deleteFriend(friend)
        }
    }

    fun showDialog() {
        _dialogState.value = true
    }

    fun hideDialog() {
        _dialogState.value = false
        _addFriendState.value = AddFriendUiState()
    }

    fun clearAddFriendError() {
        _addFriendState.update { it.copy(error = null) }
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        refreshFriends()
    }

    fun refreshFriends() {
        viewModelScope.launch {
            _isRefreshing.update {
                true
            }
            repository.refreshFriendsData()
            _isRefreshing.update {
                false
            }
        }
    }
}
