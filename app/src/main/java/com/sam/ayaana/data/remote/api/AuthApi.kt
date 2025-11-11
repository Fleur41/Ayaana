package com.sam.ayaana.data.remote.api

import com.sam.ayaana.data.remote.model.request.SocialAuthRequest
import com.sam.ayaana.data.remote.model.request.SocialUserRequest
import com.sam.ayaana.data.remote.model.response.ApiResponse
import com.sam.ayaana.data.remote.model.response.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi{
    @POST("auth/sync")
    suspend fun syncUser(@Body request: SocialAuthRequest): ApiResponse<AuthResponse>

    @POST("auth/register")
    suspend fun createUser(@Body request: SocialUserRequest): ApiResponse<AuthResponse>

    @POST("auth/refresh")
    suspend fun refreshToken(): ApiResponse<AuthResponse>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Boolean>

}