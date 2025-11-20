package com.sam.ayaana.Utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.sam.ayaana.domain.model.CreateOption

object CreateOptions {
    val defaultOptions = listOf(
        CreateOption(
            title = "Post",
            icon = Icons.Default.Image,
            description = "Share photos or videos",
            route = "create_post_details"
        ),
        CreateOption(
            title = "Story",
            icon = Icons.Default.Photo,
            description = "Share to your story for 24 hours",
            route = "create_story"
        ),
        CreateOption(
            title = "Reel",
            icon = Icons.Default.PlayArrow,
            description = "Create a short video",
            route = "create_reel"
        ),
        CreateOption(
            title = "Live",
            icon = Icons.Default.Videocam,
            description = "Go live with your followers",
            route = "create_live"
        )
    )
}