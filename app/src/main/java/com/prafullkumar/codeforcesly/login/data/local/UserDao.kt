package com.prafullkumar.codeforcesly.login.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(userEntity: UserEntity)

    @Query("SELECT * FROM users WHERE handle = :handle")
    suspend fun getUserByHandle(handle: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Delete
    suspend fun deleteUser(userEntity: UserEntity)

    @Query("DELETE FROM users WHERE handle = :handle")
    suspend fun deleteUserByHandle(handle: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}