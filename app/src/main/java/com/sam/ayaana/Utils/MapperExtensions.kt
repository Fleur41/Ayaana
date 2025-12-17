
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

// Add these imports for chat
import com.sam.ayaana.data.local.entity.ChatEntity
import com.sam.ayaana.data.local.entity.MessageEntity
import com.sam.ayaana.data.local.entity.ReelEntity
import com.sam.ayaana.data.remote.model.response.ChatResponse
import com.sam.ayaana.data.remote.model.response.MessageResponse
import com.sam.ayaana.domain.model.Chat
import com.sam.ayaana.domain.model.Message
import com.sam.ayaana.domain.model.MessageStatus
import com.sam.ayaana.domain.model.MessageType
import com.sam.ayaana.domain.model.Reel

// Date parsing helper to replace deprecated Date(String) constructor
private fun parseDate(timestamp: String): java.util.Date {
    return try {
        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            .parse(timestamp) ?: java.util.Date()
    } catch (e: Exception) {
        java.util.Date() // Fallback
    }
}

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

// ========== CHAT MAPPING EXTENSIONS ==========

// ChatEntity to Chat
fun ChatEntity.toChat(): Chat {
    return Chat(
        id = id,
        userId = userId,
        username = username,
        profileImage = profileImage,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCount = unreadCount,
        isOnline = isOnline,
        messageStatus = when (messageStatus) {
            "SENT" -> MessageStatus.SENT
            "DELIVERED" -> MessageStatus.DELIVERED
            else -> MessageStatus.SEEN
        }
    )
}

// Chat to ChatEntity
fun Chat.toChatEntity(): ChatEntity {
    return ChatEntity(
        id = id,
        userId = userId,
        username = username,
        profileImage = profileImage,
        lastMessage = lastMessage,
        timestamp = timestamp,
        unreadCount = unreadCount,
        isOnline = isOnline,
        messageStatus = messageStatus.name
    )
}

// ChatResponse to Chat
fun ChatResponse.toChat(): Chat {
    return Chat(
        id = id,
        userId = userId,
        username = username,
        profileImage = profileImage,
        lastMessage = lastMessage,
        timestamp = parseDate(timestamp),
        unreadCount = unreadCount,
        isOnline = isOnline,
        messageStatus = when (messageStatus) {
            "SENT" -> MessageStatus.SENT
            "DELIVERED" -> MessageStatus.DELIVERED
            else -> MessageStatus.SEEN
        }
    )
}

// ChatResponse to ChatEntity
fun ChatResponse.toChatEntity(): ChatEntity {
    return ChatEntity(
        id = id,
        userId = userId,
        username = username,
        profileImage = profileImage,
        lastMessage = lastMessage,
        timestamp = parseDate(timestamp),
        unreadCount = unreadCount,
        isOnline = isOnline,
        messageStatus = messageStatus
    )
}

// MessageEntity to Message
fun MessageEntity.toMessage(): Message {
    return Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        timestamp = timestamp,
        messageType = when (messageType) {
            "IMAGE" -> MessageType.IMAGE
            "VIDEO" -> MessageType.VIDEO
            "VOICE" -> MessageType.VOICE
            "LOCATION" -> MessageType.LOCATION
            else -> MessageType.TEXT
        },
        mediaUrl = mediaUrl,
        isSentByMe = isSentByMe,
        messageStatus = when (messageStatus) {
            "SENT" -> MessageStatus.SENT
            "DELIVERED" -> MessageStatus.DELIVERED
            else -> MessageStatus.SEEN
        }
    )
}

// Message to MessageEntity
fun Message.toMessageEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatId = chatId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        timestamp = timestamp,
        messageType = messageType.name,
        mediaUrl = mediaUrl,
        isSentByMe = isSentByMe,
        messageStatus = messageStatus.name
    )
}

