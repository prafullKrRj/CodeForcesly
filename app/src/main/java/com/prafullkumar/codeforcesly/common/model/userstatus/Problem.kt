package com.prafullkumar.codeforcesly.common.model.userstatus

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Problem(
    @SerializedName("contestId")
    val contestId: Int? = 0,

    @SerializedName("index")
    val index: String? = "",

    @SerializedName("name")
    val name: String? = "",

    @SerializedName("points")
    val points: Double? = 0.0,

    @SerializedName("rating")
    val rating: Int? = 0,

    @SerializedName("tags")
    val tags: List<String>? = emptyList(),

    @SerializedName("type")
    val type: String? = ""
)