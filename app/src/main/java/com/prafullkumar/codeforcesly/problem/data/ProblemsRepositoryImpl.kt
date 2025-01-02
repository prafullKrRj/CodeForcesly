package com.prafullkumar.codeforcesly.problem.data

import android.content.Context
import com.prafullkumar.codeforcesly.problem.domain.ProblemsRepository
import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse
import javax.inject.Inject

class ProblemsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ProblemsApiService
) : ProblemsRepository {

    override suspend fun getAllProblems() = getFromApi()

    private suspend fun getFromApi(): ProblemResponse {
        return apiService.getProblems("https://codeforces.com/api/problemset.problems?tags=")
    }
}