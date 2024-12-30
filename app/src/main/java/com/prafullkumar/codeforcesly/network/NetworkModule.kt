package com.prafullkumar.codeforcesly.network

import com.prafullkumar.codeforcesly.contests.data.ContestsApiService
import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import com.prafullkumar.codeforcesly.login.data.OnBoardingApiService
import com.prafullkumar.codeforcesly.problem.data.ProblemsApiService
import com.prafullkumar.codeforcesly.profile.ProfileApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://codeforces.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    fun providesContestsApiService(retrofit: Retrofit): ContestsApiService {
        return retrofit.create(ContestsApiService::class.java)
    }
    @Provides
    @Singleton
    fun providesFriendsApiService(retrofit: Retrofit): FriendsApiService {
        return retrofit.create(FriendsApiService::class.java)
    }
    @Provides
    @Singleton
    fun providesOnBoardingApiService(retrofit: Retrofit): OnBoardingApiService {
        return retrofit.create(OnBoardingApiService::class.java)
    }
    @Provides
    @Singleton
    fun providesProblemsApiService(retrofit: Retrofit): ProblemsApiService {
        return retrofit.create(ProblemsApiService::class.java)
    }
    @Provides
    @Singleton
    fun providesProfileApiService(retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

}