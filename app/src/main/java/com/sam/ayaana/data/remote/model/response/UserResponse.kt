package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserResponse(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "email") val email: String,
    @Json(name = "profile_picture") val profilePicture: String?,
    @Json(name = "full_name") val fullName: String,
    @Json(name = "bio") val bio: String?,
    @Json(name = "posts_count") val postsCount: Int,
    @Json(name = "followers_count") val followersCount: Int,
    @Json(name = "following_count") val followingCount: Int,
    @Json(name = "is_private") val isPrivate: Boolean,
    @Json(name = "is_following") val isFollowing: Boolean,
    @Json(name = "follow_status") val followStatus: String
)