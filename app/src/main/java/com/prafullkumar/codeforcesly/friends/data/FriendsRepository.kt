package com.prafullkumar.codeforcesly.friends.data

import com.prafullkumar.codeforcesly.network.ApiService
import kotlinx.coroutines.flow.first

class FriendsRepository(
    private val friendDao: FriendDao,
) {
    companion object {
        val api = ApiService.getService(FriendsApiService::class.java)
    }

    val allFriends = friendDao.getAllFriends()
    suspend fun addFriend(handle: String, name: String) {
        try {
            val response = api.getUserInfo(handle)
            if (response.status == "OK" && response.result.isNotEmpty()) {
                val userInfo = response.result[0]
                val friend = Friend(
                    handle = userInfo.handle,
                    name = name,
                    rating = userInfo.rating,
                    rank = userInfo.rank ?: "unrated",
                    avatar = userInfo.titlePhoto
                )
                friendDao.insertFriend(friend)
            }
        } catch (e: Exception) {
            // Handle error case - could create a friend with basic info only
            val friend = Friend(handle = handle, name = name)
            friendDao.insertFriend(friend)
        }
    }

    suspend fun deleteFriend(friend: Friend) {
        friendDao.deleteFriend(friend)
    }


    suspend fun refreshFriendsData() {
        try {
            val existingFriends = friendDao.getAllFriends().first()
            val updatedFriends = existingFriends.map { friend ->
                val response = api.getUserInfo(friend.handle)
                if (response.status == "OK" && response.result.isNotEmpty()) {
                    val userInfo = response.result[0]
                    friend.copy(
                        rating = userInfo.rating ?: 0,
                        rank = userInfo.rank ?: "unrated",
                        avatar = userInfo.titlePhoto,
                        lastActive = userInfo.lastOnlineTimeSeconds ?: 0
                    )
                } else friend
            }
            friendDao.insertAllFriends(updatedFriends)
        } catch (e: Exception) {
            // Handle error
        }
    }
}