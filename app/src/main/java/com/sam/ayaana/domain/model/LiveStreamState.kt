package com.sam.ayaana.domain.model

data class LiveStreamState(
    val isLive: Boolean = false,
    val isLoading: Boolean = false,
    val viewerCount: Int = 0,
    val comments: List<LiveComment> = emptyList(),
    val title: String = "",
    val isPrivate: Boolean = false,
    val session: LiveStreamSession? = null,
    val showEndConfirmation: Boolean = false,
    val error: String? = null
)