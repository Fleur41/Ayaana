package com.sam.ayaana.domain.model

import android.net.Uri

data class User(
    val id: String,
    val username: String,
    val email: String,
    val profilePicture: String? = null,
    val fullName: String,
    val bio: String?,
    val posts: Int,
    val followers: Int,
    val following: Int,
    val isPrivate: Boolean,
    val isFollowing: Boolean,
    val followStatus: FollowStatus = FollowStatus.NOT_FOLLOWING,
    val isCurrentUser: Boolean = false, // NEW: to distinguish my profile vs others

    val localProfileUri: Uri? = null,
    // Optionals
    val website: String? = null,
    val phoneNumber: String? = null
)

enum class FollowStatus {
    NOT_FOLLOWING, FOLLOWING, REQUESTED
}