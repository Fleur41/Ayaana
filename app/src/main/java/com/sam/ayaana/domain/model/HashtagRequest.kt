package com.sam.ayaana.domain.model

data class HashtagRequest(
    val text: String? = null,
    val category: String? = null,
    val maxResults: Int = 10
)