package com.prafullkumar.codeforcesly.login.data

import com.prafullkumar.codeforcesly.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    companion object {
        val api = ApiService.getService(OnBoardingApiService::class.java)
    }

    suspend fun fetchAndStoreUser(handle: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUserInfo(handle)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.result[0].toUser()
                userDao.insertUser(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to fetch user data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}