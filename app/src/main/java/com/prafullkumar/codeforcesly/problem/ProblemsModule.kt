package com.prafullkumar.codeforcesly.problem

import android.app.Application
import com.prafullkumar.codeforcesly.problem.data.ProblemsApiService
import com.prafullkumar.codeforcesly.problem.data.ProblemsRepositoryImpl
import com.prafullkumar.codeforcesly.problem.domain.ProblemsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ProblemsModule {

    @Provides
    fun provideProblemsRepository(
        context: Application,
        problemsApiService: ProblemsApiService
    ): ProblemsRepository {
        return ProblemsRepositoryImpl(context, problemsApiService)
    }
}