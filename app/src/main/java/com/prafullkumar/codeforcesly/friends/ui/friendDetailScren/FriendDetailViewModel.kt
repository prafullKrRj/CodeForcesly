package com.prafullkumar.codeforcesly.friends.ui.friendDetailScren

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val apiService: FriendsApiService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val handle = savedStateHandle.get<String>("handle")

    init {
        Log.d("FriendDetailViewModel", handle ?: "no friend")
    }
}