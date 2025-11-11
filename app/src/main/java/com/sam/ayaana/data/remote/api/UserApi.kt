package com.sam.ayaana.data.remote.api

import androidx.room.Query
import com.sam.ayaana.data.remote.model.response.ApiResponse
import com.sam.ayaana.data.remote.model.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi{
    @GET("users/me")
    suspend fun getCurrentUser(): ApiResponse<UserResponse>

    @GET("users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: String
    ): ApiResponse<UserResponse>

    @GET("users/search")
    suspend fun searchUsers(
        @Path("q") query: String
    ): ApiResponse<List<UserResponse>>

    @POST("users/{userId}/follow")
    suspend fun followUser(
        @Path("userId") userId: String
    ): ApiResponse<Boolean>

    @DELETE("users/{userId}/follow")
    suspend fun unfollowUser(
        @Path("userId") userId: String
    ): ApiResponse<Boolean>

    @GET("users/{userId}/followers")
    suspend fun getFollowers(
        @Path("userId") userId: String
    ): ApiResponse<List<UserResponse>>

    @GET("users/{userId}/following")
    suspend fun getFollowing(
        @Path("userId") userId: String
    ): ApiResponse<List<UserResponse>>

    @PUT("users/profile")
    suspend fun updateProfile(
        @Body user: UserResponse
    ): ApiResponse<UserResponse>
}