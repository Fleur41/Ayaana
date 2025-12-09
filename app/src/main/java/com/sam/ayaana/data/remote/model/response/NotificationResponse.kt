package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class NotificationResponse(
    @Json(name = "id")
    val id: String,

    @Json(name = "userId")
    val userId: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "message")
    val message: String,

    @Json(name = "userProfileImage")
    val userProfileImage: String? = null,

    @Json(name = "userName")
    val userName: String? = null,

    @Json(name = "postImage")
    val postImage: String? = null,

    @Json(name = "targetUserId")
    val targetUserId: String? = null,

    @Json(name = "postId")
    val postId: String? = null,

    @Json(name = "isRead")
    val isRead: Boolean,

    @Json(name = "timestamp")
    val timestamp: Date,

    @Json(name = "actionRequired")
    val actionRequired: Boolean = false
)

@JsonClass(generateAdapter = true)
data class FollowRequestResponse(
    @Json(name = "id")
    val id: String,

    @Json(name = "userId")
    val userId: String,

    @Json(name = "userName")
    val userName: String,

    @Json(name = "fullName")
    val fullName: String,

    @Json(name = "profileImage")
    val profileImage: String? = null,

    @Json(name = "mutualFollowers")
    val mutualFollowers: List<String> = emptyList(),

    @Json(name = "timestamp")
    val timestamp: Date
)

@JsonClass(generateAdapter = true)
data class SuggestedUserResponse(
    @Json(name = "id")
    val id: String,

    @Json(name = "userName")
    val userName: String,

    @Json(name = "fullName")
    val fullName: String,

    @Json(name = "profileImage")
    val profileImage: String? = null,

    @Json(name = "mutualConnections")
    val mutualConnections: List<String> = emptyList(),

    @Json(name = "reason")
    val reason: String
)
