package com.sam.ayaana.data.remote.model.response

data class ReelResponse(
    val id: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val duration: Long,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val userId: String,
    val username: String,
    val userProfileImage: String,
    val tags: List<String>,
    val category: String,
    val timestamp: Long,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)