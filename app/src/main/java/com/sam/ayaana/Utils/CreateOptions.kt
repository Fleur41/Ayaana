package com.sam.ayaana.Utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.Theaters
import androidx.compose.material.icons.outlined.VideoCall
import com.sam.ayaana.domain.model.CreateOption

object CreateOptions {
    val defaultOptions = listOf(
        CreateOption(
            title = "Post",
            icon = Icons.Outlined.GridOn,
            description = "Share photos or videos",
            route = "create_post",
            // route = "create_post_details",
        ),
        CreateOption(
            title = "Story",
            icon = Icons.Outlined.AddCircleOutline,
            description = "Share to your story for 24 hours",
            route = "create_story",

        ),
        CreateOption(
            title = "Reel",
            icon = Icons.Outlined.Theaters,
            description = "Create a short video",
            route = "create_post",
            // route = "create_reel",

        ),
        CreateOption(
            title = "Live",
            icon = Icons.Outlined.VideoCall,
            description = "Go live with your followers",
            route = "create_post",
            // route = "create_live",

        )
    )

    // Instagram-like create options for bottom sheet popup
    val bottomSheetOptions = listOf(
        CreateOption(
            title = "Reel",
            description = "Create a short video",
            icon = Icons.Outlined.Theaters,
            route = "create_post",
            // route = "create_reel"

        ),
        CreateOption(
            title = "Post",
            description = "Share a photo or video",
            icon = Icons.Outlined.GridOn,
            route = "create_post",
            // route = "create_post"

        ),
        CreateOption(
            title = "Story",
            description = "Share a photo or video that disappears",
            icon = Icons.Outlined.AddCircleOutline,
            route = "create_post",
            // route = "create_story"

        ),
        CreateOption(
            title = "Live",
            description = "Broadcast live video",
            icon = Icons.Outlined.VideoCall,
            route = "create_post",
            // route = "create_live"

        )
    )
}