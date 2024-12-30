package com.prafullkumar.codeforcesly.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private const val BASE_URL = "https://codeforces.com/api/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> getService(service: Class<T>): T {
        return retrofit.create(service)
    }

}