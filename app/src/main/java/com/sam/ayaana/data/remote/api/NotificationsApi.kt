package com.sam.ayaana.data.remote.api

import retrofit2.http.DELETE
import com.sam.ayaana.data.remote.model.response.FollowRequestResponse
import com.sam.ayaana.data.remote.model.response.NotificationResponse
import com.sam.ayaana.data.remote.model.response.SuggestedUserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsApi {

    // Get all notifications for current user
    @GET("notifications")
    suspend fun getNotifications(): Response<List<NotificationResponse>>

    // Mark notification as read
    @POST("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): Response<Unit>

    // Get follow requests
    @GET("follow-requests")
    suspend fun getFollowRequests(): Response<List<FollowRequestResponse>>

    // Accept follow request
    @POST("follow-requests/{id}/accept")
    suspend fun acceptFollowRequest(@Path("id") id: String): Response<Unit>

    // Delete/Decline follow request
    @DELETE("follow-requests/{id}")
    suspend fun deleteFollowRequest(@Path("id") id: String): Response<Unit>

    // Get suggested users to follow
    @GET("suggested-users")
    suspend fun getSuggestedUsers(
        @Query("limit") limit: Int = 10
    ): Response<List<SuggestedUserResponse>>

    // Follow a suggested user
    @POST("users/{userId}/follow")
    suspend fun followUser(@Path("userId") userId: String): Response<Unit>

    // Dismiss a suggested user (don't show again)
    @POST("suggested-users/{id}/dismiss")
    suspend fun dismissSuggestedUser(@Path("id") id: String): Response<Unit>
}