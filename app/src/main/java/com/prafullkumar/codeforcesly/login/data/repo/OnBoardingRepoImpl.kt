package com.prafullkumar.codeforcesly.login.data.repo

import com.prafullkumar.codeforcesly.login.data.OnBoardingApiService
import com.prafullkumar.codeforcesly.login.data.local.UserDao
import com.prafullkumar.codeforcesly.login.data.local.UserEntity
import com.prafullkumar.codeforcesly.login.domain.OnBoardingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OnBoardingRepoImpl @Inject constructor(
    private val userDao: UserDao,
    private val api: OnBoardingApiService
) : OnBoardingRepo {

    override suspend fun fetchAndStoreUser(handle: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
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