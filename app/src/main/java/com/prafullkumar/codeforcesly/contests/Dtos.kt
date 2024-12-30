package com.prafullkumar.codeforcesly.contests

import com.google.gson.annotations.SerializedName

data class ContestResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("result")
    val contests: List<Contest>
)

data class Contest(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("phase")
    val phase: String,
    @SerializedName("durationSeconds")
    val durationSeconds: Int,
    @SerializedName("startTimeSeconds")
    val startTimeSeconds: Long,
    @SerializedName("relativeTimeSeconds")
    val relativeTimeSeconds: Long?,
    @SerializedName("participants")
    val participants: Int
)