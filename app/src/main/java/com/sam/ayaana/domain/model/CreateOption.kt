package com.sam.ayaana.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class CreateOption(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val route: String
)

enum class CreateType {
    POST, STORY, REEL, LIVE
}
