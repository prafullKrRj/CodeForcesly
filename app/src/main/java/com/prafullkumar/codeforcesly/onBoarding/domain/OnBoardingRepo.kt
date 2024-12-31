package com.prafullkumar.codeforcesly.onBoarding.domain

import com.prafullkumar.codeforcesly.onBoarding.data.local.UserEntity

interface OnBoardingRepo {
    suspend fun fetchAndStoreUser(handle: String): Result<UserEntity>
}