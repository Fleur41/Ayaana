package com.sam.ayaana.data.local.dao

import androidx.room.*
import com.sam.ayaana.data.local.entity.ReelEntity
import com.sam.ayaana.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReelDao {
    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
    suspend fun getAllReels(): List<ReelEntity>

    @Query("SELECT * FROM reels WHERE category = :category ORDER BY timestamp DESC")
    suspend fun getReelsByCategory(category: String): List<ReelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReels(reels: List<ReelEntity>)

    @Query("UPDATE reels SET isLiked = :liked WHERE id = :reelId")
    suspend fun updateReelLikeStatus(reelId: String, liked: Boolean)

    @Query("UPDATE reels SET isSaved = :saved WHERE id = :reelId")
    suspend fun updateReelSaveStatus(reelId: String, saved: Boolean)

    // Search history operations
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM search_history")
    suspend fun getSearchHistoryList(): List<SearchHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchQuery(query: SearchHistoryEntity)

    @Query("UPDATE search_history SET timestamp = :timestamp WHERE id = :id")
    suspend fun updateSearchQueryTimestamp(id: String, timestamp: Long)

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchQuery(id: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()
}

//@Dao
//interface ReelDao {
//    // Reel operations
//    @Query("SELECT * FROM reels ORDER BY timestamp DESC")
//    suspend fun getAllReels(): List<ReelEntity>
//
//    @Query("SELECT * FROM reels WHERE category = :category ORDER BY timestamp DESC")
//    suspend fun getReelsByCategory(category: String): List<ReelEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertReel(reel: ReelEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAllReels(reels: List<ReelEntity>)
//
//    @Query("UPDATE reels SET isLiked = :liked WHERE id = :reelId")
//    suspend fun updateReelLikeStatus(reelId: String, liked: Boolean)
//
//    @Query("UPDATE reels SET isSaved = :saved WHERE id = :reelId")
//    suspend fun updateReelSaveStatus(reelId: String, saved: Boolean)
//
//    @Query("DELETE FROM reels WHERE id = :reelId")
//    suspend fun deleteReel(reelId: String)
//
//    @Query("DELETE FROM reels")
//    suspend fun clearAllReels()
//
//    // Search history operations
//    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
//    fun getSearchHistory(): Flow<List<SearchHistoryEntity>>
//
//    @Query("SELECT * FROM search_history")
//    suspend fun getSearchHistoryList(): List<SearchHistoryEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSearchQuery(query: SearchHistoryEntity)
//
//    @Query("UPDATE search_history SET timestamp = :timestamp WHERE id = :id")
//    suspend fun updateSearchQueryTimestamp(id: String, timestamp: Long)
//
//    @Query("DELETE FROM search_history WHERE id = :id")
//    suspend fun deleteSearchQuery(id: String)
//
//    @Query("DELETE FROM search_history")
//    suspend fun clearSearchHistory()
//
//    // Helper method for bulk delete
//    suspend fun deleteSearchQueries(queries: List<SearchHistoryEntity>) {
//        queries.forEach { deleteSearchQuery(it.id) }
//    }
//}