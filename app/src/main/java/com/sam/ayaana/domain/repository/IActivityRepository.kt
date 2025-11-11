package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.toActivity
import com.sam.ayaana.Utils.toActivityEntity
import com.sam.ayaana.data.local.dao.ActivityDao
import com.sam.ayaana.data.remote.api.ActivityApi
import com.sam.ayaana.domain.model.Activity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.sam.ayaana.Utils.Result

interface IActivityRepository {
    suspend fun getActivities(): Flow<Result<List<Activity>>>
    suspend fun getFollowRequests(): Flow<Result<List<Activity>>>
    suspend fun approveFollowRequest(userId: String): Result<Boolean>
    suspend fun denyFollowRequest(userId: String): Result<Boolean>
    suspend fun clearActivity(activityId: String): Result<Boolean>
}

class ActivityRepositoryImpl @Inject constructor(
    private val activityApi: ActivityApi,
    private val activityDao: ActivityDao
) : IActivityRepository {

    override suspend fun getActivities(): Flow<Result<List<Activity>>> = flow {
        emit(Result.Loading)
        try {
            // First try to get from local database
            val localActivities = activityDao.getActivities()
            localActivities.collect { activities ->
                if (activities.isNotEmpty()) {
                    emit(Result.Success(activities.map { it.toActivity() }))
                }
            }

            // Then fetch from API and update local database
            val response = activityApi.getActivities()
            if (response.success && response.data != null) {
                val activityEntities = response.data.map { it.toActivityEntity() }
                activityDao.insertActivities(activityEntities)
                emit(Result.Success(response.data.map { it.toActivity() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load activities"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun getFollowRequests(): Flow<Result<List<Activity>>> = flow {
        emit(Result.Loading)
        try {
            // First try to get from local database
            val localRequests = activityDao.getFollowRequests()
            localRequests.collect { requests ->
                if (requests.isNotEmpty()) {
                    emit(Result.Success(requests.map { it.toActivity() }))
                }
            }

            // Then fetch from API and update local database
            val response = activityApi.getFollowRequests()
            if (response.success && response.data != null) {
                val activityEntities = response.data.map { it.toActivityEntity() }
                activityDao.insertActivities(activityEntities)
                emit(Result.Success(response.data.map { it.toActivity() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load follow requests"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun approveFollowRequest(userId: String): Result<Boolean> {
        return try {
            val response = activityApi.approveFollowRequest(userId)
            if (response.success) {
                // Remove the follow request from local database after approval
                val requests = activityDao.getFollowRequests()
                requests.collect { activities ->
                    activities.find { it.userId == userId }?.let { activity ->
                        activityDao.deleteActivity(activity.id)
                    }
                }
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to approve follow request")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun denyFollowRequest(userId: String): Result<Boolean> {
        return try {
            val response = activityApi.denyFollowRequest(userId)
            if (response.success) {
                // Remove the follow request from local database after denial
                val requests = activityDao.getFollowRequests()
                requests.collect { activities ->
                    activities.find { it.userId == userId }?.let { activity ->
                        activityDao.deleteActivity(activity.id)
                    }
                }
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to deny follow request")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun clearActivity(activityId: String): Result<Boolean> {
        return try {
            val response = activityApi.clearActivity(activityId)
            if (response.success) {
                activityDao.deleteActivity(activityId)
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to clear activity")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }
}
