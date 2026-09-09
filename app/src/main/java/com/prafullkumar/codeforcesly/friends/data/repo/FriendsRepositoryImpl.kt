package com.prafullkumar.codeforcesly.friends.data.repo

import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import com.prafullkumar.codeforcesly.friends.data.local.Friend
import com.prafullkumar.codeforcesly.friends.data.local.FriendDao
import com.prafullkumar.codeforcesly.friends.domain.FriendsRepository
import com.prafullkumar.codeforcesly.common.runCatchingCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(
    private val friendDao: FriendDao,
    private val api: FriendsApiService
) : FriendsRepository {

    override suspend fun addFriend(handle: String, name: String): Result<Unit> {
        try {
            val response = api.getUsersInfo(handle)
            val userInfo = response.result.firstOrNull()
            if (response.status != "OK" || userInfo == null) {
                return Result.failure(Exception("Codeforces handle not found"))
            }
            val friend = Friend(
                handle = userInfo.handle,
                name = name,
                rating = userInfo.rating,
                rank = userInfo.rank ?: "unrated",
                avatar = userInfo.titlePhoto,
                lastActive = userInfo.lastOnlineTimeSeconds ?: 0
            )
            friendDao.insertFriend(friend)
            return Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return Result.failure(Exception("Could not verify profile", e))
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
            if (existingFriends.isEmpty()) return

            val usersByHandle = coroutineScope {
                existingFriends
                    .chunked(MAX_HANDLES_PER_REQUEST)
                    .map { batch ->
                        async {
                            runCatchingCancellable {
                                api.getUsersInfo(batch.joinToString(";") { it.handle })
                                    .takeIf { it.status == "OK" }
                                    ?.result
                                    .orEmpty()
                            }.getOrDefault(emptyList())
                        }
                    }
                    .awaitAll()
                    .flatten()
                    .associateBy { it.handle }
            }

            val updatedFriends = existingFriends.map { friend ->
                usersByHandle[friend.handle]?.let { userInfo ->
                    friend.copy(
                        rating = userInfo.rating,
                        rank = userInfo.rank ?: "unrated",
                        avatar = userInfo.titlePhoto,
                        lastActive = userInfo.lastOnlineTimeSeconds ?: 0
                    )
                } ?: friend
            }
            friendDao.insertAllFriends(updatedFriends)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // Handle error
        }
    }

    private companion object {
        const val MAX_HANDLES_PER_REQUEST = 50
    }
}
