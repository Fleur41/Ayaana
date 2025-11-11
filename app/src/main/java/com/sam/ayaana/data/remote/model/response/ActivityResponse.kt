package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActivityResponse(
    @Json(name = "id") val id: String,
    @Json(name = "user") val user: UserResponse,
    @Json(name = "type") val type: String,
    @Json(name = "created_at") val createdAt: Long,
    @Json(name = "post") val post: PostResponse?,
    @Json(name = "is_read") val isRead: Boolean
)
