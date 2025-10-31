package com.sam.ayaana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val email: String,
    val profilePicture: String?,
    val fullName: String,
    val bio: String?,
    val posts: Int,
    val followers: Int,
    val following: Int,
    val isPrivate: Boolean,
    val isFollowing: Boolean,
    val followStatus: String = "NOT_FOLLOWING"
)