package com.sam.ayaana.data.remote.api

import com.sam.ayaana.data.remote.model.response.ReelResponse


interface ReelsApi {
    // These will be implemented when backend is ready
    // For now, they're just placeholders

    suspend fun getReels(): List<ReelResponse> = emptyList()

    suspend fun searchReels(query: String): List<ReelResponse> = emptyList()

    suspend fun likeReel(reelId: String, liked: Boolean) {}

    suspend fun saveReel(reelId: String, saved: Boolean) {}

    suspend fun getReelsByCategory(category: String): List<ReelResponse> = emptyList()
}

