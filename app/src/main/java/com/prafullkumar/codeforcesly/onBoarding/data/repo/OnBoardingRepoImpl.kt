package com.prafullkumar.codeforcesly.onBoarding.data.repo

import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.onBoarding.data.OnBoardingApiService
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDao
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserEntity
import com.prafullkumar.codeforcesly.onBoarding.domain.OnBoardingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class OnBoardingRepoImpl @Inject constructor(
    private val userDao: UserDao,
    private val prefManager: SharedPrefManager,
    private val api: OnBoardingApiService
) : OnBoardingRepo {

    override suspend fun fetchAndStoreUser(handle: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getUserInfo(handle)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!.result[0].toUser()
                    userDao.insertUser(user)
                    prefManager.setHandle(handle)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Failed to fetch user data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}