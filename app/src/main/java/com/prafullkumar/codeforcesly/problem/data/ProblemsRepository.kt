package com.prafullkumar.codeforcesly.problem.data

import android.content.Context
import com.prafullkumar.codeforcesly.network.ApiService
import com.prafullkumar.codeforcesly.problem.ProblemsApiService
import com.prafullkumar.codeforcesly.problem.model.ProblemResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ProblemsRepository(
    private val context: Context
) {
    companion object {
        private val apiService = ApiService.getService(ProblemsApiService::class.java)
    }

    suspend fun getAllProblems() = getDummyData(context);

    private suspend fun getFromApi(): ProblemResponse {
        return apiService.getProblems("https://codeforces.com/api/problemset.problems?tags=")
    }

    private suspend fun getDummyData(context: Context): ProblemResponse {
        try {
            val data = loadJsonFromAssets(context, "problemset.json")
            print(data)
            val response = Json.decodeFromString<ProblemResponse>(data);
            return response
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun loadJsonFromAssets(context: Context, fileName: String): String {
        return withContext(Dispatchers.IO) {
            val assetManager = context.assets
            assetManager.open(fileName).bufferedReader().use { it.readText() }
        }
    }
}