package com.prafullkumar.codeforcesly.common.model.userrating

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserRatingDto(
    @SerializedName("result")
    val result: List<Rating>,
    @SerializedName("status")
    val status: String
)