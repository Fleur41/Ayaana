package com.sam.ayaana.domain.repository

import com.sam.ayaana.data.service.HashtagGenerator
import com.sam.ayaana.domain.model.Hashtag
import com.sam.ayaana.domain.model.HashtagRequest
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow



interface IHashtagRepository {
    suspend fun generateHashtags(request: HashtagRequest): Result<List<Hashtag>>
    fun getTrendingHashtags(): Flow<List<Hashtag>>
}

class HashtagRepositoryImpl @Inject constructor(
    private val hashtagGenerator: HashtagGenerator
) : IHashtagRepository {

    override suspend fun generateHashtags(request: HashtagRequest): Result<List<Hashtag>> {
        return try {
            val text = request.text ?: ""
            val hashtags = hashtagGenerator.generateHashtagsFromText(text)
            Result.success(hashtags.take(request.maxResults))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTrendingHashtags(): Flow<List<Hashtag>> = flow {
        emit(hashtagGenerator.getTrendingHashtags())
    }
}
