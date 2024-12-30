package com.prafullkumar.codeforcesly.problem.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Problem(
    @SerializedName("contestId")
    val contestId: Int?,
    @SerializedName("index")
    val index: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("points")
    val points: Double? = 0.0,
    @SerializedName("rating")
    val rating: Double? = 0.0,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("type")
    val type: String?
)