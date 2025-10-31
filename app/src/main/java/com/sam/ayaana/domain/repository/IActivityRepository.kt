package com.sam.ayaana.domain.repository

import com.sam.ayaana.domain.model.Activity
import kotlinx.coroutines.flow.Flow

interface IActivityRepository {
    suspend fun getActivities(): Flow<Result<List<Activity>>>
    suspend fun getFollowRequests(): Flow<Result<List<Activity>>>
    suspend fun approveFollowRequest(userId: String): Result<Boolean>
    suspend fun denyFollowRequest(userId: String): Result<Boolean>
    suspend fun clearActivity(activityId: String): Result<Boolean>
}
