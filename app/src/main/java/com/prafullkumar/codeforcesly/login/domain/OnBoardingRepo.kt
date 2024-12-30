package com.prafullkumar.codeforcesly.login.domain

import com.prafullkumar.codeforcesly.login.data.local.UserEntity

interface OnBoardingRepo {
    suspend fun fetchAndStoreUser(handle: String): Result<UserEntity>
}