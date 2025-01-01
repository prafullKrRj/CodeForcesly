package com.prafullkumar.codeforcesly.common.model.userstatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmissionDto(
    @SerialName("author")
    val author: Author? = Author(),

    @SerialName("contestId")
    val contestId: Int? = 0,

    @SerialName("creationTimeSeconds")
    val creationTimeSeconds: Long? = 0,

    @SerialName("id")
    val id: Int? = 0,

    @SerialName("memoryConsumedBytes")
    val memoryConsumedBytes: Long? = 0,

    @SerialName("passedTestCount")
    val passedTestCount: Long? = 0,

    @SerialName("points")
    val points: Double? = 0.0,

    @SerialName("pointsInfo")
    val pointsInfo: String? = "",

    @SerialName("problem")
    val problem: Problem? = Problem(),

    @SerialName("programmingLanguage")
    val programmingLanguage: String? = null,

    @SerialName("relativeTimeSeconds")
    val relativeTimeSeconds: Long? = null,

    @SerialName("testset")
    val testset: String? = null,

    @SerialName("timeConsumedMillis")
    val timeConsumedMillis: Long? = null,

    @SerialName("verdict")
    val verdict: String? = null
)