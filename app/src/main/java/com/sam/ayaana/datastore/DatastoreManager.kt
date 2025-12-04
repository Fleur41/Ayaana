package com.sam.ayaana.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_preferences")

class DatastoreManager @Inject constructor(
    @ApplicationContext private val context: Context
){
    //val authenticatedKey = booleanPreferencesKey("authenticated")
    // Keys for different types of data
    private val authenticatedKey = booleanPreferencesKey("authenticated")
    private val authTokenKey = stringPreferencesKey("auth_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val languageKey = booleanPreferencesKey("language")
    private val themeKey = stringPreferencesKey("app_theme")
    private val recentSearchesKey = stringSetPreferencesKey("recent_searches")
    // Profile Image PathKey
    private val profileImagePathKey = stringPreferencesKey("profile_image_path")
    private val privacySettingKey = booleanPreferencesKey("privacy_setting")
    // Flow for language
    val language: Flow<Boolean> = context.dataStore.data.map{ preference ->
        preference[authenticatedKey] ?: false
    }


    // Flow for auth token
    val authToken: Flow<String?> = context.dataStore.data.map { preference ->
        preference[authTokenKey]
    }

    // Flow for recent searches
    val recentSearches: Flow<List<String>> = context.dataStore.data.map { preference ->
        preference[recentSearchesKey]?.toList() ?: emptyList()
    }

    // Get profile image path
    val profileImagePath: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[profileImagePathKey]
    }

    // Theme flow
    val theme: Flow<String> = context.dataStore.data.map { preference ->
        preference[themeKey] ?: "light"
    }

    // Privacy setting flow
    val privacySetting: Flow<Boolean> = context.dataStore.data.map { preference ->
        preference[privacySettingKey] ?: false
    }

    suspend fun saveIsAuthenticated(authenticated: Boolean) {
        context.dataStore.edit { preference ->
            preference[authenticatedKey] = authenticated
        }
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preference ->
            preference[authTokenKey] = token
            preference[authenticatedKey] = true
        }
    }


    suspend fun getAuthToken(): String? {
        return context.dataStore.data.map { preference ->
            preference[authTokenKey]
        }.first()
    }

    suspend fun clearAuthToken() {
        context.dataStore.edit { preference ->
            preference.remove(authTokenKey)
            preference[authenticatedKey] = false
        }
    }

    // Methods for recent searches management
    suspend fun clearRecentSearches() {
        context.dataStore.edit { preference ->
            preference.remove(recentSearchesKey)
        }
    }

    suspend fun saveRecentSearches(searches: List<String>) {
        context.dataStore.edit { preference ->
            preference[recentSearchesKey] = searches.toSet()
        }
    }

//    private suspend fun saveRecentSearchesInternal(searches: List<String>) {
//        context.dataStore.edit { preference ->
//            preference[recentSearchesKey] = searches.toSet()
//        }
//    }

    suspend fun addRecentSearch(search: String) {
        val currentSearches = context.dataStore.data.map { preference ->
            preference[recentSearchesKey]?.toList() ?: emptyList()
        }.first()

        // Remove if exists to avoid duplicates and add to beginning
        val updatedList = mutableListOf(search)
        updatedList.addAll(currentSearches.filter { it != search })

        // Keep only last 10 searches
        val finalList = updatedList.take(10)

        // NOW USING: saveRecentSearchesInternal
        saveRecentSearches(finalList)
    }
//    suspend fun addRecentSearch(search: String) {
//        context.dataStore.edit { preference ->
//            val currentSearches = preference[recentSearchesKey]?.toMutableSet() ?: mutableSetOf()
//            // Remove if exists to avoid duplicates
//            currentSearches.remove(search)
//            // Add to beginning (by converting to list and back)
//            val updatedList = mutableListOf(search)
//            updatedList.addAll(currentSearches.toList())
//            // Keep only last 10 searches
//            preference[recentSearchesKey] = updatedList.take(10).toSet()
//        }
//    }

    // ADDED: Method to remove a single recent search
    suspend fun removeRecentSearch(search: String) {
        val currentSearches = context.dataStore.data.map { preference ->
            preference[recentSearchesKey]?.toList() ?: emptyList()
        }.first()

        val updatedList = currentSearches.filter { it != search }
        // NOW USING: saveRecentSearchesInternal
        saveRecentSearches(updatedList)
    }

//    suspend fun removeRecentSearch(search: String) {
//        context.dataStore.edit { preference ->
//            val currentSearches = preference[recentSearchesKey]?.toMutableSet() ?: mutableSetOf()
//            currentSearches.remove(search)
//            preference[recentSearchesKey] = currentSearches
//        }
//    }

    //New
    // Save profile image path
    suspend fun saveProfileImagePath(path: String) {
        context.dataStore.edit { preference ->
            preference[profileImagePathKey] = path
        }
    }

    //  Get profile image path
    suspend fun getProfileImagePath(): String? {
        return context.dataStore.data.map {preferences ->
            preferences[profileImagePathKey]
        }.first()
    }

    // Clear profile image path
    suspend fun clearProfileImagePath() {
        context.dataStore.edit { preferences ->
            preferences.remove(profileImagePathKey)
        }
    }

    // Theme methods
    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preference ->
            preference[themeKey] = theme
        }
    }

//    suspend fun getTheme(): String{
//        return context.dataStore.data.map { preference ->
//            preference[themeKey] ?: "light"
//        }.first()
//    }

    // Privacy setting methods
    suspend fun savePrivacySetting(isPrivate: Boolean) {
        context.dataStore.edit { preference ->
            preference[privacySettingKey] = isPrivate
        }
    }

    suspend fun getPrivacySetting(): Boolean {
        return context.dataStore.data.map { preference ->
            preference[privacySettingKey] ?: false
        }.first()
    }
}