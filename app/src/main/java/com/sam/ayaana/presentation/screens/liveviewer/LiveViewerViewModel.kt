package com.sam.ayaana.presentation.screens.livestream

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.LiveComment
import com.sam.ayaana.domain.model.LiveViewerState
import com.sam.ayaana.domain.repository.ILiveStreamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveViewerViewModel @Inject constructor(
    private val liveStreamRepository: ILiveStreamRepository
) : ViewModel() {

    private val _viewerState = MutableStateFlow(LiveViewerState())
    val viewerState: StateFlow<LiveViewerState> = _viewerState.asStateFlow()

    // Load live stream by ID
    fun loadLiveStream(streamId: String) {
        viewModelScope.launch {
            _viewerState.update { it.copy(isLoading = true, error = null) }

            val result = liveStreamRepository.getLiveStreamById(streamId)
            when (result) {
                is Result.Success -> {
                    _viewerState.update {
                        it.copy(
                            isLoading = false,
                            stream = result.data,
                            error = null
                        )
                    }
                    // Join the stream
                    joinStream(streamId)
                    // Start polling for updates
                    startViewerPolling(streamId)
                    startCommentPolling(streamId)
                }
                is Result.Error -> {
                    _viewerState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Handle loading
                }
            }
        }
    }

    // Send a comment
    fun sendComment(comment: String) {
        viewModelScope.launch {
            _viewerState.value.stream?.id?.let { streamId ->
                val result = liveStreamRepository.sendLiveComment(streamId, comment)
                when (result) {
                    is Result.Success -> {
                        // Add to local comments
                        val newComment = LiveComment(
                            id = System.currentTimeMillis().toString(),
                            username = "You", // Current user
                            text = comment,
                            timestamp = System.currentTimeMillis()
                        )
                        _viewerState.update { state ->
                            state.copy(
                                comments = state.comments + newComment
                            )
                        }
                    }
                    is Result.Error -> {
                        _viewerState.update {
                            it.copy(error = result.message)
                        }
                    }
                    is Result.Loading -> {
                        // Handle loading
                    }
                }
            }
        }
    }

    // Send a like
    fun sendLike() {
        // Mock implementation - in real app, call API
        _viewerState.update { it.copy(likes = it.likes + 1) }
    }

    // Follow the streamer
    fun followStreamer() {
        _viewerState.update { it.copy(isFollowing = true) }
    }

    // Join a live stream
    private fun joinStream(streamId: String) {
        viewModelScope.launch {
            liveStreamRepository.joinLiveStream(streamId)
            // Handle result if needed
        }
    }

    // Leave a live stream
    fun leaveStream() {
        viewModelScope.launch {
            _viewerState.value.stream?.id?.let { streamId ->
                liveStreamRepository.leaveLiveStream(streamId)
            }
        }
    }

    // Poll for viewer count updates
    private fun startViewerPolling(streamId: String) {
        viewModelScope.launch {
            while (true) { // In real app, stop when screen is destroyed
                val result = liveStreamRepository.getViewerCount(streamId)
                when (result) {
                    is Result.Success -> {
                        _viewerState.update { it.copy(viewerCount = result.data) }
                    }
                    else -> {
                        // Handle error/loading
                    }
                }
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    // Poll for new comments
    private fun startCommentPolling(streamId: String) {
        viewModelScope.launch {
            while (true) {
                // In real app, this would be a WebSocket or real-time listener
                // For now, we'll just simulate
                delay(3000)

                // Simulate new comments from other viewers
                if (_viewerState.value.viewerCount > 0) {
                    val mockComments = listOf(
                        LiveComment(
                            id = "mock_${System.currentTimeMillis()}",
                            username = "viewer_${(1..10).random()}",
                            text = getRandomComment(),
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    _viewerState.update { state ->
                        state.copy(comments = state.comments + mockComments)
                    }
                }
            }
        }
    }

    // Clear error
    fun clearError() {
        _viewerState.update { it.copy(error = null) }
    }

    // Toggle comments visibility
    fun toggleComments() {
        _viewerState.update { it.copy(showComments = !it.showComments) }
    }

    // Clean up when leaving
    override fun onCleared() {
        super.onCleared()
        leaveStream()
    }

    private fun getRandomComment(): String {
        val comments = listOf(
            "Great stream! 👍",
            "Love this! ❤️",
            "Can you show that again?",
            "Amazing content!",
            "🔥🔥🔥",
            "Welcome new viewers!",
            "What game is this?",
            "Nice setup!",
            "How long have you been streaming?",
            "Followed! ✨"
        )
        return comments.random()
    }
}

