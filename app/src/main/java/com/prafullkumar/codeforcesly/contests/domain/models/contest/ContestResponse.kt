package com.prafullkumar.codeforcesly.contests.domain.models.contest

import com.google.gson.annotations.SerializedName

data class ContestResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val contests: List<Contest>
)