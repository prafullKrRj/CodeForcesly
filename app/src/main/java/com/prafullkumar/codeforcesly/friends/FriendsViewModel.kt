package com.prafullkumar.codeforcesly.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prafullkumar.codeforcesly.friends.data.Friend
import com.prafullkumar.codeforcesly.friends.data.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: FriendsRepository) : ViewModel() {
    val allFriends = repository.allFriends.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _dialogState = MutableStateFlow(false)
    val dialogState = _dialogState.asStateFlow()

    fun addFriend(handle: String, name: String) {
        viewModelScope.launch {
            repository.addFriend(handle, name)
            _dialogState.value = false
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
    }

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        refreshFriends()
    }

    fun refreshFriends() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.refreshFriendsData()
            _isRefreshing.value = false
        }
    }
}