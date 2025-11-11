package com.sam.ayaana.data.remote.api

import com.sam.ayaana.data.remote.model.response.ActivityResponse
import com.sam.ayaana.data.remote.model.response.ApiResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivityApi {
    @GET("activities")
    suspend fun getActivities(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<ActivityResponse>>

    @GET("activities/requests")
    suspend fun getFollowRequests(): ApiResponse<List<ActivityResponse>>

    @POST("activities/requests/{userId}/approve")
    suspend fun approveFollowRequest(@Path("userId") userId: String): ApiResponse<Boolean>

    @POST("activities/requests/{userId}/deny")
    suspend fun denyFollowRequest(@Path("userId") userId: String): ApiResponse<Boolean>

    @DELETE("activities/{activityId}")
    suspend fun clearActivity(@Path("activityId") activityId: String): ApiResponse<Boolean>
}