package com.sam.ayaana.domain.repository

interface ISocialAuthRepository{
    suspend fun syncUserWithSocialApi(): Result<Boolean>
    suspend fun getSocialApiToken(): String?
    suspend fun refreshSocialToken(): Result<String>
    suspend fun logoutFromSocialApi(): Result<Boolean>

}