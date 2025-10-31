package com.sam.ayaana.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sam.ayaana.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY timestamp DESC")
    fun getActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE type = 'FOLLOW_REQUEST' ORDER BY timestamp DESC")
    fun getFollowRequests(): Flow<List<ActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivity(activityId: String)

    @Query("DELETE FROM activities")
    suspend fun clearActivities()
}