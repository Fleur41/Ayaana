package com.sam.ayaana.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.Chat
import com.sam.ayaana.domain.model.Message
import com.sam.ayaana.domain.repository.IChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: IChatRepository
) : ViewModel() {

    private val _chatsState = MutableStateFlow<Result<List<Chat>>>(Result.Loading)
    val chatsState: StateFlow<Result<List<Chat>>> = _chatsState.asStateFlow()

    private val _messagesState = MutableStateFlow<Result<List<Message>>>(Result.Loading)
    val messagesState: StateFlow<Result<List<Message>>> = _messagesState.asStateFlow()

    private val _selectedChat = MutableStateFlow<Chat?>(null)
    val selectedChat: StateFlow<Chat?> = _selectedChat.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSendingVoice = MutableStateFlow(false)
    val isSendingVoice: StateFlow<Boolean> = _isSendingVoice.asStateFlow()

    init {

        loadChats()
    }

    fun loadChats() {

        viewModelScope.launch {
            chatRepository.getChats().collect { result ->
                _chatsState.value = result
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatId).collect { result ->
                _messagesState.value = result
            }
        }
    }

    fun selectChat(chat: Chat) {
        _selectedChat.value = chat
        loadMessages(chat.id)
        markAsRead(chat.id)
    }

    fun sendMessage(content: String) {
        val currentChat = _selectedChat.value ?: return
        viewModelScope.launch {
            chatRepository.sendTextMessage(currentChat.id, content)
            loadMessages(currentChat.id)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchChats() {
        viewModelScope.launch {
            val query = _searchQuery.value
            if (query.isNotEmpty()) {
                val results = chatRepository.searchChats(query)
                _chatsState.value = Result.Success(results)
            } else {
                loadChats()
            }
        }
    }

    fun clearSelectedChat() {
        _selectedChat.value = null
        _messagesState.value = Result.Loading
    }

    private fun markAsRead(chatId: String) {
        viewModelScope.launch {
            chatRepository.markAsRead(chatId)
        }
    }

    fun sendVoiceMessage(audioFile: File) {
        val currentChat = _selectedChat.value ?: return
        viewModelScope.launch {
            _isSendingVoice.value = true
            val result = chatRepository.sendVoiceMessage(currentChat.id, audioFile)
            _isSendingVoice.value = false

            if (result is Result.Success) {
                // Reload messages to show the new voice message
                loadMessages(currentChat.id)
            }
            // You can handle error case here if needed
        }
    }
}