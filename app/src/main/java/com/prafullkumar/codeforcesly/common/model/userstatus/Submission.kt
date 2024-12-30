package com.prafullkumar.codeforcesly.common.model.userstatus

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Submission(
    @SerialName("author")
    val author: Author,

    @SerialName("contest_id")
    val contestId: Int,

    @SerialName("creation_time_seconds")
    val creationTimeSeconds: Int,

    @SerialName("id")
    val id: Int,

    @SerialName("memory_consumed_bytes")
    val memoryConsumedBytes: Int,

    @SerialName("passed_test_count")
    val passedTestCount: Int,

    @SerialName("points")
    val points: Double,

    @SerialName("points_info")
    val pointsInfo: String,

    @SerialName("problem")
    val problem: Problem,

    @SerialName("programming_language")
    val programmingLanguage: String,

    @SerialName("relative_time_seconds")
    val relativeTimeSeconds: Int,

    @SerialName("testset")
    val testset: String,

    @SerialName("time_consumed_millis")
    val timeConsumedMillis: Int,

    @SerialName("verdict")
    val verdict: String
)