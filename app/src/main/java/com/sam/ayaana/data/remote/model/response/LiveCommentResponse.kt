package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LiveCommentResponse(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "username") val username: String,
    @Json(name = "text") val text: String,
    @Json(name = "timestamp") val timestamp: Long,
    @Json(name = "stream_id") val streamId: String
)