package com.prafullkumar.codeforcesly.problem.domain

import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse

interface ProblemsRepository {

    suspend fun getAllProblems(): ProblemResponse
}
