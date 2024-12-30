package com.prafullkumar.codeforcesly.contests.domain

import com.google.gson.annotations.SerializedName

data class ContestResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val contests: List<Contest>
)