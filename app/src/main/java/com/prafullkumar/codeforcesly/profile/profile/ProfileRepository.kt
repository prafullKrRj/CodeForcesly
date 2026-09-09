package com.prafullkumar.codeforcesly.profile.profile

import android.content.Context
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfo
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfoResponse
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDao
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserEntity
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.Locale
import javax.inject.Inject

interface ProfileRepository {
    suspend fun getUserInfo(forceRefresh: Boolean = false): UserInfoResponse
    suspend fun getRecentSubmissions(forceRefresh: Boolean = false): UserStatus
}

class ProfileRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api: ProfileApiService,
    private val prefManager: SharedPrefManager,
    private val userDao: UserDao
) : ProfileRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getUserInfo(forceRefresh: Boolean): UserInfoResponse {
        val handle = prefManager.getHandle().orEmpty().trim()
        require(handle.isNotEmpty()) { "No Codeforces handle configured" }
        val cacheFile = profileCacheFile(handle)
        val cached = readCache(cacheFile)
        if (!forceRefresh && cached != null && cacheFile.isFresh()) return cached

        try {
            val response = api.getUserInfo(handle)
            if (response.status != "OK" || response.result.isEmpty()) {
                error("User profile unavailable")
            }
            writeCache(cacheFile, response)
            return response
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            cached?.let { return it }
            userDao.getUserByHandle(handle)?.let {
                return UserInfoResponse(result = listOf(it.toUserInfo()), status = "OK")
            }
            throw e
        }
    }

    override suspend fun getRecentSubmissions(forceRefresh: Boolean): UserStatus {
        val handle = prefManager.getHandle().orEmpty().trim()
        require(handle.isNotEmpty()) { "No Codeforces handle configured" }
        val cacheFile = recentSubmissionsCacheFile(handle)
        val cached = readSubmissionsCache(cacheFile)
        if (!forceRefresh && cached != null && cacheFile.isFresh()) return cached

        return try {
            val response = api.getUserSubmissions(handle = handle, from = 1, count = 5)
            if (response.status != "OK") error("Recent submissions unavailable")
            writeSubmissionsCache(cacheFile, response)
            response
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            cached?.let { return it }
            throw e
        }
    }

    private fun profileCacheFile(handle: String): File {
        return File(context.filesDir, "profile-${safeHandle(handle)}.json")
    }

    private fun recentSubmissionsCacheFile(handle: String): File {
        return File(context.filesDir, "recent-submissions-${safeHandle(handle)}.json")
    }

    private fun safeHandle(handle: String): String =
        handle.lowercase(Locale.US).replace(Regex("[^a-z0-9._-]"), "_")

    private fun File.isFresh(): Boolean =
        System.currentTimeMillis() - lastModified() <= PROFILE_CACHE_TTL_MS

    private fun readCache(file: File): UserInfoResponse? = runCatching {
        if (file.exists()) json.decodeFromString<UserInfoResponse>(file.readText()) else null
    }.getOrNull()

    private fun writeCache(file: File, response: UserInfoResponse) {
        runCatching {
            val temporaryFile = File(file.parentFile, "${file.name}.tmp")
            temporaryFile.writeText(json.encodeToString(response))
            if (!temporaryFile.renameTo(file)) temporaryFile.delete()
        }
    }

    private fun readSubmissionsCache(file: File): UserStatus? = runCatching {
        if (file.exists()) json.decodeFromString<UserStatus>(file.readText()) else null
    }.getOrNull()

    private fun writeSubmissionsCache(file: File, response: UserStatus) {
        runCatching {
            val temporaryFile = File(file.parentFile, "${file.name}.tmp")
            temporaryFile.writeText(json.encodeToString(response))
            if (!temporaryFile.renameTo(file)) temporaryFile.delete()
        }
    }
}

private const val PROFILE_CACHE_TTL_MS = 30 * 60 * 1000L

private fun UserEntity.toUserInfo() = UserInfo(
    avatar = avatar,
    city = null,
    contribution = contribution,
    country = null,
    firstName = null,
    friendOfCount = 0,
    handle = handle,
    lastName = null,
    lastOnlineTimeSeconds = null,
    maxRank = maxRank,
    maxRating = maxRating,
    organization = null,
    rank = rank,
    rating = rating,
    registrationTimeSeconds = 0,
    titlePhoto = titlePhoto
)
