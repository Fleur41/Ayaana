package com.sam.ayaana.presentation.screens.reels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.domain.model.Reel
import com.sam.ayaana.domain.model.ReelsUiState
import com.sam.ayaana.domain.repository.IReelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReelsViewModel @Inject constructor(
    private val reelsRepository: IReelsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReelsUiState())
    val uiState: StateFlow<ReelsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadReels()
    }

    fun loadReels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val reels = reelsRepository.getReels()
                _uiState.update {
                    it.copy(
                        reels = reels,
                        filteredReels = reels,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load reels"
                    )
                }
            }
        }
    }

    fun searchReels(query: String) {
        _searchQuery.value = query

        if (query.isEmpty()) {
            _uiState.update {
                it.copy(filteredReels = it.reels, isSearching = false)
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true) }
            try {
                val searchResults = reelsRepository.searchReels(query)
                _uiState.update {
                    it.copy(
                        filteredReels = searchResults,
                        isSearching = false,
                        searchError = null
                    )
                }

                // Add to search history if we got results
                if (searchResults.isNotEmpty()) {
                    addToSearchHistory(query)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchError = e.message ?: "Search failed"
                    )
                }
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.update {
            it.copy(
                filteredReels = it.reels,
                isSearching = false,
                searchError = null
            )
        }
    }

    fun likeReel(reelId: String) {
        viewModelScope.launch {
            val currentReels = _uiState.value.reels.toMutableList()
            val index = currentReels.indexOfFirst { it.id == reelId }

            if (index != -1) {
                val reel = currentReels[index]
                val updatedReel = reel.copy(
                    likes = if (reel.isLiked) reel.likes - 1 else reel.likes + 1,
                    isLiked = !reel.isLiked
                )
                currentReels[index] = updatedReel

                _uiState.update {
                    it.copy(
                        reels = currentReels,
                        filteredReels = updateFilteredReels(it.filteredReels, updatedReel)
                    )
                }

                reelsRepository.likeReel(reelId, updatedReel.isLiked)
            }
        }
    }

    fun saveReel(reelId: String) {
        viewModelScope.launch {
            val currentReels = _uiState.value.reels.toMutableList()
            val index = currentReels.indexOfFirst { it.id == reelId }

            if (index != -1) {
                val reel = currentReels[index]
                val updatedReel = reel.copy(isSaved = !reel.isSaved)
                currentReels[index] = updatedReel

                _uiState.update {
                    it.copy(
                        reels = currentReels,
                        filteredReels = updateFilteredReels(it.filteredReels, updatedReel)
                    )
                }

                reelsRepository.saveReel(reelId, updatedReel.isSaved)
            }
        }
    }

    // FIXED: Make this non-suspend and handle coroutine internally
    fun addToSearchHistory(query: String) {
        viewModelScope.launch {
            if (query.isNotBlank()) {
                reelsRepository.addToSearchHistory(query)
            }
        }
    }

    fun filterByCategory(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val filtered = reelsRepository.getReelsByCategory(category)
                _uiState.update {
                    it.copy(
                        filteredReels = filtered,
                        selectedCategory = category,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to filter by category: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearFilter() {
        _uiState.update {
            it.copy(
                filteredReels = it.reels,
                selectedCategory = null
            )
        }
    }

    private fun updateFilteredReels(filteredReels: List<Reel>, updatedReel: Reel): List<Reel> {
        return filteredReels.map { reel ->
            if (reel.id == updatedReel.id) updatedReel else reel
        }
    }
}