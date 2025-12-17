package com.sam.ayaana.domain.model


data class ReelsUiState(
    val reels: List<Reel> = emptyList(),
    val filteredReels: List<Reel> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val searchError: String? = null,
    val selectedCategory: String? = null
)