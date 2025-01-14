package com.prafullkumar.codeforcesly.contests.domain.models.contestDetails

import com.google.gson.annotations.SerializedName
import com.prafullkumar.codeforcesly.problem.domain.model.Problem

data class ContestResult(
    @SerializedName("contest") val contest: Contest,
    @SerializedName("problems") val problems: List<Problem>,
)