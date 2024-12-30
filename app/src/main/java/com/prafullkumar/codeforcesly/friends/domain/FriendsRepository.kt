package com.prafullkumar.codeforcesly.friends.domain

import com.prafullkumar.codeforcesly.friends.data.local.Friend
import kotlinx.coroutines.flow.Flow

interface FriendsRepository {

    suspend fun addFriend(handle: String, name: String)
    suspend fun deleteFriend(friend: Friend)
    fun getAllFriends(): Flow<List<Friend>>
    suspend fun refreshFriendsData()
}