package com.prafullkumar.codeforcesly.problem.domain.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ProblemStatistic(
    @SerializedName("contestId")
    val contestId: Int,
    @SerializedName("index")
    val index: String,
    @SerializedName("solvedCount")
    val solvedCount: Double? = 0.0
)