// MessageResponse to Message
fun MessageResponse.toMessage(): Message {
    return Message(
        id = id,
        chatId = chatId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        timestamp = parseDate(timestamp),
        messageType = when (messageType) {
            "IMAGE" -> MessageType.IMAGE
            "VIDEO" -> MessageType.VIDEO
            "VOICE" -> MessageType.VOICE
            "LOCATION" -> MessageType.LOCATION
            else -> MessageType.TEXT
        },
        mediaUrl = mediaUrl,
        isSentByMe = senderId == "currentUser",
        messageStatus = when (messageStatus) {
            "SENT" -> MessageStatus.SENT
            "DELIVERED" -> MessageStatus.DELIVERED
            else -> MessageStatus.SEEN
        }
    )
}

// MessageResponse to MessageEntity
fun MessageResponse.toMessageEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        chatId = chatId,
        senderId = senderId,
        receiverId = receiverId,
        content = content,
        timestamp = parseDate(timestamp),
        messageType = messageType,
        mediaUrl = mediaUrl,
        isSentByMe = senderId == "currentUser",
        messageStatus = messageStatus
    )
}

fun ReelEntity.toReel(): Reel {
    return Reel(
        id = this.id,
        title = this.title,
        description = this.description,
        videoUrl = this.videoUrl,
        thumbnailUrl = this.thumbnailUrl,
        duration = this.duration,
        likes = this.likes,
        comments = this.comments,
        shares = this.shares,
        userId = this.userId,
        username = this.username,
        userProfileImage = this.userProfileImage,
        tags = this.tags.split(",").map { it.trim() },
        category = this.category,
        timestamp = this.timestamp,
        isLiked = this.isLiked,
        isSaved = this.isSaved
    )
}

