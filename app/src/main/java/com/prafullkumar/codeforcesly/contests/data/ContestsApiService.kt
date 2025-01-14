package com.prafullkumar.codeforcesly.contests.data

import com.prafullkumar.codeforcesly.contests.domain.models.contest.ContestResponse
import com.prafullkumar.codeforcesly.contests.domain.models.contestDetails.ParticularContestResponse
import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface ContestsApiService {
    @GET("contest.list")
    suspend fun getContests(): ContestResponse

    @GET
    suspend fun getProblems(@Url url: String): ProblemResponse


    @GET
    suspend fun getParticularContestProblems(
        @Url url: String
    ): ParticularContestResponse
}
