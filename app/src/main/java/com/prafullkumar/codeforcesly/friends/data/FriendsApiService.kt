package com.prafullkumar.codeforcesly.friends.data

import com.prafullkumar.codeforcesly.friends.model.UserInfoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FriendsApiService {
    @GET("user.info")
    suspend fun getUserInfo(@Query("handles") handle: String): UserInfoResponse
}