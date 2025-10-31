package com.sam.ayaana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val username: String,
    val userProfileImage: String,
    val type: String,
    val timestamp: Long,
    val postId: String? = null,
    val postImage: String? = null
)