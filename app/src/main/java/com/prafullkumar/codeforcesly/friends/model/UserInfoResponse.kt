package com.prafullkumar.codeforcesly.friends.model

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("result")
    val result: List<Result>,
    @SerializedName("status")
    val status: String
)