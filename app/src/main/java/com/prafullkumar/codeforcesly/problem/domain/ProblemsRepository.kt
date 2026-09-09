package com.prafullkumar.codeforcesly.problem.domain

import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse

interface ProblemsRepository {

    suspend fun getAllProblems(): ProblemResponse

    suspend fun refreshAllProblems(): ProblemResponse = getAllProblems()

    suspend fun getRecommendedProblems(problems: List<Problem>): List<Problem> = emptyList()

    suspend fun getSolvedProblemKeys(): Set<String> = emptySet()
}
