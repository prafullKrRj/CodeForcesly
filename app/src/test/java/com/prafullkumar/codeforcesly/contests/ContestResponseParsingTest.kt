package com.prafullkumar.codeforcesly.contests

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prafullkumar.codeforcesly.contests.ui.normalizeContestProblems
import com.prafullkumar.codeforcesly.contests.domain.models.contest.ContestResponse
import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContestResponseParsingTest {
    @Test
    fun parsesOfficialContestListShapeWithoutParticipantCount() {
        val response = Gson().fromJson<ContestResponse>(
            """
            {
              "status": "OK",
              "result": [
                {
                  "id": 2261,
                  "name": "Codeforces Round",
                  "type": "CF",
                  "phase": "BEFORE",
                  "durationSeconds": 7200,
                  "startTimeSeconds": 1792247700,
                  "relativeTimeSeconds": -100
                }
              ]
            }
            """.trimIndent(),
            object : TypeToken<ContestResponse>() {}.type,
        )

        assertEquals("OK", response.status)
        assertEquals(1, response.contests.orEmpty().size)
        assertNull(response.contests.orEmpty().single().participants)
    }

    @Test
    fun contestProblemsNormalizeMissingTagsBeforeRendering() {
        val problem = Problem(
            contestId = 2260,
            index = "A",
            name = "Monocarp's Contest",
            type = "PROGRAMMING",
        )

        assertEquals(emptyList<String>(), normalizeContestProblems(listOf(problem)).single().tags)
    }
}
