package com.prafullkumar.codeforcesly.common.model.userstatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Author(
    @SerialName("contestId")
    val contestId: Int? = 0,

    @SerialName("ghost")
    val ghost: Boolean? = false,

    @SerialName("members")
    val members: List<Member>? = emptyList(),

    @SerialName("participantType")
    val participantType: String? = "",

    @SerialName("room")
    val room: Int? = 0,

    @SerialName("startTimeSeconds")
    val startTimeSeconds: Int? = 0,

    @SerialName("teamId")
    val teamId: Int? = 0,

    @SerialName("teamName")
    val teamName: String? = ""
)