package com.prafullkumar.codeforcesly.profile.profile

import android.content.Context
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfoResponse
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
    }
}