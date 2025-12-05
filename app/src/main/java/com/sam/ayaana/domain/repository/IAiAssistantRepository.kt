package com.sam.ayaana.domain.repository

import com.sam.ayaana.data.service.MockAiService
import com.sam.ayaana.domain.model.AiMessage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

interface IAiAssistantRepository {
    fun sendMessage(message: String): Flow<AiMessage>
    fun getConversationHistory(): Flow<List<AiMessage>>
    fun clearHistory()
}
class AiAssistantRepositoryImpl @Inject constructor() : IAiAssistantRepository {

    private val mockAiService = MockAiService()
    private val conversationHistory = mutableListOf<AiMessage>()

    override fun sendMessage(message: String): Flow<AiMessage> = flow {
        // Add user message to history
        val userMessage = AiMessage(
            id = UUID.randomUUID().toString(),
            content = message,
            isFromUser = true
        )
        conversationHistory.add(userMessage)
        emit(userMessage)

        // Simulate AI thinking
        delay(800)

        // Get AI response
        val aiResponse = mockAiService.processMessage(message)
        val aiMessage = AiMessage(
            id = UUID.randomUUID().toString(),
            content = aiResponse,
            isFromUser = false
        )
        conversationHistory.add(aiMessage)
        emit(aiMessage)
    }

    override fun getConversationHistory(): Flow<List<AiMessage>> = flow {
        emit(conversationHistory.toList())
    }

    override fun clearHistory() {
        conversationHistory.clear()
    }
}