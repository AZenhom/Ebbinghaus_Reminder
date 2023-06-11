package com.ahmedzenhom.ebbinghaus.data.di

import android.content.Context
import androidx.room.Room
import com.ahmedzenhom.ebbinghaus.data.db.AppDatabase
import com.ahmedzenhom.ebbinghaus.data.db.EventDao
import com.ahmedzenhom.ebbinghaus.data.db.EventSlotsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CacheModule {
    @Singleton
    @Provides
    fun getRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "Ebbinghaus.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun getAreaDao(database: AppDatabase): EventDao {
        return database.eventDao()
    }

    @Provides
    fun getCityDao(database: AppDatabase): EventSlotsDao {
        return database.eventSlotsDao()
    }
}