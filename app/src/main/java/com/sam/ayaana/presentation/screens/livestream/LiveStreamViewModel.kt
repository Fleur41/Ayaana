package com.sam.ayaana.presentation.screens.livestream

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.Utils.RTMPStreamer
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.LiveComment
import com.sam.ayaana.domain.model.LiveStreamSession
import com.sam.ayaana.domain.model.LiveStreamState
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
class LiveStreamViewModel @Inject constructor(
    private val liveStreamRepository: ILiveStreamRepository
) : ViewModel() {

    // Live stream state
    private val _liveStreamState = MutableStateFlow(LiveStreamState())
    val liveStreamState: StateFlow<LiveStreamState> = _liveStreamState.asStateFlow()

    // Live stream session (RTMP info)
    private var currentSession: LiveStreamSession? = null
    private var rtmpStreamer: RTMPStreamer? = null

    // Start live stream
    fun startLiveStream(context: Context) {
        viewModelScope.launch {
            _liveStreamState.update { it.copy(isLoading = true, error = null) }

            val result = liveStreamRepository.startLiveStream(
                title = _liveStreamState.value.title,
                isPrivate = _liveStreamState.value.isPrivate
            )

            when (result) {
                is Result.Success -> {
                    currentSession = result.data
                    _liveStreamState.update {
                        it.copy(
                            isLoading = false,
                            isLive = true,
                            session = result.data
                        )
                    }
                    // Start RTMP streaming
                    startRTMPStreaming(context, result.data)
                    // Start polling for viewer count
                    startViewerCountPolling(result.data.streamId)
                }
                is Result.Error -> {
                    _liveStreamState.update {
                        it.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
                is Result.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }

    // End live stream
    fun endLiveStream() {
        viewModelScope.launch {
            _liveStreamState.update { it.copy(isLoading = true, error = null) }

            currentSession?.let { session ->
                val result = liveStreamRepository.endLiveStream(session.streamId)

                when (result) {
                    is Result.Success -> {
                        // Stop RTMP streaming
                        stopRTMPStreaming()
                        // Save recording
                        saveLiveRecording(session.streamId)

                        _liveStreamState.update {
                            it.copy(
                                isLoading = false,
                                isLive = false,
                                session = null
                            )
                        }
                        currentSession = null
                    }
                    is Result.Error -> {
                        _liveStreamState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                    is Result.Loading -> {
                        // Handle loading state if needed
                    }
                }
            }
        }
    }

    // Send live comment
    fun sendLiveComment(comment: String) {
        viewModelScope.launch {
            currentSession?.let { session ->
                val result = liveStreamRepository.sendLiveComment(session.streamId, comment)

                when (result) {
                    is Result.Success -> {
                        // Add comment to local state
                        val newComment = LiveComment(
                            id = System.currentTimeMillis().toString(),
                            username = "You", // Current user
                            text = comment,
                            timestamp = System.currentTimeMillis()
                        )
                        _liveStreamState.update { state ->
                            state.copy(
                                comments = state.comments + newComment
                            )
                        }
                    }
                    is Result.Error -> {
                        _liveStreamState.update {
                            it.copy(error = result.message)
                        }
                    }
                    is Result.Loading -> {
                        // Handle loading state if needed
                    }
                }
            }
        }
    }

    // Update title
    fun updateTitle(title: String) {
        _liveStreamState.update { it.copy(title = title) }
    }

    // Toggle privacy
    fun togglePrivacy() {
        _liveStreamState.update { it.copy(isPrivate = !it.isPrivate) }
    }

    // Show/hide end confirmation dialog
    fun showEndLiveConfirmation() {
        _liveStreamState.update { it.copy(showEndConfirmation = true) }
    }

    fun hideEndLiveConfirmation() {
        _liveStreamState.update { it.copy(showEndConfirmation = false) }
    }

    // Clear error
    fun clearError() {
        _liveStreamState.update { it.copy(error = null) }
    }

    // Expose RTMPStreamer for preview
    fun getRTMPStreamer(): RTMPStreamer? = rtmpStreamer
    // RTMP Streaming implementation
    private fun startRTMPStreaming(context: Context, session: LiveStreamSession) {
        try {
            rtmpStreamer = RTMPStreamer(context).apply {
                // Configure stream
                setVideoConfig(
                    width = 1280,
                    height = 720,
                    fps = 30,
                    bitrate = 2500000
                )
                setAudioConfig(
                    sampleRate = 44100,
                    bitrate = 128000,
                    isStereo = true
                )

                // Start streaming
                startStream(
                    rtmpUrl = session.rtmpUrl,
                    streamKey = session.streamKey,
                    onStreamStarted = {
                        println("RTMP Stream started successfully")
                        viewModelScope.launch {
                            _liveStreamState.update { it.copy(error = null) }
                        }
                    },
                    onStreamError = { error ->
                        println("RTMP Stream error: $error")
                        viewModelScope.launch {
                            _liveStreamState.update { it.copy(error = error) }
                        }
                    },
                    onStreamStopped = {
                        println("RTMP Stream stopped")
                    }
                )
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _liveStreamState.update {
                    it.copy(error = "Failed to start streaming: ${e.message}")
                }
            }
        }
    }

    private fun stopRTMPStreaming() {
        try {
            rtmpStreamer?.stopStream()
            rtmpStreamer = null
        } catch (e: Exception) {
            viewModelScope.launch {
                _liveStreamState.update {
                    it.copy(error = "Failed to stop streaming: ${e.message}")
                }
            }
        }
    }

    private fun startViewerCountPolling(streamId: String) {
        viewModelScope.launch {
            while (_liveStreamState.value.isLive) {
                val result = liveStreamRepository.getViewerCount(streamId)
                when (result) {
                    is Result.Success -> {
                        _liveStreamState.update { it.copy(viewerCount = result.data) }
                    }
                    is Result.Error -> {
                        // Log error but don't stop polling
                        println("Failed to get viewer count: ${result.message}")
                    }
                    is Result.Loading -> {
                        // Handle loading if needed
                    }
                }
                delay(5000) // Poll every 5 seconds
            }
        }
    }

    private fun saveLiveRecording(streamId: String) {
        viewModelScope.launch {
            when (val result = liveStreamRepository.saveLiveRecording(streamId)) {
                is Result.Success -> {
                    println("Live recording saved: ${result.data}")
                }
                is Result.Error -> {
                    println("Failed to save recording: ${result.message}")
                }
                is Result.Loading -> {
                    // Handle loading
                }
            }
        }
    }

    // Clean up resources when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        if (_liveStreamState.value.isLive) {
            stopRTMPStreaming()
        }
    }
}