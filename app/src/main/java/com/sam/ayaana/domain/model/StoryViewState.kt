package com.sam.ayaana.domain.model

data class StoryViewState(
    val stories: List<StoryUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val selectedStory: StoryUiModel? = null,
    val isStoryViewerVisible: Boolean = false
)

data class StoryUiModel(
    val id: Int,
    val username: String,
    val profileImageUrl: String,
    val storyImageUrl: String,
    val isViewed: Boolean
)