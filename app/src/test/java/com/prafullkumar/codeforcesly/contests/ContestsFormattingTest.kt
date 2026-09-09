package com.prafullkumar.codeforcesly.contests

import com.prafullkumar.codeforcesly.contests.domain.models.contest.Contest
import com.prafullkumar.codeforcesly.contests.ui.formatDuration
import com.prafullkumar.codeforcesly.contests.ui.filterContests
import java.time.Duration
import org.junit.Assert.assertEquals
import org.junit.Test

class ContestsFormattingTest {
    private val contests = listOf(
        Contest(1, "Codeforces Round 1", "CF", "FINISHED", 7200, 1L, null, null),
        Contest(2, "Educational Round", "ICPC", "FINISHED", 7200, 2L, null, null),
    )

    @Test
    fun expiredCountdownDoesNotCountUp() {
        assertEquals("0s", formatDuration(Duration.ofSeconds(-1)))
    }

    @Test
    fun durationUsesCompactDayHourMinuteFormat() {
        assertEquals("1d 2h 3m 4s", formatDuration(Duration.ofSeconds(93_784)))
    }

    @Test
    fun contestSearchMatchesNameAndTypeAndSupportsBlankQuery() {
        assertEquals(listOf(contests[0]), filterContests(contests, "round 1"))
        assertEquals(listOf(contests[1]), filterContests(contests, "icpc"))
        assertEquals(contests, filterContests(contests, "  "))
    }
}
