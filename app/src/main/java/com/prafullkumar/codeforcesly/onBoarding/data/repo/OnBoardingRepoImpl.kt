package com.prafullkumar.codeforcesly.onBoarding.data.repo

import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.onBoarding.data.OnBoardingApiService
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDao
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserEntity
import com.prafullkumar.codeforcesly.onBoarding.domain.OnBoardingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject

class OnBoardingRepoImpl @Inject constructor(
    private val userDao: UserDao,
    private val prefManager: SharedPrefManager,
    private val api: OnBoardingApiService
) : OnBoardingRepo {

    override suspend fun fetchAndStoreUser(handle: String): Result<UserEntity> =
        withContext(Dispatchers.IO) {
            try {
                val body = api.getUserInfo(handle)
                val userInfo = body.result.firstOrNull()
                if (body.status == "OK" && userInfo != null) {
                    val user = userInfo.toUser()
                    userDao.insertUser(user)
                    prefManager.setHandle(handle)
                    Result.success(user)
                } else {
                    Result.failure(Exception("Public Codeforces handle not found"))
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: HttpException) {
                Result.failure(Exception("Public Codeforces handle not found"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
