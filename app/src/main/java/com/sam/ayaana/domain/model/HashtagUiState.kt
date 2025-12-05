package com.sam.ayaana.domain.model

data class HashtagUiState(
    val hashtags: List<Hashtag> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val generatedText: String = "",
    val copiedHashtag: String? = null
)