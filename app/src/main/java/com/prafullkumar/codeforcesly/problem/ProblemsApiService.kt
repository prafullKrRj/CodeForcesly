package com.prafullkumar.codeforcesly.problem

import com.prafullkumar.codeforcesly.problem.model.ProblemResponse


import retrofit2.http.GET
import retrofit2.http.Url

interface ProblemsApiService {

    @GET
    suspend fun getProblems(@Url url: String): ProblemResponse
}
