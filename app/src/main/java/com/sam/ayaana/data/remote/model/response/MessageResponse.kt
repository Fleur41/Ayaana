package com.sam.ayaana.data.remote.model.response


data class MessageResponse(
    val id: String,
    val chatId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: String,
    val messageType: String,
    val mediaUrl: String?,
    val messageStatus: String
)

data class ChatResponse(
    val id: String,
    val userId: String,
    val username: String,
    val profileImage: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isOnline: Boolean,
    val messageStatus: String
)
