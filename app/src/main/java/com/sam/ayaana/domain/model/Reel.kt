package com.sam.ayaana.domain.model


data class Reel(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val duration: Long, // in seconds
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val userId: String,
    val username: String,
    val userProfileImage: String,
    val tags: List<String>, // For AI search
    val category: String, // For filtering
    val timestamp: Long,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)


data class SearchQuery(
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)