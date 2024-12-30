package com.prafullkumar.codeforcesly.problem.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class ProblemResponse(
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: String
)