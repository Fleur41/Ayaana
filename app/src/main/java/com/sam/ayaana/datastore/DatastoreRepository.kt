package com.sam.ayaana.datastore

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class DatastoreRepository @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    val authenticated: Flow<Boolean> = datastoreManager.language
    // New properties from DatastoreManager
    val authToken: Flow<String?> = datastoreManager.authToken


    suspend fun saveIsAuthenticated(authenticated: Boolean) {
        datastoreManager.saveIsAuthenticated(authenticated)
    }

    // New methods from DatastoreManager
    suspend fun saveAuthToken(token: String) {
        datastoreManager.saveAuthToken(token)
    }

    suspend fun getAuthToken(): String? {
        return datastoreManager.getAuthToken()
    }

    suspend fun clearAuthToken() {
        datastoreManager.clearAuthToken()
    }




}


