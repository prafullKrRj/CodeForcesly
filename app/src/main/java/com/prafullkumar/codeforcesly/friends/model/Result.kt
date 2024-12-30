package com.prafullkumar.codeforcesly.friends.model

import com.google.gson.annotations.SerializedName
import com.prafullkumar.codeforcesly.login.data.UserEntity

data class Result(
    @SerializedName("avatar") val avatar: String,
    @SerializedName("city") val city: String?,
    @SerializedName("contribution") val contribution: Long,
    @SerializedName("country") val country: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("friendOfCount") val friendOfCount: Long,
    @SerializedName("handle") val handle: String,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("lastOnlineTimeSeconds") val lastOnlineTimeSeconds: Long?,
    @SerializedName("maxRank") val maxRank: String?,
    @SerializedName("maxRating") val maxRating: Int,
    @SerializedName("organization") val organization: String?,
    @SerializedName("rank") val rank: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("registrationTimeSeconds") val registrationTimeSeconds: Long,
    @SerializedName("titlePhoto") val titlePhoto: String
) {
    fun toUser(): UserEntity {
        return UserEntity(
            handle = handle,
            rating = rating ?: 0,
            rank = rank ?: "unrated",
            maxRating = maxRating ?: 0,
            maxRank = maxRank ?: "unrated",
            contribution = contribution ?: 0,
            avatar = avatar ?: "",
            titlePhoto = titlePhoto ?: ""
        )
    }
}