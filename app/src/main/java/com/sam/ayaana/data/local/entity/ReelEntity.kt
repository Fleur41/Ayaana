package com.sam.ayaana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reels")
data class ReelEntity(
    @PrimaryKey
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
    val tags: String, // Stored as comma-separated string
    val category: String,
    val timestamp: Long,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)


