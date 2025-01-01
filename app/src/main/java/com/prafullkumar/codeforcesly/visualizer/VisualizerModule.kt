package com.prafullkumar.codeforcesly.visualizer

import android.app.Application
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.visualizer.data.VisualizerApiService
import com.prafullkumar.codeforcesly.visualizer.data.VisualizerRepositoryImpl
import com.prafullkumar.codeforcesly.visualizer.domain.VisualizerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object VisualizerModule {

    @Provides
    fun provideVisualizerRepository(
        api: VisualizerApiService,
        prefManager: SharedPrefManager, application: Application
    ): VisualizerRepository {
        return VisualizerRepositoryImpl(api, prefManager, application)
    }
}
