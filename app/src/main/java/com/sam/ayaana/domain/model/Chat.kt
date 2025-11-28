package com.sam.ayaana.domain.model

import java.util.Date

data class Chat(
    val id: String,
    val userId: String,
    val username: String,
    val profileImage: String,
    val lastMessage: String,
    val timestamp: Date,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val messageStatus: MessageStatus = MessageStatus.SEEN
)

data class Message(
    val id: String,
    val chatId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Date,
    val messageType: MessageType = MessageType.TEXT,
    val mediaUrl: String? = null,
    val filePath: String? = null,
    val isSentByMe: Boolean = true,
    val messageStatus: MessageStatus = MessageStatus.SENT
)

enum class MessageType {
    TEXT, IMAGE, VIDEO, VOICE, LOCATION
}

enum class MessageStatus {
    SENT, DELIVERED, SEEN
}

