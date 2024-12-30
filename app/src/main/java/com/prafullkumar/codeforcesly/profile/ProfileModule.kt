package com.prafullkumar.codeforcesly.profile

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {
    @Provides
    @Singleton
    fun provideCodeforcesRepository(
        api: ProfileApiService,
        context: Application
    ): ProfileRepository {
        return ProfileRepositoryImpl(context = context, api = api)
    }
}