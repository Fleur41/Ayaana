package com.sam.ayaana.domain.model

data class Story(
    val id: Int,
    val userId: String,
    val username: String,
    val profileImageUrl: String,
    val storyImageUrl: String, // This will be the human image
    val isViewed: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)