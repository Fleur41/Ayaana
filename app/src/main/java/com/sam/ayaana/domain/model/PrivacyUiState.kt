package com.sam.ayaana.domain.model

data class PrivacyUiState(
    val isAccountPrivate: Boolean = false,
    val closeFriendsEnabled: Boolean = true,
    val crosspostingEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)