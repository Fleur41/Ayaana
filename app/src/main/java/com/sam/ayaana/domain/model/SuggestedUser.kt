package com.sam.ayaana.domain.model

data class SuggestedUser(
    val id: String,
    val userName: String,
    val fullName: String,
    val profileImage: String? = null,
    val mutualConnections: List<String> = emptyList(),
    val reason: String
)