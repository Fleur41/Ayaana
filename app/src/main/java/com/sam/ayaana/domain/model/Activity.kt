package com.sam.ayaana.domain.model

data class Activity(
    val id: String,
    val userId: String,
    val username: String,
    val userProfileImage: String,
    val type: ActivityType,
    val timestamp: Long,
    val postId: String? = null,
    val postImage: String? = null
)

enum class ActivityType {
    LIKE, COMMENT, FOLLOW, FOLLOW_REQUEST, REPOST
}