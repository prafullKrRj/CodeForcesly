package com.prafullkumar.codeforcesly.problem.model

import com.google.gson.annotations.SerializedName

data class ProblemResponse(
    @SerializedName("result")
    val result: Result,
    @SerializedName("status")
    val status: String
)