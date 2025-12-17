// domain/repository/IReelsRepository.kt
package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.AISearchUtils
import com.sam.ayaana.data.local.dao.ReelDao
import com.sam.ayaana.data.local.entity.ReelEntity
import com.sam.ayaana.data.local.entity.SearchHistoryEntity
import com.sam.ayaana.data.remote.api.ReelsApi
import com.sam.ayaana.domain.model.Reel
import com.sam.ayaana.domain.model.SearchQuery
import com.sam.ayaana.Utils.MockReelData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

interface IReelsRepository {
    suspend fun getReels(): List<Reel>
    suspend fun getReelsByCategory(category: String): List<Reel>
    suspend fun searchReels(query: String): List<Reel>
    suspend fun likeReel(reelId: String, liked: Boolean)
    suspend fun saveReel(reelId: String, saved: Boolean)
    fun getSearchHistory(): Flow<List<SearchQuery>>
    suspend fun addToSearchHistory(query: String)
    suspend fun clearSearchHistory()
}

class ReelsRepositoryImpl @Inject constructor(
    private val reelDao: ReelDao,
    private val reelsApi: ReelsApi // Keep the API dependency for DI
) : IReelsRepository {

    // Implement SearchableItem for Reel
    private data class SearchableReel(
        override val title: String,
        override val description: String,
        override val username: String,
        override val tags: List<String>,
        override val category: String
    ) : AISearchUtils.SearchableItem

    override suspend fun getReels(): List<Reel> {
        return try {
            // First try to get from API (will return empty list for mock)
            val apiReels = reelsApi.getReels()

            if (apiReels.isNotEmpty()) {
                // Convert API response to domain models
                val reels = apiReels.map { response ->
                    Reel(
                        id = response.id,
                        title = response.title,
                        description = response.description,
                        videoUrl = response.videoUrl,
                        thumbnailUrl = response.thumbnailUrl,
                        duration = response.duration,
                        likes = response.likes,
                        comments = response.comments,
                        shares = response.shares,
                        userId = response.userId,
                        username = response.username,
                        userProfileImage = response.userProfileImage,
                        tags = response.tags,
                        category = response.category,
                        timestamp = response.timestamp,
                        isLiked = response.isLiked,
                        isSaved = response.isSaved
                    )
                }

                // Save to local database
                reelDao.insertAllReels(reels.map { it.toReelEntity() })
                return reels
            }

            // If API returns empty (mock), try local database
            val localReels = reelDao.getAllReels()
            if (localReels.isNotEmpty()) {
                return localReels.map { it.toReel() }
            }

            // If no data anywhere, use mock data
            val mockReels = MockReelData.generateMockReels()

            // Save mock reels to local database for offline access
            reelDao.insertAllReels(mockReels.map { reel ->
                ReelEntity(
                    id = reel.id,
                    title = reel.title,
                    description = reel.description,
                    videoUrl = reel.videoUrl,
                    thumbnailUrl = reel.thumbnailUrl,
                    duration = reel.duration,
                    likes = reel.likes,
                    comments = reel.comments,
                    shares = reel.shares,
                    userId = reel.userId,
                    username = reel.username,
                    userProfileImage = reel.userProfileImage,
                    tags = reel.tags.joinToString(","),
                    category = reel.category,
                    timestamp = reel.timestamp,
                    isLiked = reel.isLiked,
                    isSaved = reel.isSaved
                )
            })

            mockReels
        } catch (e: Exception) {
            // If API call fails, fall back to local data
            e.printStackTrace()
            val localReels = reelDao.getAllReels()
            if (localReels.isNotEmpty()) {
                localReels.map { it.toReel() }
            } else {
                // Last resort: mock data
                MockReelData.generateMockReels()
            }
        }
    }

    override suspend fun getReelsByCategory(category: String): List<Reel> {
        return try {
            // Try API first
            val apiReels = reelsApi.getReelsByCategory(category)
            if (apiReels.isNotEmpty()) {
                return apiReels.map { response ->
                    Reel(
                        id = response.id,
                        title = response.title,
                        description = response.description,
                        videoUrl = response.videoUrl,
                        thumbnailUrl = response.thumbnailUrl,
                        duration = response.duration,
                        likes = response.likes,
                        comments = response.comments,
                        shares = response.shares,
                        userId = response.userId,
                        username = response.username,
                        userProfileImage = response.userProfileImage,
                        tags = response.tags,
                        category = response.category,
                        timestamp = response.timestamp,
                        isLiked = response.isLiked,
                        isSaved = response.isSaved
                    )
                }
            }

            // Fall back to local database
            val entities = reelDao.getReelsByCategory(category)
            entities.map { entity ->
                entity.toReel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fall back to local database on error
            val entities = reelDao.getReelsByCategory(category)
            entities.map { entity ->
                entity.toReel()
            }
        }
    }

    override suspend fun searchReels(query: String): List<Reel> {
        return try {
            // Try API search first
            val apiResults = reelsApi.searchReels(query)
            if (apiResults.isNotEmpty()) {
                return apiResults.map { response ->
                    Reel(
                        id = response.id,
                        title = response.title,
                        description = response.description,
                        videoUrl = response.videoUrl,
                        thumbnailUrl = response.thumbnailUrl,
                        duration = response.duration,
                        likes = response.likes,
                        comments = response.comments,
                        shares = response.shares,
                        userId = response.userId,
                        username = response.username,
                        userProfileImage = response.userProfileImage,
                        tags = response.tags,
                        category = response.category,
                        timestamp = response.timestamp,
                        isLiked = response.isLiked,
                        isSaved = response.isSaved
                    )
                }
            }

            // Fall back to local AI search
            val allReels = getReels()

            // Convert reels to searchable items
            val searchableItems = allReels.map { reel ->
                SearchableReel(
                    title = reel.title,
                    description = reel.description,
                    username = reel.username,
                    tags = reel.tags,
                    category = reel.category
                )
            }

            // Use AI-powered search
            val searchResults = AISearchUtils.aiSearch(query, searchableItems)

            // Map back to reels
            searchResults.mapNotNull { searchableItem ->
                allReels.find { reel ->
                    reel.title == searchableItem.title &&
                            reel.username == searchableItem.username
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fall back to local AI search on error
            val allReels = getReels()

            val searchableItems = allReels.map { reel ->
                SearchableReel(
                    title = reel.title,
                    description = reel.description,
                    username = reel.username,
                    tags = reel.tags,
                    category = reel.category
                )
            }

            val searchResults = AISearchUtils.aiSearch(query, searchableItems)

            searchResults.mapNotNull { searchableItem ->
                allReels.find { reel ->
                    reel.title == searchableItem.title &&
                            reel.username == searchableItem.username
                }
            }
        }
    }

    override suspend fun likeReel(reelId: String, liked: Boolean) {
        // Update in local database immediately (optimistic update)
        reelDao.updateReelLikeStatus(reelId, liked)

        // Call API in background
        try {
            reelsApi.likeReel(reelId, liked)
        } catch (e: Exception) {
            e.printStackTrace()
            // If API call fails, revert local change or show error
            // For now, we keep the optimistic update
        }
    }

    override suspend fun saveReel(reelId: String, saved: Boolean) {
        // Update in local database immediately (optimistic update)
        reelDao.updateReelSaveStatus(reelId, saved)

        // Call API in background
        try {
            reelsApi.saveReel(reelId, saved)
        } catch (e: Exception) {
            e.printStackTrace()
            // If API call fails, revert local change or show error
        }
    }

    override fun getSearchHistory(): Flow<List<SearchQuery>> {
        return reelDao.getSearchHistory().map { historyEntities ->
            historyEntities.map { entity ->
                SearchQuery(
                    query = entity.query,
                    timestamp = entity.timestamp
                )
            }.sortedByDescending { it.timestamp }
        }
    }

    override suspend fun addToSearchHistory(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return

        val searchHistoryEntity = SearchHistoryEntity(
            id = UUID.randomUUID().toString(),
            query = trimmedQuery,
            timestamp = System.currentTimeMillis()
        )

        try {
            // Check if similar query already exists
            val existingQueries = reelDao.getSearchHistoryList()
            val similarQueryExists = existingQueries.any { existing ->
                existing.query.equals(trimmedQuery, ignoreCase = true) ||
                        existing.query.contains(trimmedQuery, ignoreCase = true) ||
                        trimmedQuery.contains(existing.query, ignoreCase = true)
            }

            if (!similarQueryExists) {
                reelDao.insertSearchQuery(searchHistoryEntity)

                // Keep only last 20 search queries
                val allQueries = reelDao.getSearchHistoryList()
                if (allQueries.size > 20) {
                    val queriesToDelete = allQueries.sortedBy { it.timestamp }
                        .take(allQueries.size - 20)
                    queriesToDelete.forEach { reelDao.deleteSearchQuery(it.id) }
                }
            } else {
                // Update timestamp of existing query
                val existing = existingQueries.firstOrNull {
                    it.query.equals(trimmedQuery, ignoreCase = true)
                }
                existing?.let {
                    reelDao.updateSearchQueryTimestamp(it.id, System.currentTimeMillis())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun clearSearchHistory() {
        try {
            reelDao.clearSearchHistory()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Helper extension function to convert Entity to Domain model
    private fun ReelEntity.toReel(): Reel {
        return Reel(
            id = this.id,
            title = this.title,
            description = this.description,
            videoUrl = this.videoUrl,
            thumbnailUrl = this.thumbnailUrl,
            duration = this.duration,
            likes = this.likes,
            comments = this.comments,
            shares = this.shares,
            userId = this.userId,
            username = this.username,
            userProfileImage = this.userProfileImage,
            tags = this.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            category = this.category,
            timestamp = this.timestamp,
            isLiked = this.isLiked,
            isSaved = this.isSaved
        )
    }

    // Helper extension function to convert Domain model to Entity
    private fun Reel.toReelEntity(): ReelEntity {
        return ReelEntity(
            id = this.id,
            title = this.title,
            description = this.description,
            videoUrl = this.videoUrl,
            thumbnailUrl = this.thumbnailUrl,
            duration = this.duration,
            likes = this.likes,
            comments = this.comments,
            shares = this.shares,
            userId = this.userId,
            username = this.username,
            userProfileImage = this.userProfileImage,
            tags = this.tags.joinToString(","),
            category = this.category,
            timestamp = this.timestamp,
            isLiked = this.isLiked,
            isSaved = this.isSaved
        )
    }
}

