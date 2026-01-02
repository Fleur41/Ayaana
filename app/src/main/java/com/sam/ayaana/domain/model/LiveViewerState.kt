package com.sam.ayaana.domain.model

data class LiveViewerState(
    val isLoading: Boolean = false,
    val stream: LiveStream? = null,
    val viewerCount: Int = 0,
    val comments: List<LiveComment> = emptyList(),
    val showComments: Boolean = true,
    val isFollowing: Boolean = false,
    val likes: Int = 0,
    val error: String? = null
)