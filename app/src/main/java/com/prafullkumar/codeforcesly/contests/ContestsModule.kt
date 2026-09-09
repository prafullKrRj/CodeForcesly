package com.prafullkumar.codeforcesly.contests

import android.app.Application
import com.prafullkumar.codeforcesly.contests.data.ContestsApiService
import com.prafullkumar.codeforcesly.contests.data.ContestsRepositoryImpl
import com.prafullkumar.codeforcesly.contests.domain.ContestsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContestsModule {
    @Provides
    @Singleton
    fun provideContestsRepository(
        context: Application,
        api: ContestsApiService
    ): ContestsRepository = ContestsRepositoryImpl(context, api)
}
