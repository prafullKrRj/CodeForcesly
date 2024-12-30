package com.prafullkumar.codeforcesly.login

import android.app.Application
import androidx.room.Room
import com.prafullkumar.codeforcesly.login.data.OnBoardingApiService
import com.prafullkumar.codeforcesly.login.data.local.UserDao
import com.prafullkumar.codeforcesly.login.data.local.UserDatabase
import com.prafullkumar.codeforcesly.login.data.repo.OnBoardingRepoImpl
import com.prafullkumar.codeforcesly.login.domain.OnBoardingRepo
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
        apiService: OnBoardingApiService
    ): OnBoardingRepo {
        return OnBoardingRepoImpl(userDao, apiService)
    }
}