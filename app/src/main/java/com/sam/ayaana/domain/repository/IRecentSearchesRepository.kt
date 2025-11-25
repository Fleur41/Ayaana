package com.sam.ayaana.domain.repository

import android.R.attr.data
import androidx.datastore.dataStore
import com.sam.ayaana.datastore.DatastoreRepository
import com.sam.ayaana.domain.repository.IRecentSearchesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface IRecentSearchesRepository {
    suspend fun saveRecentSearches(searches: List<String>)
    fun getRecentSearches(): Flow<List<String>>
    suspend fun clearRecentSearches()
    suspend fun addRecentSearch(search: String)
    suspend fun removeRecentSearch(search: String)
}

class RecentSearchesRepositoryImpl @Inject constructor(
    private val datastoreRepository: DatastoreRepository
) : IRecentSearchesRepository {

    // UPDATED: Using DataStore for recent searches persistence
    override suspend fun saveRecentSearches(searches: List<String>) {
        datastoreRepository.saveRecentSearches(searches)
    }

    override fun getRecentSearches(): Flow<List<String>> {
        return datastoreRepository.recentSearches
    }

    override suspend fun clearRecentSearches() {
        datastoreRepository.clearRecentSearches()
    }

    override suspend fun addRecentSearch(search: String) {
        datastoreRepository.addRecentSearch(search)
    }

    override suspend fun removeRecentSearch(search: String) {
        val currentSearches = datastoreRepository.recentSearches.first()
        val updatedList = currentSearches.toMutableList()
        updatedList.remove(search)
        datastoreRepository.saveRecentSearches(updatedList)

    }
}