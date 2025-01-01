package com.prafullkumar.codeforcesly.friends.data.repo

import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import com.prafullkumar.codeforcesly.friends.data.local.Friend
import com.prafullkumar.codeforcesly.friends.data.local.FriendDao
import com.prafullkumar.codeforcesly.friends.domain.FriendsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(
    private val friendDao: FriendDao,
    private val api: FriendsApiService
) : FriendsRepository {

    override suspend fun addFriend(handle: String, name: String) {
        try {
            val response = api.getUsersInfo(handle)
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
            val friend = Friend(handle = handle, name = name)
            friendDao.insertFriend(friend)
        }
    }

    override suspend fun deleteFriend(friend: Friend) {
        friendDao.deleteFriend(friend)
    }

    override fun getAllFriends(): Flow<List<Friend>> {
        return friendDao.getAllFriends()
    }


    override suspend fun refreshFriendsData() {
        try {
            val existingFriends = friendDao.getAllFriends().first()
            val updatedFriends = existingFriends.map { friend ->
                val response = api.getUsersInfo(friend.handle)
                if (response.status == "OK" && response.result.isNotEmpty()) {
                    val userInfo = response.result[0]
                    friend.copy(
                        rating = userInfo.rating,
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