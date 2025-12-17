package com.sam.ayaana.domain.model

data class ReelDetailUiState(
    val reel: Reel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)