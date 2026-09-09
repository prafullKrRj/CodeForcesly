package com.prafullkumar.codeforcesly.problem.data

import android.content.Context
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.common.runCatchingCancellable
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDao
import com.prafullkumar.codeforcesly.problem.domain.ProblemsRepository
import com.prafullkumar.codeforcesly.problem.domain.model.Problem
import com.prafullkumar.codeforcesly.problem.domain.model.ProblemResponse
import com.prafullkumar.codeforcesly.problem.domain.model.Result
import java.io.File
import java.util.Locale
import kotlin.math.abs
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ProblemsRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ProblemsApiService,
    private val prefManager: SharedPrefManager,
    private val userDao: UserDao
) : ProblemsRepository {

    private val json = Json { ignoreUnknownKeys = true }
    private val cacheFile = File(context.filesDir, CACHE_FILE_NAME)

    override suspend fun getAllProblems(): ProblemResponse {
        readCachedProblems(freshOnly = true)?.let { return it }

        return runCatchingCancellable { getFromApi() }.getOrElse { error ->
            readCachedProblems(freshOnly = false) ?: throw error
        }
    }

    override suspend fun refreshAllProblems(): ProblemResponse = getFromApi()

    override suspend fun getRecommendedProblems(problems: List<Problem>): List<Problem> {
        val handle = prefManager.getHandle().orEmpty().trim()
        if (handle.isEmpty() || problems.isEmpty()) return emptyList()

        val submissions = readOrFetchUserStatus(handle) ?: return emptyList()
        val acceptedSubmissions = submissions.result.filter { it.verdict == "OK" }
        val solvedKeys = acceptedSubmissions.mapNotNull { submission ->
            submission.problem?.index?.let { index ->
                problemKey(submission.problem.contestId, index)
            }
        }.toSet()
        val topicWeights = acceptedSubmissions
            .asSequence()
            .flatMap { it.problem?.tags.orEmpty().asSequence() }
            .groupingBy { it }
            .eachCount()
        val targetRating = userDao.getUserByHandle(handle)?.rating?.coerceIn(800, 3500) ?: 1400

        return problems.asSequence()
            .filter { problemKey(it.contestId, it.index) !in solvedKeys }
            .sortedByDescending { problem ->
                val topicScore = problem.tags.orEmpty().sumOf { topicWeights[it] ?: 0 }
                val ratingDistance = abs(
                    (problem.rating ?: targetRating.toDouble()).toInt() - targetRating
                )
                topicScore * 1_000 - ratingDistance
            }
            .take(MAX_RECOMMENDATIONS)
            .toList()
    }

    override suspend fun getSolvedProblemKeys(): Set<String> {
        val handle = prefManager.getHandle().orEmpty().trim()
        if (handle.isEmpty()) return emptySet()
        return readOrFetchUserStatus(handle)
            ?.result
            ?.asSequence()
            ?.filter { it.verdict == "OK" }
            ?.mapNotNull { submission ->
                submission.problem?.index?.let { index ->
                    problemKey(submission.problem.contestId, index)
                }
            }
            ?.toSet()
            ?: emptySet()
    }

    private suspend fun getFromApi(): ProblemResponse {
        val response = apiService.getProblems("https://codeforces.com/api/problemset.problems?tags=")
        if (response.status == "OK") {
            runCatching {
                val temporaryFile = File(cacheFile.parentFile, "${cacheFile.name}.tmp")
                temporaryFile.writeText(
                    json.encodeToString(
                        ListSerializer(Problem.serializer()),
                        response.result.problems,
                    )
                )
                if (!temporaryFile.renameTo(cacheFile)) temporaryFile.delete()
            }
        }
        return response
    }

    private fun readCachedProblems(freshOnly: Boolean): ProblemResponse? {
        if (!cacheFile.exists()) return null
        if (freshOnly && System.currentTimeMillis() - cacheFile.lastModified() > CACHE_TTL_MS) {
            return null
        }

        return runCatching {
            val problems = json.decodeFromString(
                ListSerializer(Problem.serializer()),
                cacheFile.readText(),
            )
            ProblemResponse(
                status = "OK",
                result = Result(problemStatistics = emptyList(), problems = problems),
            )
        }.getOrNull()
    }

    private suspend fun readOrFetchUserStatus(handle: String): UserStatus? {
        val file = statusCacheFile(handle)
        val cached = readStatusCache(file)
        if (cached != null && System.currentTimeMillis() - file.lastModified() <= STATUS_CACHE_TTL_MS) {
            return cached
        }

        return runCatchingCancellable {
            apiService.getUserStatus(handle = handle, from = 1, count = STATUS_LIMIT)
        }.getOrNull()?.also { response ->
            if (response.status == "OK") writeStatusCache(file, response)
        } ?: cached
    }

    private fun statusCacheFile(handle: String): File =
        File(context.filesDir, "problem-recommendations-${safeHandle(handle)}.json")

    private fun safeHandle(handle: String): String =
        handle.lowercase(Locale.US).replace(Regex("[^a-z0-9._-]"), "_")

    private fun readStatusCache(file: File): UserStatus? = runCatching {
        if (file.exists()) json.decodeFromString<UserStatus>(file.readText()) else null
    }.getOrNull()

    private fun writeStatusCache(file: File, response: UserStatus) {
        runCatching {
            val temporaryFile = File(file.parentFile, "${file.name}.tmp")
            temporaryFile.writeText(json.encodeToString(response))
            if (!temporaryFile.renameTo(file)) temporaryFile.delete()
        }
    }

    private fun problemKey(contestId: Int?, index: String): String =
        "${contestId ?: "archive"}-$index"

    private companion object {
        const val CACHE_FILE_NAME = "problemset-cache.json"
        const val CACHE_TTL_MS = 6 * 60 * 60 * 1000L
        const val STATUS_LIMIT = 1_000
        const val STATUS_CACHE_TTL_MS = 30 * 60 * 1000L
        const val MAX_RECOMMENDATIONS = 12
    }
}
