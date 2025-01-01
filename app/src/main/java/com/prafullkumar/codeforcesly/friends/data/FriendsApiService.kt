package com.prafullkumar.codeforcesly.friends.data

import com.prafullkumar.codeforcesly.common.model.userinfo.UserInfoResponse
import com.prafullkumar.codeforcesly.common.model.userrating.UserRatingDto
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import retrofit2.http.GET
import retrofit2.http.Query

interface FriendsApiService {
    @GET("user.info")
    suspend fun getUsersInfo(@Query("handles") handle: String): UserInfoResponse


    @GET("user.status")
    suspend fun getUserSubmissions(@Query("handle") handle: String): UserStatus

    @GET("user.rating")
    suspend fun getUserRating(@Query("handle") handle: String): UserRatingDto
}