package com.prafullkumar.codeforcesly.friends.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Friend::class], version = 1)
abstract class FriendsDatabase : RoomDatabase() {
    abstract fun friendDao(): FriendDao

    companion object {
        @Volatile
        private var INSTANCE: FriendsDatabase? = null

        fun getDatabase(context: Context): FriendsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FriendsDatabase::class.java,
                    "codeforces_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}