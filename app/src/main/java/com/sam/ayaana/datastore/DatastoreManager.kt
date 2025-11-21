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
    private val recentSearchesKey = stringSetPreferencesKey("recent_searches")

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

    // ADDED: Methods for recent searches management
    suspend fun saveRecentSearches(searches: List<String>) {
        context.dataStore.edit { preference ->
            preference[recentSearchesKey] = searches.toSet()
        }
    }

//    suspend fun getRecentSearches(): List<String> {
//        return context.dataStore.data.map { preference ->
//            preference[recentSearchesKey]?.toList() ?: emptyList()
//        }.first()
//    }

    suspend fun clearRecentSearches() {
        context.dataStore.edit { preference ->
            preference.remove(recentSearchesKey)
        }
    }

    suspend fun addRecentSearch(search: String) {
        context.dataStore.edit { preference ->
            val currentSearches = preference[recentSearchesKey]?.toMutableSet() ?: mutableSetOf()
            // Remove if exists to avoid duplicates
            currentSearches.remove(search)
            // Add to beginning (by converting to list and back)
            val updatedList = mutableListOf(search)
            updatedList.addAll(currentSearches.toList())
            // Keep only last 10 searches
            preference[recentSearchesKey] = updatedList.take(10).toSet()
        }
    }
}