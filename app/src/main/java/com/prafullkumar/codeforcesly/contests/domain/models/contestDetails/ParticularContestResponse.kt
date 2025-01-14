package com.prafullkumar.codeforcesly.contests.domain.models.contestDetails

import com.google.gson.annotations.SerializedName

data class ParticularContestResponse(
    @SerializedName("result") val result: ContestResult,
    @SerializedName("status") val status: String
)