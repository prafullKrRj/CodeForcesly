package com.prafullkumar.codeforcesly.friends

import android.app.Application
import androidx.room.Room
import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import com.prafullkumar.codeforcesly.friends.data.local.FriendDao
import com.prafullkumar.codeforcesly.friends.data.local.FriendsDatabase
import com.prafullkumar.codeforcesly.friends.data.repo.FriendsRepositoryImpl
import com.prafullkumar.codeforcesly.friends.domain.FriendsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class FriendsModule {

    @Provides
    @Singleton
    fun providesFriendsDao(context: Application): FriendDao {
        return Room.databaseBuilder(
            context = context,
            klass = FriendsDatabase::class.java,
            "friends_database"
        ).build().friendDao()
    }

    @Provides
    @Singleton
    fun providedFriendsRepo(
        friendsDao: FriendDao,
        apiService: FriendsApiService
    ): FriendsRepository {
        return FriendsRepositoryImpl(friendsDao, apiService);
    }
}