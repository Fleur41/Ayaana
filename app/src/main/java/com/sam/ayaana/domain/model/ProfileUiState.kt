package com.sam.ayaana.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.ui.graphics.vector.ImageVector

data class ProfileUiState(
    val user: User? = null,
    val posts: List<Post> = emptyList(),
    val taggedPosts: List<Post> = emptyList(),
    val reels: List<Post> = emptyList(),
    val selectedTab: ProfileTab = ProfileTab.POSTS,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class ProfileTab(
    val title: String,
    val icon: ImageVector
) {
    POSTS("Posts", Icons.Default.GridOn),
    REELS("Reels", Icons.Default.VideoLibrary),
    TAGGED("Tagged", Icons.Default.Tag)
}