fun Reel.toReelEntity(): ReelEntity {
    return ReelEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        videoUrl = this.videoUrl,
        thumbnailUrl = this.thumbnailUrl,
        duration = this.duration,
        likes = this.likes,
        comments = this.comments,
        shares = this.shares,
        userId = this.userId,
        username = this.username,
        userProfileImage = this.userProfileImage,
        tags = this.tags.joinToString(","),
        category = this.category,
        timestamp = this.timestamp,
        isLiked = this.isLiked,
        isSaved = this.isSaved
    )
}
//package com.sam.ayaana.Utils
//
//import com.sam.ayaana.data.local.entity.ActivityEntity
//import com.sam.ayaana.data.local.entity.PostEntity
//import com.sam.ayaana.data.local.entity.UserEntity
//import com.sam.ayaana.data.remote.model.response.ActivityResponse
//import com.sam.ayaana.data.remote.model.response.PostResponse
//import com.sam.ayaana.data.remote.model.response.UserResponse
//import com.sam.ayaana.domain.model.Activity
//import com.sam.ayaana.domain.model.ActivityType
//import com.sam.ayaana.domain.model.Post
//import com.sam.ayaana.domain.model.PostType
//import com.sam.ayaana.domain.model.User
//import com.sam.ayaana.domain.model.FollowStatus
//
//// Add these imports for chat
//import com.sam.ayaana.data.local.entity.ChatEntity as LocalChatEntity
//import com.sam.ayaana.data.local.entity.MessageEntity as LocalMessageEntity
//import com.sam.ayaana.data.remote.model.response.ChatResponse
//import com.sam.ayaana.data.remote.model.response.MessageResponse
//import com.sam.ayaana.domain.model.Chat
//import com.sam.ayaana.domain.model.Message
//import com.sam.ayaana.domain.model.MessageStatus as DomainMessageStatus
//import com.sam.ayaana.domain.model.MessageType as DomainMessageType
//
//// Date parsing helper to replace deprecated Date(String) constructor
//private fun parseDate(timestamp: String): java.util.Date {
//    return try {
//        java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
//            .parse(timestamp) ?: java.util.Date()
//    } catch (e: Exception) {
//        java.util.Date() // Fallback
//    }
//}
//// PostResponse to PostEntity - FIXED to match your PostEntity
//fun PostResponse.toPostEntity(): PostEntity {
//    return PostEntity(
//        id = this.id,
//        userId = this.user.id,
//        username = this.user.username,
//        userProfileImage = this.user.profilePicture ?: "",
//        imageUrl = this.imageUrl,
//        caption = this.caption,
//        likes = this.likesCount,
//        comments = this.commentsCount,
//        reposts = this.repostsCount, // This matches your PostEntity field
//        isLiked = this.isLiked,
//        isReposted = this.isReposted, // This matches your PostEntity field
//        timestamp = this.createdAt.time,
//        location = this.location,
//        originalPostId = this.originalPost?.id,
//        type = this.type
//    )
//}
//
//// PostEntity to Post - FIXED to match your PostEntity
//fun PostEntity.toPost(): Post {
//    return Post(
//        id = this.id,
//        userId = this.userId,
//        username = this.username,
//        userProfileImage = this.userProfileImage,
//        imageUrl = this.imageUrl,
//        caption = this.caption,
//        likes = this.likes,
//        comments = this.comments,
//        reposts = this.reposts, // This matches your PostEntity field
//        isLiked = this.isLiked,
//        isReposted = this.isReposted, // This matches your PostEntity field
//        timestamp = this.timestamp,
//        location = this.location,
//        originalPost = null, // Would need to fetch separately
//        type = if (this.type == "REPOST") PostType.REPOST else PostType.ORIGINAL
//    )
//}
//
//// PostResponse to Post - FIXED to match your PostResponse
//fun PostResponse.toPost(): Post {
//    return Post(
//        id = this.id,
//        userId = this.user.id,
//        username = this.user.username,
//        userProfileImage = this.user.profilePicture ?: "",
//        imageUrl = this.imageUrl,
//        caption = this.caption,
//        likes = this.likesCount,
//        comments = this.commentsCount,
//        reposts = this.repostsCount, // This matches your PostResponse field
//        isLiked = this.isLiked,
//        isReposted = this.isReposted, // This matches your PostResponse field
//        timestamp = this.createdAt.time,
//        location = this.location,
//        originalPost = this.originalPost?.toPost(),
//        type = if (this.type == "REPOST") PostType.REPOST else PostType.ORIGINAL
//    )
//}
//
//// UserResponse to UserEntity
//fun UserResponse.toUserEntity(): UserEntity {
//    return UserEntity(
//        id = this.id,
//        username = this.username,
//        email = this.email,
//        profilePicture = this.profilePicture,
//        fullName = this.fullName,
//        bio = this.bio,
//        posts = this.postsCount,
//        followers = this.followersCount,
//        following = this.followingCount,
//        isPrivate = this.isPrivate,
//        isFollowing = this.isFollowing,
//        followStatus = this.followStatus
//    )
//}
//
//// UserEntity to User
//fun UserEntity.toUser(): User {
//    return User(
//        id = this.id,
//        username = this.username,
//        email = this.email,
//        profilePicture = this.profilePicture,
//        fullName = this.fullName,
//        bio = this.bio,
//        posts = this.posts,
//        followers = this.followers,
//        following = this.following,
//        isPrivate = this.isPrivate,
//        isFollowing = this.isFollowing,
//        followStatus = when (this.followStatus) {
//            "FOLLOWING" -> FollowStatus.FOLLOWING
//            "REQUESTED" -> FollowStatus.REQUESTED
//            else -> FollowStatus.NOT_FOLLOWING
//        }
//    )
//}
//
//// UserResponse to User
//fun UserResponse.toUser(): User {
//    return User(
//        id = this.id,
//        username = this.username,
//        email = this.email,
//        profilePicture = this.profilePicture,
//        fullName = this.fullName,
//        bio = this.bio,
//        posts = this.postsCount,
//        followers = this.followersCount,
//        following = this.followingCount,
//        isPrivate = this.isPrivate,
//        isFollowing = this.isFollowing,
//        followStatus = when (this.followStatus) {
//            "FOLLOWING" -> FollowStatus.FOLLOWING
//            "REQUESTED" -> FollowStatus.REQUESTED
//            else -> FollowStatus.NOT_FOLLOWING
//        }
//    )
//}
//
//// User to UserResponse (for profile updates)
//fun User.toUserResponse(): UserResponse {
//    return UserResponse(
//        id = this.id,
//        username = this.username,
//        email = this.email,
//        profilePicture = this.profilePicture,
//        fullName = this.fullName,
//        bio = this.bio,
//        postsCount = this.posts,
//        followersCount = this.followers,
//        followingCount = this.following,
//        isPrivate = this.isPrivate,
//        isFollowing = this.isFollowing,
//        followStatus = when (this.followStatus) {
//            FollowStatus.FOLLOWING -> "FOLLOWING"
//            FollowStatus.REQUESTED -> "REQUESTED"
//            else -> "NOT_FOLLOWING"
//        }
//    )
//}
//
//// User to UserEntity
//fun User.toUserEntity(): UserEntity {
//    return UserEntity(
//        id = this.id,
//        username = this.username,
//        email = this.email,
//        profilePicture = this.profilePicture,
//        fullName = this.fullName,
//        bio = this.bio,
//        posts = this.posts,
//        followers = this.followers,
//        following = this.following,
//        isPrivate = this.isPrivate,
//        isFollowing = this.isFollowing,
//        followStatus = when (this.followStatus) {
//            FollowStatus.FOLLOWING -> "FOLLOWING"
//            FollowStatus.REQUESTED -> "REQUESTED"
//            else -> "NOT_FOLLOWING"
//        }
//    )
//}
//
//// ActivityResponse to ActivityEntity
//fun ActivityResponse.toActivityEntity(): ActivityEntity {
//    return ActivityEntity(
//        id = this.id,
//        userId = this.user.id,
//        username = this.user.username,
//        userProfileImage = this.user.profilePicture ?: "",
//        type = this.type,
//        timestamp = this.createdAt,
//        postId = this.post?.id,
//        postImage = this.post?.imageUrl
//    )
//}
//
//// ActivityEntity to Activity
//fun ActivityEntity.toActivity(): Activity {
//    return Activity(
//        id = this.id,
//        userId = this.userId,
//        username = this.username,
//        userProfileImage = this.userProfileImage,
//        type = when (this.type) {
//            "LIKE" -> ActivityType.LIKE
//            "COMMENT" -> ActivityType.COMMENT
//            "FOLLOW" -> ActivityType.FOLLOW
//            "FOLLOW_REQUEST" -> ActivityType.FOLLOW_REQUEST
//            "REPOST" -> ActivityType.REPOST
//            else -> ActivityType.LIKE
//        },
//        timestamp = this.timestamp,
//        postId = this.postId,
//        postImage = this.postImage
//    )
//}
//
//// ActivityResponse to Activity
//fun ActivityResponse.toActivity(): Activity {
//    return Activity(
//        id = this.id,
//        userId = this.user.id,
//        username = this.user.username,
//        userProfileImage = this.user.profilePicture ?: "",
//        type = when (this.type) {
//            "LIKE" -> ActivityType.LIKE
//            "COMMENT" -> ActivityType.COMMENT
//            "FOLLOW" -> ActivityType.FOLLOW
//            "FOLLOW_REQUEST" -> ActivityType.FOLLOW_REQUEST
//            "REPOST" -> ActivityType.REPOST
//            else -> ActivityType.LIKE
//        },
//        timestamp = this.createdAt,
//        postId = this.post?.id,
//        postImage = this.post?.imageUrl
//    )
//}
//
//// New mapping functions
//// Chat mapping extensions
//fun LocalChatEntity.toChat(): Chat {
//    return Chat(
//        id = id,
//        userId = userId,
//        username = username,
//        profileImage = profileImage,
//        lastMessage = lastMessage,
//        timestamp = timestamp,
//        unreadCount = unreadCount,
//        isOnline = isOnline,
//        messageStatus = when (messageStatus) {
//            "SENT" -> DomainMessageStatus.SENT
//            "DELIVERED" -> DomainMessageStatus.DELIVERED
//            else -> DomainMessageStatus.SEEN
//        }
//    )
//}
//
//fun Chat.toChatEntity(): LocalChatEntity {
//    return LocalChatEntity(
//        id = id,
//        userId = userId,
//        username = username,
//        profileImage = profileImage,
//        lastMessage = lastMessage,
//        timestamp = timestamp,
//        unreadCount = unreadCount,
//        isOnline = isOnline,
//        messageStatus = messageStatus.name
//    )
//}
//
//fun ChatResponse.toChat(): Chat {
//    return Chat(
//        id = id,
//        userId = userId,
//        username = username,
//        profileImage = profileImage,
//        lastMessage = lastMessage,
//        timestamp = parseDate(timestamp),
//        unreadCount = unreadCount,
//        isOnline = isOnline,
//        messageStatus = when (messageStatus) {
//            "SENT" -> DomainMessageStatus.SENT
//            "DELIVERED" -> DomainMessageStatus.DELIVERED
//            else -> DomainMessageStatus.SEEN
//        }
//    )
//}
//
//fun ChatResponse.toChatEntity(): LocalChatEntity {
//    return LocalChatEntity(
//        id = id,
//        userId = userId,
//        username = username,
//        profileImage = profileImage,
//        lastMessage = lastMessage,
//        timestamp =  parseDate(timestamp),
//        unreadCount = unreadCount,
//        isOnline = isOnline,
//        messageStatus = messageStatus
//    )
//}
//
//// Message mapping extensions
//fun LocalMessageEntity.toMessage(): Message {
//    return Message(
//        id = id,
//        chatId = chatId,
//        senderId = senderId,
//        receiverId = receiverId,
//        content = content,
//        timestamp = timestamp,
//        messageType = when (messageType) {
//            "IMAGE" -> DomainMessageType.IMAGE
//            "VIDEO" -> DomainMessageType.VIDEO
//            "VOICE" -> DomainMessageType.VOICE
//            "LOCATION" -> DomainMessageType.LOCATION
//            else -> DomainMessageType.TEXT
//        },
//        mediaUrl = mediaUrl,
//        isSentByMe = isSentByMe,
//        messageStatus = when (messageStatus) {
//            "SENT" -> DomainMessageStatus.SENT
//            "DELIVERED" -> DomainMessageStatus.DELIVERED
//            else -> DomainMessageStatus.SEEN
//        }
//    )
//}
//
//fun Message.toMessageEntity(): LocalMessageEntity {
//    return LocalMessageEntity(
//        id = id,
//        chatId = chatId,
//        senderId = senderId,
//        receiverId = receiverId,
//        content = content,
//        timestamp = timestamp,
//        messageType = messageType.name,
//        mediaUrl = mediaUrl,
//        isSentByMe = isSentByMe,
//        messageStatus = messageStatus.name
//    )
//}
//
//fun MessageResponse.toMessage(): Message {
//    return Message(
//        id = id,
//        chatId = chatId,
//        senderId = senderId,
//        receiverId = receiverId,
//        content = content,
//        timestamp =  parseDate(timestamp),
//        messageType = when (messageType) {
//            "IMAGE" -> DomainMessageType.IMAGE
//            "VIDEO" -> DomainMessageType.VIDEO
//            "VOICE" -> DomainMessageType.VOICE
//            "LOCATION" -> DomainMessageType.LOCATION
//            else -> DomainMessageType.TEXT
//        },
//        mediaUrl = mediaUrl,
//        isSentByMe = senderId == "currentUser",
//        messageStatus = when (messageStatus) {
//            "SENT" -> DomainMessageStatus.SENT
//            "DELIVERED" -> DomainMessageStatus.DELIVERED
//            else -> DomainMessageStatus.SEEN
//        }
//    )
//}
//
//fun MessageResponse.toMessageEntity(): LocalMessageEntity {
//    return LocalMessageEntity(
//        id = id,
//        chatId = chatId,
//        senderId = senderId,
//        receiverId = receiverId,
//        content = content,
//        timestamp =  parseDate(timestamp),
//        messageType = messageType,
//        mediaUrl = mediaUrl,
//        isSentByMe = senderId == "currentUser",
//        messageStatus = messageStatus
//    )
//}