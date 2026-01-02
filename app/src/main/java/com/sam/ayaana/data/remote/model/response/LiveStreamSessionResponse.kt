package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class LiveStreamSessionResponse(
    @Json(name = "stream_id") val streamId: String,
    @Json(name = "rtmp_url") val rtmpUrl: String,
    @Json(name = "stream_key") val streamKey: String,
    @Json(name = "viewer_url") val viewerUrl: String,
    @Json(name = "expires_at") val expiresAt: Date
)