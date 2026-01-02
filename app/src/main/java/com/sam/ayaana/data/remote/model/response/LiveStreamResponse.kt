package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class LiveStreamResponse(
    @Json(name = "id") val id: String,
    @Json(name = "host_id") val hostId: String,
    @Json(name = "host_username") val hostUsername: String,
    @Json(name = "host_profile_image") val hostProfileImage: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
    @Json(name = "stream_url") val streamUrl: String,
    @Json(name = "viewer_count") val viewerCount: Int,
    @Json(name = "is_live") val isLive: Boolean,
    @Json(name = "started_at") val startedAt: Date,
    @Json(name = "ended_at") val endedAt: Date?,
    @Json(name = "tags") val tags: List<String>,
    @Json(name = "is_private") val isPrivate: Boolean
)


