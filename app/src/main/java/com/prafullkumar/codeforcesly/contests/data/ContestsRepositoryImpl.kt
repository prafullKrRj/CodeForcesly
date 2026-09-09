package com.prafullkumar.codeforcesly.contests.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.prafullkumar.codeforcesly.common.runCatchingCancellable
import com.prafullkumar.codeforcesly.contests.domain.ContestsRepository
import com.prafullkumar.codeforcesly.contests.domain.models.contest.ContestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ContestsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api: ContestsApiService
) : ContestsRepository {
    private val gson = Gson()
    private val cacheLifetimeMillis = 15 * 60 * 1000L

    override suspend fun getContests(forceRefresh: Boolean): ContestResponse = withContext(Dispatchers.IO) {
        val cacheFile = File(context.filesDir, "contests-cache.json")
        val cached = readCache(cacheFile)
        if (!forceRefresh && cached != null && cacheFile.isFresh()) return@withContext cached

        runCatchingCancellable {
            api.getContests().also { response ->
                check(response.status.equals("OK", ignoreCase = true)) {
                    "Contest data unavailable"
                }
                writeCache(cacheFile, response)
            }
        }.getOrElse { cached ?: throw it }
    }

    private fun File.isFresh(): Boolean = System.currentTimeMillis() - lastModified() < cacheLifetimeMillis

    private fun readCache(file: File): ContestResponse? = runCatching {
        if (!file.exists()) return@runCatching null
        gson.fromJson<ContestResponse>(file.readText(), object : TypeToken<ContestResponse>() {}.type)
    }.getOrNull()

    private fun writeCache(file: File, response: ContestResponse) {
        runCatching {
            val temporaryFile = File(file.parentFile, "${file.name}.tmp")
            temporaryFile.writeText(gson.toJson(response))
            if (!temporaryFile.renameTo(file)) temporaryFile.delete()
        }
    }
}
