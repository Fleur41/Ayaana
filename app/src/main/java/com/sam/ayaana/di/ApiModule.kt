package com.sam.ayaana.di

import com.sam.ayaana.data.remote.api.ReelsApi
import com.sam.ayaana.data.remote.model.response.ReelResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideReelsApi(): ReelsApi {
        // Return a mock object that implements your ReelsApi interface
        return object : ReelsApi {
            override suspend fun getReels(): List<ReelResponse> = emptyList()
            override suspend fun searchReels(query: String): List<ReelResponse> = emptyList()
            override suspend fun likeReel(reelId: String, liked: Boolean) { /* Do nothing for mock */ }
            override suspend fun saveReel(reelId: String, saved: Boolean) { /* Do nothing for mock */ }
            override suspend fun getReelsByCategory(category: String): List<ReelResponse> = emptyList()
        }
    }
}