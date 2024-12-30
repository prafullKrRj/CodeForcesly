package com.prafullkumar.codeforcesly.contests.data

import com.prafullkumar.codeforcesly.contests.domain.ContestResponse
import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface ContestsApiService {
    @GET("contest.list")
    suspend fun getContests(): ContestResponse

    @GET
    suspend fun getProblems(@Url url: String): ProblemResponse
}
