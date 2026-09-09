package com.prafullkumar.codeforcesly.visualizer

import com.prafullkumar.codeforcesly.common.model.userstatus.Problem
import com.prafullkumar.codeforcesly.common.model.userstatus.SubmissionDto
import com.prafullkumar.codeforcesly.common.model.userrating.Rating
import com.prafullkumar.codeforcesly.visualizer.ui.charts.ratingChartBounds
import com.prafullkumar.codeforcesly.visualizer.ui.VisualizerDataGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VisualizerDataGeneratorTest {
    @Test
    fun ratingChartBoundsContainLowAndFlatRatingHistory() {
        val bounds = ratingChartBounds(listOf(394.0, 394.0))

        assertEquals(194.0, bounds?.minValue ?: error("bounds missing"), 0.0)
        assertEquals(594.0, bounds?.maxValue ?: error("bounds missing"), 0.0)
    }

    @Test
    fun ratingChartBoundsAreAbsentForEmptyHistory() {
        assertEquals(null, ratingChartBounds(emptyList()))
    }

    @Test
    fun largeRatingHistoryIsBoundedWithoutDroppingExtremes() {
        val ratings = List(500) { time ->
            rating(
                time = time,
                name = if (time == 499) "Latest" else "Contest $time",
                old = 1200,
                new = when (time) {
                    123 -> 4000
                    499 -> 1699
                    else -> 1200
                },
                rank = 1
            )
        }

        val data = VisualizerDataGenerator.getVisualizerData(emptyList(), ratings)

        assertTrue(data.ratingGraphRating.size <= 480)
        assertEquals(1200.0, data.ratingGraphRating.first(), 0.0)
        assertTrue(4000.0 in data.ratingGraphRating)
        assertEquals(1699.0, data.ratingGraphRating.last(), 0.0)
    }

    @Test
    fun ratingDataIsChronologicalAndIncludesLatestContestContext() {
        val data = VisualizerDataGenerator.getVisualizerData(
            submissions = emptyList(),
            ratings = listOf(
                rating(time = 20, name = "Later", old = 1500, new = 1550, rank = 2),
                rating(time = 10, name = "Earlier", old = 1400, new = 1500, rank = 4)
            )
        )

        assertEquals(listOf(1500.0, 1550.0), data.ratingGraphRating)
        assertEquals("Later", data.latestContestName)
        assertEquals(50, data.latestRatingDelta)
        assertEquals(2, data.latestRank)
    }

    @Test
    fun solvedByIndexCountsUniqueAcceptedProblemsOnly() {
        val submissions = listOf(
            submission(contestId = 1, index = "A", verdict = "WRONG_ANSWER"),
            submission(contestId = 1, index = "A", verdict = "OK"),
            submission(contestId = 1, index = "A", verdict = "OK"),
            submission(contestId = 2, index = "A", verdict = "OK"),
            submission(contestId = 2, index = "B", verdict = "OK")
        )

        val data = VisualizerDataGenerator.getVisualizerData(submissions, emptyList())

        assertEquals(mapOf("A" to 2, "B" to 1), data.indexCounts)
    }

    private fun submission(contestId: Int, index: String, verdict: String) = SubmissionDto(
        contestId = contestId,
        problem = Problem(contestId = contestId, index = index),
        verdict = verdict
    )

    private fun rating(time: Int, name: String, old: Int, new: Int, rank: Int) = Rating(
        contestId = time,
        contestName = name,
        handle = "tourist",
        newRating = new,
        oldRating = old,
        rank = rank,
        ratingUpdateTimeSeconds = time
    )
}
