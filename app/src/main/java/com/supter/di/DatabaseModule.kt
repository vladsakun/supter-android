package com.supter.di

import android.content.Context
import androidx.room.Room
import com.supter.data.db.AppDatabase
import com.supter.data.db.PurchaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase{
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "supter.db"
        ).build()
    }

    @Provides
    fun provideDao(database: AppDatabase): PurchaseDao {
        return database.purchaseDao()
    }
}