package com.prafullkumar.codeforcesly.profile

import android.app.Application
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.profile.profile.ProfileApiService
import com.prafullkumar.codeforcesly.profile.profile.ProfileRepository
import com.prafullkumar.codeforcesly.profile.profile.ProfileRepositoryImpl
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsRepository
import com.prafullkumar.codeforcesly.profile.submissions.SubmissionsRepositoryImpl
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
        context: Application,
        prefManager: SharedPrefManager
    ): ProfileRepository {
        return ProfileRepositoryImpl(context = context, api = api, prefManager)
    }

    @Provides
    @Singleton
    fun providesSharedPrefManager(
        context: Application
    ): SharedPrefManager {
        return SharedPrefManager(context = context)
    }

    @Provides
    @Singleton
    fun providesSubmissionsRepository(
        api: ProfileApiService
    ): SubmissionsRepository {
        return SubmissionsRepositoryImpl(api = api)
    }
}