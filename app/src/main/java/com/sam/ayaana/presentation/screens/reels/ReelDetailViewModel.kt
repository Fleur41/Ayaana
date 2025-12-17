// presentation/screens/reels/ReelDetailViewModel.kt
package com.sam.ayaana.presentation.screens.reels

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.Utils.DownloadState
import com.sam.ayaana.domain.model.Reel
import com.sam.ayaana.domain.repository.IReelsRepository
import com.sam.ayaana.Utils.MediaStoreDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReelDetailViewModel @Inject constructor(
    private val reelsRepository: IReelsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReelDetailUiState())
    val uiState: StateFlow<ReelDetailUiState> = _uiState.asStateFlow()

    // FIXED: Add download state flow
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private val reelId: String = savedStateHandle["reelId"] ?: ""

    fun loadReel() {
        if (reelId.isEmpty()) {
            _uiState.update { it.copy(error = "Invalid reel ID") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val reels = reelsRepository.getReels()
                val reel = reels.find { it.id == reelId }

                if (reel != null) {
                    _uiState.update {
                        it.copy(
                            reel = reel,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Reel not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load reel"
                    )
                }
            }
        }
    }

    fun likeReel() {
        viewModelScope.launch {
            if (reelId.isNotEmpty()) {
                reelsRepository.likeReel(reelId, uiState.value.reel?.isLiked != true)
                // Reload reel to get updated state
                loadReel()
            }
        }
    }

    // FIXED: Simplified download function without @Composable
    fun downloadReelThumbnail(
        context: Context,
        reel: Reel
    ) {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Downloading
            try {
                val result = MediaStoreDownloader.downloadReelThumbnail(
                    context = context,
                    imageUrl = reel.thumbnailUrl,
                    title = reel.title,
                    description = reel.description
                )
                _downloadState.value = when (result) {
                    MediaStoreDownloader.DownloadResult.Success -> DownloadState.Success
                    is MediaStoreDownloader.DownloadResult.Failure ->
                        DownloadState.Failed(result.exception.message ?: "Download failed")
                }
            } catch (e: Exception) {
                _downloadState.value = DownloadState.Failed(e.message ?: "Download failed")
            }
        }
    }
}

// FIXED: Data class in separate section
data class ReelDetailUiState(
    val reel: Reel? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

