package com.prafullkumar.codeforcesly.contests.domain

import com.prafullkumar.codeforcesly.contests.domain.models.contest.ContestResponse

interface ContestsRepository {
    suspend fun getContests(forceRefresh: Boolean = false): ContestResponse
}
