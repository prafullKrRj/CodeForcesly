package com.prafullkumar.codeforcesly.friends

import android.app.Application
import androidx.room.Room
import com.prafullkumar.codeforcesly.friends.data.FriendsApiService
import com.prafullkumar.codeforcesly.friends.data.local.FriendDao
import com.prafullkumar.codeforcesly.friends.data.local.FriendsDatabase
import com.prafullkumar.codeforcesly.friends.data.repo.FriendsRepositoryImpl
import com.prafullkumar.codeforcesly.friends.domain.FriendsRepository
import com.prafullkumar.codeforcesly.friends.ui.friendDetailScren.FriendDetailRepository
import com.prafullkumar.codeforcesly.friends.ui.friendDetailScren.FriendDetailRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FriendsModule {

    @Provides
    @Singleton
    fun providesFriendsDatabase(context: Application): FriendsDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = FriendsDatabase::class.java,
            "friends_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providesFriendsDao(friendsDatabase: FriendsDatabase): FriendDao {
        return friendsDatabase.friendDao()
    }

    @Provides
    @Singleton
    fun providedFriendsRepo(
        friendsDao: FriendDao,
        apiService: FriendsApiService
    ): FriendsRepository {
        return FriendsRepositoryImpl(friendsDao, apiService)
    }

    @Provides
    @Singleton
    fun providesFriendDetailRepository(apiService: FriendsApiService): FriendDetailRepository {
        return FriendDetailRepositoryImpl(apiService)
    }
}