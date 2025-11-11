package com.sam.ayaana.di

import android.content.Context
import androidx.room.Room
import com.sam.ayaana.data.local.database.AyaanaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AyaanaDatabase{
        return  Room.databaseBuilder(
            context, AyaanaDatabase::class.java, AyaanaDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providePostDao(database: AyaanaDatabase) = database.postDao()


    @Provides
    @Singleton
    fun provideUserDao(database: AyaanaDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideActivityDao(database: AyaanaDatabase) = database.activityDao()

}