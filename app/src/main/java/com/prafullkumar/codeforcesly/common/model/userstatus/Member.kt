package com.prafullkumar.codeforcesly.common.model.userstatus

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Member(
    @SerializedName("handle")
    val handle: String? = ""
)