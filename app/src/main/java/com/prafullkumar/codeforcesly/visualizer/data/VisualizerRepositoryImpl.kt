package com.prafullkumar.codeforcesly.visualizer.data

import android.content.Context
import com.prafullkumar.codeforcesly.common.runCatchingCancellable
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.visualizer.domain.UserData
import com.prafullkumar.codeforcesly.visualizer.domain.VisualizerRepository
import java.io.File
import java.util.Locale
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
    override suspend fun getUserData(forceRefresh: Boolean): UserData {
        val handle = prefManager.getHandle() ?: throw Exception("Handle not found")
        if (!forceRefresh) {
            readCachedData(handle, freshOnly = true)?.let { return it }
        }

        val data = runCatchingCancellable {
            coroutineScope {
                val ratingInfo = async {
                    runCatchingCancellable {
                        api.getUserRating(handle).takeIf { it.status == "OK" }?.result
                    }
                }
                val contestInfo = async {
                    runCatchingCancellable {
                        api.getUserStatus(
                            handle = handle,
                            from = 1,
                            count = STATUS_LIMIT
                        ).takeIf { it.status == "OK" }?.result
                    }
                }
                val ratings = ratingInfo.await()
                val submissions = contestInfo.await()
                if (ratings.getOrNull() == null && submissions.getOrNull() == null) {
                    throw Exception("No public stats available")
                }
                UserData(
                    handle = handle,
                    ratings = ratings.getOrNull().orEmpty(),
                    submissions = submissions.getOrNull().orEmpty()
                )
            }
        }.getOrElse { error -> readCachedData(handle) ?: throw error }
        cacheData(handle, data)
        return data
    }

    private fun readCachedData(handle: String, freshOnly: Boolean = false): UserData? {
        val file = cacheFile(handle)
        if (!file.exists()) return null
        if (freshOnly && System.currentTimeMillis() - file.lastModified() > CACHE_TTL_MS) {
            return null
        }
        return runCatching {
            json.decodeFromString(UserData.serializer(), file.readText())
        }.getOrNull()
    }

    private fun cacheData(handle: String, data: UserData) {
        runCatching {
            val file = cacheFile(handle)
            val temporaryFile = File(file.parentFile, "${file.name}.tmp")
            temporaryFile.writeText(json.encodeToString(UserData.serializer(), data))
            if (!temporaryFile.renameTo(file)) temporaryFile.delete()
        }
    }

    private fun cacheFile(handle: String): File {
        val safeHandle = handle.lowercase(Locale.ROOT).map { character ->
            if (character.isLetterOrDigit() || character == '.' || character == '_' || character == '-') {
                character
            } else {
                '_'
            }
        }.joinToString("")
        return File(context.filesDir, "visualizer-$safeHandle.json")
    }

    private companion object {
        const val STATUS_LIMIT = 5_000
        const val CACHE_TTL_MS = 30 * 60 * 1000L
    }
}
