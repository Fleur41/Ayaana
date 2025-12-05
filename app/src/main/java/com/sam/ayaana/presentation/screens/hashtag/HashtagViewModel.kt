package com.sam.ayaana.presentation.screens.hashtag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.domain.model.Hashtag
import com.sam.ayaana.domain.model.HashtagRequest
import com.sam.ayaana.domain.model.HashtagUiState
import com.sam.ayaana.domain.repository.IHashtagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HashtagViewModel @Inject constructor(
    private val repository: IHashtagRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HashtagUiState())
    val uiState: StateFlow<HashtagUiState> = _uiState.asStateFlow()

    fun generateHashtags(text: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = repository.generateHashtags(
                HashtagRequest(text = text, maxResults = 15)
            )

            result.fold(
                onSuccess = { hashtags ->
                    _uiState.update {
                        it.copy(
                            hashtags = hashtags,
                            isLoading = false,
                            generatedText = text
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            error = error.message ?: "Failed to generate hashtags",
                            isLoading = false
                        )
                    }
                }
            )
        }
    }

    fun copyToClipboard(hashtag: String) {
        // This will be handled in the UI layer
        _uiState.update { it.copy(copiedHashtag = hashtag) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}