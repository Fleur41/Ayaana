package com.sam.ayaana.domain.model

data class MenuUiState(
    val isDarkTheme: Boolean = false,
    val isPrivateAccount: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)