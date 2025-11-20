package com.sam.ayaana.presentation.screens.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.domain.model.CreateType
import com.sam.ayaana.domain.repository.IPostRepository
import com.sam.ayaana.Utils.MediaUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: IPostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreatePostUiState>(CreatePostUiState.Initial)
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    private val _selectedMedia = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMedia: StateFlow<List<Uri>> = _selectedMedia.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    fun selectMedia(uris: List<Uri>) {
        _selectedMedia.value = uris
        _uiState.value = CreatePostUiState.MediaSelected(uris)
    }

    fun createPost(
        context: Context,
        mediaUris: List<Uri>,
        caption: String,
        type: CreateType = CreateType.POST
    ) {
        if (mediaUris.isEmpty()) {
            _uiState.value = CreatePostUiState.Failed("Please select media to upload")
            return
        }

        _uiState.value = CreatePostUiState.Loading(0f)

        viewModelScope.launch {
            try {
                // Convert all URIs to files
                val mediaFiles = mediaUris.mapNotNull { uri ->
                    MediaUtils.uriToFile(context, uri)
                }

                if (mediaFiles.isEmpty()) {
                    _uiState.value = CreatePostUiState.Failed("Failed to process media files")
                    return@launch
                }

                // Validate media types based on post type
                val validationResult = validateMediaTypes(context, mediaUris, type)
                if (!validationResult.isValid) {
                    _uiState.value = CreatePostUiState.Failed(validationResult.errorMessage)
                    // Clean up files
                    mediaFiles.forEach { it.delete() }
                    return@launch
                }

                // Update progress - file processing complete
                _uploadProgress.value = 0.3f
                _uiState.value = CreatePostUiState.Loading(0.3f)

                val result = when (type) {
                    CreateType.POST -> {
                        when {
                            // Single image post
                            mediaFiles.size == 1 && MediaUtils.isImageUri(context.contentResolver, mediaUris.first()) -> {
                                postRepository.createPostWithMedia(mediaFiles.first(), caption)
                            }
                            // Multiple media post (images + videos)
                            mediaFiles.size > 1 -> {
                                postRepository.createPostWithMultipleMedia(mediaFiles, caption)
                            }
                            // Single video post
                            mediaFiles.size == 1 && MediaUtils.isVideoUri(context.contentResolver, mediaUris.first()) -> {
                                postRepository.createVideoPost(mediaFiles.first(), caption)
                            }
                            else -> {
                                com.sam.ayaana.Utils.Result.Error("Unsupported media combination")
                            }
                        }
                    }
                    CreateType.STORY -> {
                        // Stories support both images and videos, but single file only
                        if (mediaFiles.size == 1) {
                            postRepository.createStory(mediaFiles.first())
                        } else {
                            com.sam.ayaana.Utils.Result.Error("Stories can only contain one media file")
                        }
                    }
                    CreateType.REEL -> {
                        // Reels are video-only, single file
                        if (mediaFiles.size == 1 && MediaUtils.isVideoUri(context.contentResolver, mediaUris.first())) {
                            postRepository.createReel(mediaFiles.first(), caption)
                        } else {
                            com.sam.ayaana.Utils.Result.Error("Reels must be a single video file")
                        }
                    }
                    CreateType.LIVE -> {
                        // Live streaming implementation would go here
                        com.sam.ayaana.Utils.Result.Error("Live streaming not implemented yet")
                    }
                }

                // Update progress - upload complete
                _uploadProgress.value = 1.0f
                _uiState.value = CreatePostUiState.Loading(1.0f)

                when (result) {
                    is com.sam.ayaana.Utils.Result.Success -> {
                        // Clean up temporary files
                        mediaFiles.forEach { it.delete() }

                        _uiState.value = CreatePostUiState.Success
                        clearSelection()
                    }
                    is com.sam.ayaana.Utils.Result.Error -> {
                        _uiState.value = CreatePostUiState.Failed(result.message)
                        // Clean up temporary files on error too
                        mediaFiles.forEach { it.delete() }
                    }
                    else -> {
                        _uiState.value = CreatePostUiState.Failed("Unknown error occurred")
                        mediaFiles.forEach { it.delete() }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = CreatePostUiState.Failed(e.message ?: "Upload failed")
            }
        }
    }

    private fun validateMediaTypes(context: Context, mediaUris: List<Uri>, type: CreateType): ValidationResult {
        return when (type) {
            CreateType.POST -> {
                // Posts support multiple images and videos
                if (mediaUris.size > 10) {
                    ValidationResult(false, "Maximum 10 media files allowed per post")
                } else {
                    ValidationResult(true, "")
                }
            }
            CreateType.STORY -> {
                // Stories support single image or video
                if (mediaUris.size != 1) {
                    ValidationResult(false, "Stories can only contain one media file")
                } else {
                    ValidationResult(true, "")
                }
            }
            CreateType.REEL -> {
                // Reels support single video only
                if (mediaUris.size != 1) {
                    ValidationResult(false, "Reels can only contain one video file")
                } else if (!MediaUtils.isVideoUri(context.contentResolver, mediaUris.first())) {
                    ValidationResult(false, "Reels must be a video file")
                } else {
                    ValidationResult(true, "")
                }
            }
            CreateType.LIVE -> {
                ValidationResult(false, "Live streaming validation not implemented")
            }
        }
    }

    fun clearSelection() {
        _selectedMedia.value = emptyList()
        _uiState.value = CreatePostUiState.Initial
        _uploadProgress.value = 0f
    }

    fun clearError() {
        if (_uiState.value is CreatePostUiState.Failed) {
            _uiState.value = CreatePostUiState.Initial
        }
    }
}

// Helper class for validation results
data class ValidationResult(val isValid: Boolean, val errorMessage: String)

sealed class CreatePostUiState {
    object Initial : CreatePostUiState()
    data class MediaSelected(val uris: List<Uri>) : CreatePostUiState()
    data class Loading(val progress: Float) : CreatePostUiState()
    object Success : CreatePostUiState()
    data class Failed(val message: String) : CreatePostUiState()
}