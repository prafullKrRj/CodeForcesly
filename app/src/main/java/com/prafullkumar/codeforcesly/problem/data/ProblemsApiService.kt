package com.prafullkumar.codeforcesly.problem.data

import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse


import retrofit2.http.GET
import retrofit2.http.Url

interface ProblemsApiService {

    @GET
    suspend fun getProblems(@Url url: String): ProblemResponse
}
