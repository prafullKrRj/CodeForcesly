package com.prafullkumar.codeforcesly.visualizer.data

import android.content.Context
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.visualizer.domain.UserData
import com.prafullkumar.codeforcesly.visualizer.domain.VisualizerRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        } catch (e: Exception) {
            throw e
        }
    }
}