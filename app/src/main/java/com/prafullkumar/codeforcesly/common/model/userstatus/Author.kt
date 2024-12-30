package com.prafullkumar.codeforcesly.common.model.userstatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Author(
    @SerialName("contest_id")
    val contestId: Int,

    @SerialName("ghost")
    val ghost: Boolean,

    @SerialName("members")
    val members: List<Member>,

    @SerialName("participant_type")
    val participantType: String,

    @SerialName("room")
    val room: Int,

    @SerialName("start_time_seconds")
    val startTimeSeconds: Int,

    @SerialName("team_id")
    val teamId: Int,

    @SerialName("team_name")
    val teamName: String
)
