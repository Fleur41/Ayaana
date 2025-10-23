package com.sam.ayaana.datastore

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class DatastoreRepository @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    val authenticated: Flow<Boolean> = datastoreManager.language

    suspend fun saveIsAuthenticated(authenticated: Boolean) {
        datastoreManager.saveIsAuthenticated(authenticated)
    }
}


