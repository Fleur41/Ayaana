package com.sam.ayaana.domain.model


data class Post(
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
    val originalPost: Post? = null, // For reposts
    val type: PostType = PostType.ORIGINAL
)

enum class PostType {
    ORIGINAL, REPOST
}