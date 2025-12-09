package com.sam.ayaana.data.remote.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationRequest(
    @Json(name = "userId")
    val userId: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "targetUserId")
    val targetUserId: String? = null,

    @Json(name = "postId")
    val postId: String? = null,

    @Json(name = "message")
    val message: String? = null
)
