package com.sam.ayaana.presentation.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.AiMessage
import com.sam.ayaana.domain.model.Chat
import com.sam.ayaana.domain.model.Message
import com.sam.ayaana.domain.repository.IChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    // FIXED: Use MutableList instead of List
    private var allChats: MutableList<Chat> = mutableListOf()

    private val _filteredChats = MutableStateFlow<List<Chat>>(emptyList())
    val filteredChats: StateFlow<List<Chat>> = _filteredChats.asStateFlow()

    private val _aiMessages = MutableStateFlow<List<AiMessage>>(emptyList())
    val aiMessages: StateFlow<List<AiMessage>> = _aiMessages.asStateFlow()

    private val _isAiResponding = MutableStateFlow(false)
    val isAiResponding: StateFlow<Boolean> = _isAiResponding.asStateFlow()

    val hasSearchResults: Boolean
        get() = _searchQuery.value.isNotEmpty() && _filteredChats.value.isEmpty()

    val searchResultMessage: String
        get() = if (_searchQuery.value.isNotEmpty() && _filteredChats.value.isEmpty()) {
            "No results found for \"${_searchQuery.value}\""
        } else {
            ""
        }

    init {
        loadChats()
        setupSearchDebounce()
    }

    fun loadChats() {
        viewModelScope.launch {
            chatRepository.getChats().collect { result ->
                when (result) {
                    is Result.Success -> {
                        _chatsState.value = result
                        // FIXED: Use MutableList operations
                        allChats.clear()
                        allChats.addAll(result.data)
                        _filteredChats.value = result.data
                        println("✅ DEBUG: Loaded ${allChats.size} chats into allChats")
                        println("✅ DEBUG: First chat: ${allChats.firstOrNull()?.username}")
                    }
                    is Result.Error -> {
                        _chatsState.value = result
                        println("❌ DEBUG: Error loading chats: ${result.message}")
                    }
                    is Result.Loading -> {
                        _chatsState.value = result
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    println("🔍 DEBUG: Starting search for: '$query', allChats size: ${allChats.size}")
                    performSearch(query)
                }
        }
    }

    private fun performSearch(query: String) {
        println("🔍 DEBUG: performSearch called with: '$query'")
        println("🔍 DEBUG: allChats contains: ${allChats.map { it.username }}")

        if (query.isEmpty()) {
            _filteredChats.value = allChats
            println("🔍 DEBUG: Empty query, showing all ${allChats.size} chats")
        } else {
            val filtered = allChats.filter { chat ->
                val matches = chat.username.contains(query, ignoreCase = true) ||
                        chat.lastMessage.contains(query, ignoreCase = true)
                if (matches) {
                    println("✅ DEBUG: Found match: ${chat.username}")
                }
                matches
            }
            _filteredChats.value = filtered
            println("🔍 DEBUG: Found ${filtered.size} matches for '$query'")
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _filteredChats.value = allChats
        println("🔍 DEBUG: Search cleared, showing all ${allChats.size} chats")
    }

    fun updateSearchQuery(query: String) {
        println("🔍 DEBUG: updateSearchQuery called with: '$query'")
        _searchQuery.value = query
    }

    // FIXED: Renamed to avoid conflict with searchChats() below
    fun triggerSearch() {
        // Manual search trigger if needed
        performSearch(_searchQuery.value)
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

    // FIXED: Remove duplicate searchChats method (we're using triggerSearch instead)

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

    // sendAiQuery
    fun sendAiQuery(query: String) {
        viewModelScope.launch {
            _isAiResponding.value = true

            // Add user message
            val userMessage = AiMessage(
                id = System.currentTimeMillis().toString(),
                content = query,
                isFromUser = true,
                timestamp = System.currentTimeMillis()
            )

            _aiMessages.value += userMessage

            // Simulate AI response (replace with real AI later)
            delay(1000) // Simulate AI processing

            val aiResponse = AiMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                content = "I'm Ayaana AI! You asked: \"$query\". This is a mock response. When we integrate real AI, I'll provide helpful answers!",
                isFromUser = false,
                timestamp = System.currentTimeMillis()
            )

            _aiMessages.value += aiResponse
            _isAiResponding.value = false
        }
    }

    fun clearAiConversation() {
        _aiMessages.value = emptyList()
    }
}
