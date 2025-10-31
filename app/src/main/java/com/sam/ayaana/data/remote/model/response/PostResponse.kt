package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class PostResponse(
    @Json(name = "id") val id: String,
    @Json(name = "user") val user: UserResponse,
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "caption") val caption: String,
    @Json(name = "likes_count") val likesCount: Int,
    @Json(name = "comments_count") val commentsCount: Int,
    @Json(name = "reposts_count") val repostsCount: Int,
    @Json(name = "is_liked") val isLiked: Boolean,
    @Json(name = "is_reposted") val isReposted: Boolean,
    @Json(name = "created_at") val createdAt: Date,
    @Json(name = "location") val location: String?,
    @Json(name = "original_post") val originalPost: PostResponse?,
    @Json(name = "type") val type: String
)