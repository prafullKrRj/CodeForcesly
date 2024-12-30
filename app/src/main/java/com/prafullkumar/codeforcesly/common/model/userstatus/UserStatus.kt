package com.prafullkumar.codeforcesly.common.model.userstatus

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserStatus(
    @SerializedName("result")
    val result: List<Submission>,
    @SerializedName("status")
    val status: String
)