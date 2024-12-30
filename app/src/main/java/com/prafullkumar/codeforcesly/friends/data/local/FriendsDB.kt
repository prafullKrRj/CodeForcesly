package com.prafullkumar.codeforcesly.friends.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Friend::class], version = 1)
abstract class FriendsDatabase : RoomDatabase() {
    abstract fun friendDao(): FriendDao
}