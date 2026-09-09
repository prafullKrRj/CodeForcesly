package com.prafullkumar.codeforcesly.network

import com.prafullkumar.codeforcesly.contests.data.ContestsApiService
import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import com.prafullkumar.codeforcesly.onBoarding.data.OnBoardingApiService
import com.prafullkumar.codeforcesly.problem.data.ProblemsApiService
import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService
import com.prafullkumar.codeforcesly.visualizer.data.VisualizerApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    @Singleton
    fun providesRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://codeforces.com/api/")
            .client(client)
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

    @Provides
    @Singleton
    fun providesVisualizerApiService(retrofit: Retrofit): VisualizerApiService {
        return retrofit.create(VisualizerApiService::class.java)
    }
}
