package com.prafullkumar.codeforcesly.onBoarding

import android.app.Application
import androidx.room.Room
import com.prafullkumar.codeforcesly.common.SharedPrefManager
import com.prafullkumar.codeforcesly.onBoarding.data.OnBoardingApiService
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDao
import com.prafullkumar.codeforcesly.onBoarding.data.local.UserDatabase
import com.prafullkumar.codeforcesly.onBoarding.data.repo.OnBoardingRepoImpl
import com.prafullkumar.codeforcesly.onBoarding.domain.OnBoardingRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class OnBoardingModule {

    @Provides
    fun providesUserDao(context: Application): UserDao {
        return Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user_database"
        ).build().userDao()
    }

    @Provides
    fun provideOnBoardingRepository(
        userDao: UserDao,
        apiService: OnBoardingApiService,
        prefManager: SharedPrefManager
    ): OnBoardingRepo {
        return OnBoardingRepoImpl(userDao, prefManager, apiService)
    }
}