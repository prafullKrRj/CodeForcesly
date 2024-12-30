package com.prafullkumar.codeforcesly.problem.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Result(
    @SerializedName("problemStatistics")
    val problemStatistics: List<ProblemStatistic>,
    @SerializedName("problems")
    val problems: List<Problem>
)