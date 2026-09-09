package com.prafullkumar.codeforcesly.problem.data

import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ProblemsApiService {

    @GET
    suspend fun getProblems(@Url url: String): ProblemResponse

    @GET("user.status")
    suspend fun getUserStatus(
        @Query("handle") handle: String,
        @Query("from") from: Int,
        @Query("count") count: Int
    ): UserStatus
}
