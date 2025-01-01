package com.prafullkumar.codeforcesly.visualizer.data

import android.content.Context
import android.util.Log
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.common.model.userrating.UserRatingDto
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import com.prafullkumar.codeforcesly.visualizer.domain.UserData
import com.prafullkumar.codeforcesly.visualizer.domain.VisualizerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class VisualizerRepositoryImpl @Inject constructor(
    private val api: VisualizerApiService,
    private val prefManager: SharedPrefManager,
    private val context: Context
) : VisualizerRepository {

    private val json = Json { ignoreUnknownKeys = true }
    override suspend fun getUserData(): UserData {
        try {
            val handle = prefManager.getHandle() ?: throw Exception("Handle not found")
            val ratingInfo = coroutineScope {
                async { api.getUserRating(handle) }
            }
            val contestInfo = coroutineScope {
                async { api.getUserStatus(handle) }
            }
            return UserData(
                handle = handle,
                ratings = ratingInfo.await().result,
                submissions = contestInfo.await().result
            )
//            return getDataFromAsset().copy(handle = handle)
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getDataFromAsset(): UserData {
        val ratings =
            json.decodeFromString<UserRatingDto>(loadJsonFromAssets(context, "rating.json"))
        val submissions =
            json.decodeFromString<UserStatus>(loadJsonFromAssets(context, "status.json"))
        Log.d("VisualizerRepositoryImpl", "ratings: ${ratings.result}")
        Log.d("VisualizerRepositoryImpl", "submissions: ${submissions.result}")
        return UserData("", ratings.result, submissions.result)
    }

    private suspend fun loadJsonFromAssets(context: Context, fileName: String): String {
        return withContext(Dispatchers.IO) {
            val assetManager = context.assets
            assetManager.open(fileName).bufferedReader().use { it.readText() }
        }
    }
}