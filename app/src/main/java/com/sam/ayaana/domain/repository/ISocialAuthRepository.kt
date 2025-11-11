package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.Result
import com.sam.ayaana.data.remote.api.AuthApi
import com.sam.ayaana.data.remote.model.request.SocialAuthRequest
import com.sam.ayaana.data.remote.model.request.SocialUserRequest
import com.sam.ayaana.datastore.DatastoreRepository
import jakarta.inject.Inject

interface ISocialAuthRepository{
    suspend fun syncUserWithSocialApi(): Result<Boolean>
    suspend fun getSocialApiToken(): String?
    suspend fun refreshSocialToken(): Result<String>
    suspend fun logoutFromSocialApi(): Result<Boolean>
}

class SocialAuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val datastoreRepository: DatastoreRepository
) : ISocialAuthRepository {

    override suspend fun syncUserWithSocialApi(): Result<Boolean> {
        return Result.Error("Social synchronization not implemented")
    }

    override suspend fun getSocialApiToken(): String? {
        return datastoreRepository.getAuthToken()
    }

    override suspend fun refreshSocialToken(): Result<String> {
        return try {
            val response = authApi.refreshToken()
            if (response.success && response.data != null) {
                datastoreRepository.saveAuthToken(response.data.token)
                Result.Success(response.data.token)
            } else {
                Result.Error(response.message ?: "Failed to refresh token")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error during token refresh")
        }
    }

    override suspend fun logoutFromSocialApi(): Result<Boolean> {
        return try {
            val response = authApi.logout()
            if (response.success) {
                // Clear the stored token
                datastoreRepository.clearAuthToken()
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to logout from social API")
            }
        } catch (e: Exception) {
            // Even if API call fails, clear local token
            datastoreRepository.clearAuthToken()
            Result.Success(true)
        }
    }
}