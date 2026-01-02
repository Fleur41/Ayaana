package com.sam.ayaana.domain.model

import java.util.Date

data class LiveStream(
    val id: String,
    val hostId: String,
    val hostUsername: String,
    val hostProfileImage: String?,
    val title: String?,
    val thumbnailUrl: String?,
    val streamUrl: String,
    val viewerCount: Int,
    val isLive: Boolean,
    val startedAt: Date,
    val endedAt: Date?,
    val tags: List<String> = emptyList(),
    val isPrivate: Boolean = false
)

data class LiveStreamSession(
    val streamId: String,
    val rtmpUrl: String,
    val streamKey: String,
    val viewerUrl: String,
    val expiresAt: Date
)