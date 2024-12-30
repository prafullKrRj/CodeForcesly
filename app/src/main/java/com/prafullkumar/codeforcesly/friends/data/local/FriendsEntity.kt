package com.prafullkumar.codeforcesly.friends.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey val handle: String,
    val name: String,
    val rating: Int = 0,
    val rank: String = "",
    val avatar: String = "",
    @ColumnInfo(name = "last_active") val lastActive: Long = 0,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)