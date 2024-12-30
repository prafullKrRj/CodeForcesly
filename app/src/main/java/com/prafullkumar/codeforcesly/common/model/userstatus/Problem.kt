package com.prafullkumar.codeforcesly.common.model.userstatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Problem(
    @SerialName("contest_id")
    val contestId: Int,

    @SerialName("index")
    val index: String,

    @SerialName("name")
    val name: String,

    @SerialName("points")
    val points: Double,

    @SerialName("rating")
    val rating: Int,

    @SerialName("tags")
    val tags: List<String>,

    @SerialName("type")
    val type: String
)
