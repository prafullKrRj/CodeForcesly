package com.prafullkumar.codeforcesly.visualizer.data

import com.prafullkumar.codeforcesly.common.model.userrating.UserRatingDto
import com.prafullkumar.codeforcesly.common.model.userstatus.UserStatus
import retrofit2.http.GET
import retrofit2.http.Query

interface VisualizerApiService {

    @GET("user.rating")
    suspend fun getUserRating(
        @Query("handle") handle: String
    ): UserRatingDto

    @GET("user.status")
    suspend fun getUserStatus(
        @Query("handle") handle: String
    ): UserStatus

}