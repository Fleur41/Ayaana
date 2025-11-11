package com.sam.ayaana.Utils

import com.sam.ayaana.data.local.entity.ActivityEntity
import com.sam.ayaana.data.local.entity.PostEntity
import com.sam.ayaana.data.local.entity.UserEntity
import com.sam.ayaana.data.remote.model.response.ActivityResponse
import com.sam.ayaana.data.remote.model.response.PostResponse
import com.sam.ayaana.data.remote.model.response.UserResponse
import com.sam.ayaana.domain.model.Activity
import com.sam.ayaana.domain.model.ActivityType
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.model.PostType
import com.sam.ayaana.domain.model.User
import com.sam.ayaana.domain.model.FollowStatus

// PostResponse to PostEntity - FIXED to match your PostEntity
fun PostResponse.toPostEntity(): PostEntity {
    return PostEntity(
        id = this.id,
        userId = this.user.id,
        username = this.user.username,
        userProfileImage = this.user.profilePicture ?: "",
        imageUrl = this.imageUrl,
        caption = this.caption,
        likes = this.likesCount,
        comments = this.commentsCount,
        reposts = this.repostsCount, // This matches your PostEntity field
        isLiked = this.isLiked,
        isReposted = this.isReposted, // This matches your PostEntity field
        timestamp = this.createdAt.time,
        location = this.location,
        originalPostId = this.originalPost?.id,
        type = this.type
    )
}

// PostEntity to Post - FIXED to match your PostEntity
fun PostEntity.toPost(): Post {
    return Post(
        id = this.id,
        userId = this.userId,
        username = this.username,
        userProfileImage = this.userProfileImage,
        imageUrl = this.imageUrl,
        caption = this.caption,
        likes = this.likes,
        comments = this.comments,
        reposts = this.reposts, // This matches your PostEntity field
        isLiked = this.isLiked,
        isReposted = this.isReposted, // This matches your PostEntity field
        timestamp = this.timestamp,
        location = this.location,
        originalPost = null, // Would need to fetch separately
        type = if (this.type == "REPOST") PostType.REPOST else PostType.ORIGINAL
    )
}

// PostResponse to Post - FIXED to match your PostResponse
fun PostResponse.toPost(): Post {
    return Post(
        id = this.id,
        userId = this.user.id,
        username = this.user.username,
        userProfileImage = this.user.profilePicture ?: "",
        imageUrl = this.imageUrl,
        caption = this.caption,
        likes = this.likesCount,
        comments = this.commentsCount,
        reposts = this.repostsCount, // This matches your PostResponse field
        isLiked = this.isLiked,
        isReposted = this.isReposted, // This matches your PostResponse field
        timestamp = this.createdAt.time,
        location = this.location,
        originalPost = this.originalPost?.toPost(),
        type = if (this.type == "REPOST") PostType.REPOST else PostType.ORIGINAL
    )
}

// UserResponse to UserEntity
fun UserResponse.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        profilePicture = this.profilePicture,
        fullName = this.fullName,
        bio = this.bio,
        posts = this.postsCount,
        followers = this.followersCount,
        following = this.followingCount,
        isPrivate = this.isPrivate,
        isFollowing = this.isFollowing,
        followStatus = this.followStatus
    )
}

// UserEntity to User
fun UserEntity.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        profilePicture = this.profilePicture,
        fullName = this.fullName,
        bio = this.bio,
        posts = this.posts,
        followers = this.followers,
        following = this.following,
        isPrivate = this.isPrivate,
        isFollowing = this.isFollowing,
        followStatus = when (this.followStatus) {
            "FOLLOWING" -> FollowStatus.FOLLOWING
            "REQUESTED" -> FollowStatus.REQUESTED
            else -> FollowStatus.NOT_FOLLOWING
        }
    )
}

// UserResponse to User
fun UserResponse.toUser(): User {
    return User(
        id = this.id,
        username = this.username,
        email = this.email,
        profilePicture = this.profilePicture,
        fullName = this.fullName,
        bio = this.bio,
        posts = this.postsCount,
        followers = this.followersCount,
        following = this.followingCount,
        isPrivate = this.isPrivate,
        isFollowing = this.isFollowing,
        followStatus = when (this.followStatus) {
            "FOLLOWING" -> FollowStatus.FOLLOWING
            "REQUESTED" -> FollowStatus.REQUESTED
            else -> FollowStatus.NOT_FOLLOWING
        }
    )
}

// User to UserResponse (for profile updates)
fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username,
        email = this.email,
        profilePicture = this.profilePicture,
        fullName = this.fullName,
        bio = this.bio,
        postsCount = this.posts,
        followersCount = this.followers,
        followingCount = this.following,
        isPrivate = this.isPrivate,
        isFollowing = this.isFollowing,
        followStatus = when (this.followStatus) {
            FollowStatus.FOLLOWING -> "FOLLOWING"
            FollowStatus.REQUESTED -> "REQUESTED"
            else -> "NOT_FOLLOWING"
        }
    )
}

// User to UserEntity
fun User.toUserEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        email = this.email,
        profilePicture = this.profilePicture,
        fullName = this.fullName,
        bio = this.bio,
        posts = this.posts,
        followers = this.followers,
        following = this.following,
        isPrivate = this.isPrivate,
        isFollowing = this.isFollowing,
        followStatus = when (this.followStatus) {
            FollowStatus.FOLLOWING -> "FOLLOWING"
            FollowStatus.REQUESTED -> "REQUESTED"
            else -> "NOT_FOLLOWING"
        }
    )
}

// ActivityResponse to ActivityEntity
fun ActivityResponse.toActivityEntity(): ActivityEntity {
    return ActivityEntity(
        id = this.id,
        userId = this.user.id,
        username = this.user.username,
        userProfileImage = this.user.profilePicture ?: "",
        type = this.type,
        timestamp = this.createdAt,
        postId = this.post?.id,
        postImage = this.post?.imageUrl
    )
}

// ActivityEntity to Activity
fun ActivityEntity.toActivity(): Activity {
    return Activity(
        id = this.id,
        userId = this.userId,
        username = this.username,
        userProfileImage = this.userProfileImage,
        type = when (this.type) {
            "LIKE" -> ActivityType.LIKE
            "COMMENT" -> ActivityType.COMMENT
            "FOLLOW" -> ActivityType.FOLLOW
            "FOLLOW_REQUEST" -> ActivityType.FOLLOW_REQUEST
            "REPOST" -> ActivityType.REPOST
            else -> ActivityType.LIKE
        },
        timestamp = this.timestamp,
        postId = this.postId,
        postImage = this.postImage
    )
}

// ActivityResponse to Activity
fun ActivityResponse.toActivity(): Activity {
    return Activity(
        id = this.id,
        userId = this.user.id,
        username = this.user.username,
        userProfileImage = this.user.profilePicture ?: "",
        type = when (this.type) {
            "LIKE" -> ActivityType.LIKE
            "COMMENT" -> ActivityType.COMMENT
            "FOLLOW" -> ActivityType.FOLLOW
            "FOLLOW_REQUEST" -> ActivityType.FOLLOW_REQUEST
            "REPOST" -> ActivityType.REPOST
            else -> ActivityType.LIKE
        },
        timestamp = this.createdAt,
        postId = this.post?.id,
        postImage = this.post?.imageUrl
    )
}