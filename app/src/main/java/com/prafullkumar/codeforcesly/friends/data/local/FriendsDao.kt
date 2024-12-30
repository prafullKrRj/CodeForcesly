package com.prafullkumar.codeforcesly.friends.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends ORDER BY added_at DESC")
    fun getAllFriends(): Flow<List<Friend>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFriends(friends: List<Friend>)

    @Query("DELETE FROM friends")
    suspend fun deleteAllFriends()

    @Delete
    suspend fun deleteFriend(friend: Friend)
}