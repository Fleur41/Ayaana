package com.sam.ayaana.datastore

import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class DatastoreRepository @Inject constructor(
    private val datastoreManager: DatastoreManager
) {
    val authenticated: Flow<Boolean> = datastoreManager.language
    // New properties from DatastoreManager
    val authToken: Flow<String?> = datastoreManager.authToken
    // Recent searches flow
    val recentSearches: Flow<List<String>> = datastoreManager.recentSearches
    val profileImagePath: Flow<String?> = datastoreManager.profileImagePath
    val theme: Flow<String> = datastoreManager.theme
    val privacySetting: Flow<Boolean> = datastoreManager.privacySetting


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

    // ADDED: Methods for recent searches
    suspend fun removeRecentSearch(search: String) {
        datastoreManager.removeRecentSearch(search)
    }
    // Removed
    suspend fun saveRecentSearches(searches: List<String>) {
        datastoreManager.saveRecentSearches(searches)
    }


//    fun getRecentSearches(): List<String> {
//        return datastoreManager.getRecentSearches()
//    }

    suspend fun clearRecentSearches() {
        datastoreManager.clearRecentSearches()
    }

    suspend fun addRecentSearch(search: String) {
        datastoreManager.addRecentSearch(search)
    }

    suspend fun saveProfileImagePath(path: String){
        datastoreManager.saveProfileImagePath(path)
    }

    suspend fun getProfileImagePath(): String? {
        return datastoreManager.getProfileImagePath()
    }

    suspend fun clearProfileImagePath() {
        datastoreManager.clearProfileImagePath()
    }

    // Theme methods
    suspend fun saveTheme(theme: String) {
        datastoreManager.saveTheme(theme)
    }

//    suspend fun getTheme(): String{
//        return datastoreManager.getTheme()
//    }

    // ADD: Privacy setting methods
    suspend fun savePrivacySetting(isPrivate: Boolean) {
        datastoreManager.savePrivacySetting(isPrivate)
    }

    suspend fun getPrivacySetting(): Boolean {
        return datastoreManager.getPrivacySetting()
    }
}


