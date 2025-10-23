package com.sam.ayaana.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_preferences")

class DatastoreManager @Inject constructor(
    @ApplicationContext private val context: Context
){
    val authenticatedKey = booleanPreferencesKey("authenticated")

    val language: Flow<Boolean> = context.dataStore.data.map{ preference ->
        preference[authenticatedKey] ?: false
    }

    suspend fun saveIsAuthenticated(authenticated: Boolean) {
        context.dataStore.edit { preference ->
            preference[authenticatedKey] = authenticated
        }
    }
}