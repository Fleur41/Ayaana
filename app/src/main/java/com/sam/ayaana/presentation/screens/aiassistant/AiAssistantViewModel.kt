package com.sam.ayaana.presentation.screens.aiassistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.domain.model.AiMessage
import com.sam.ayaana.domain.repository.IAiAssistantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val repository: IAiAssistantRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<AiMessage>>(emptyList())
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadInitialGreeting()
    }

    private fun loadInitialGreeting() {
        viewModelScope.launch {
            val greeting = AiMessage(
                id = "welcome",
                content = "Hi! I'm Ayaana AI. I can help you with captions, hashtags, and social media tips. What would you like to know? 🤖",
                isFromUser = false
            )
            _messages.update { listOf(greeting) }
        }
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            repository.sendMessage(message).collect { newMessage ->
                _messages.update { currentMessages ->
                    currentMessages + newMessage
                }
                _isLoading.value = false
            }
        }
    }

    fun clearConversation() {
        viewModelScope.launch {
            repository.clearHistory()
            _messages.value = emptyList()
            loadInitialGreeting()
        }
    }
}