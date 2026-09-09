package com.prafullkumar.codeforcesly.problem

import android.app.Application
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDao
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
        problemsApiService: ProblemsApiService,
        prefManager: SharedPrefManager,
        userDao: UserDao
    ): ProblemsRepository {
        return ProblemsRepositoryImpl(context, problemsApiService, prefManager, userDao)
    }
}
