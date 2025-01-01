package com.prafullkumar.codeforcesly.visualizer.domain

import com.prafullkumar.codeforcesly.common.model.userrating.Rating
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto

data class UserData(
    val handle: String,
    val ratings: List<Rating>,
    val submissions: List<SubmissionDto>
)