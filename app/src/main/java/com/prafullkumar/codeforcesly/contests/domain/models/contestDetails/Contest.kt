package com.prafullkumar.codeforcesly.contests.domain.models.contestDetails

import com.google.gson.annotations.SerializedName

data class Contest(
    @SerializedName("durationSeconds") val durationSeconds: Int? = 0,
    @SerializedName("frozen") val frozen: Boolean? = false,
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("name") val name: String? = "",
    @SerializedName("phase") val phase: String? = "",
    @SerializedName("relativeTimeSeconds") val relativeTimeSeconds: Int? = 0,
    @SerializedName("startTimeSeconds") val startTimeSeconds: Int? = 0,
    @SerializedName("type") val type: String? = ""
)