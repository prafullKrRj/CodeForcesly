package com.prafullkumar.codeforcesly.onBoarding.data

import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OnBoardingApiService {
    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): Response<UserInfoResponse>
}