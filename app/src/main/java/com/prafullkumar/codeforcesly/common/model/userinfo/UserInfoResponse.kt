package com.prafullkumar.codeforcesly.common.model.userinfo

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    @SerializedName("result")
    val result: List<UserInfo>,
    @SerializedName("status")
    val status: String
)