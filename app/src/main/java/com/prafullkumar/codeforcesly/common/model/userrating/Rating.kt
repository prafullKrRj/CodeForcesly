package com.prafullkumar.codeforcesly.common.model.userrating

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Rating(
    @SerializedName("contestId") val contestId: Int,
    @SerializedName("contestName") val contestName: String,
    @SerializedName("handle") val handle: String,
    @SerializedName("newRating") val newRating: Int,
    @SerializedName("oldRating") val oldRating: Int,
    @SerializedName("rank") val rank: Int,
    @SerializedName("ratingUpdateTimeSeconds") val ratingUpdateTimeSeconds: Int
)