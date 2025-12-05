package com.sam.ayaana.di

import com.sam.ayaana.data.service.HashtagGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideHashtagGenerator(): HashtagGenerator {
        return HashtagGenerator()
    }
}