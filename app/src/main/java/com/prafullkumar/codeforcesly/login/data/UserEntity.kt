package com.prafullkumar.codeforcesly.login.data

import androidx.room.Entity
import androidx.room.PrimaryKey


// Add this extension function for better null handling in case you need it
fun UserEntity.toSafeUser(): UserEntity {
    return UserEntity(
        handle = handle,
        rating = rating.takeIf { it >= 0 } ?: 0,
        rank = rank.takeIf { it.isNotBlank() } ?: "unrated",
        maxRating = maxRating.takeIf { it >= 0 } ?: 0,
        maxRank = maxRank.takeIf { it.isNotBlank() } ?: "unrated",
        contribution = contribution,
        avatar = avatar.takeIf { it.isNotBlank() } ?: "",
        titlePhoto = titlePhoto.takeIf { it.isNotBlank() } ?: ""
    )
}




@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val handle: String,
    val rating: Int,
    val rank: String,
    val maxRating: Int,
    val maxRank: String,
    val contribution: Long,
    val avatar: String,
    val titlePhoto: String
)
