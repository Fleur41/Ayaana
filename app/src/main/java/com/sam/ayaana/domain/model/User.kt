package com.sam.ayaana.domain.model

data class User(
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
    val followStatus: FollowStatus = FollowStatus.NOT_FOLLOWING
)

enum class FollowStatus {
    NOT_FOLLOWING, FOLLOWING, REQUESTED
}