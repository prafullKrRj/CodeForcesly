package com.prafullkumar.codeforcesly.profile.profile

import android.content.Context
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface ProfileRepository {
    suspend fun getUserInfo(): UserInfoResponse
}

class ProfileRepositoryImpl @Inject constructor(
    private val context: Context,
    private val api: ProfileApiService,
    private val prefManager: SharedPrefManager
) : ProfileRepository {

    override suspend fun getUserInfo(): UserInfoResponse {
        try {
            val response = api.getUserInfo(prefManager.getHandle() ?: "")
            return response
        } catch (e: Exception) {
            throw e
        }
//        return getDummyData()
    }

    private suspend fun getDummyData(): UserInfoResponse {
        try {
            val data = loadJsonFromAssets(context, "user.json")
            print(data)
            val response = Json.decodeFromString<UserInfoResponse>(data)
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