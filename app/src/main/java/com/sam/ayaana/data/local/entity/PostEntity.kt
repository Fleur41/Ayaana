package com.sam.ayaana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val username: String,
    val userProfileImage: String,
    val imageUrl: String,
    val caption: String,
    val likes: Int,
    val comments: Int,
    val reposts: Int,
    val isLiked: Boolean,
    val isReposted: Boolean,
    val timestamp: Long,
    val location: String? = null,
    val originalPostId: String? = null,
    val type: String = "ORIGINAL"
)