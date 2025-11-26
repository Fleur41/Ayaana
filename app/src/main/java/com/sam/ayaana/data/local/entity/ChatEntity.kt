package com.sam.ayaana.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date


@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val username: String,
    val profileImage: String,
    val lastMessage: String,
    val timestamp: Date,
    val unreadCount: Int = 0,
    val isOnline: Boolean = false,
    val messageStatus: String = "SEEN"
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Date,
    val messageType: String = "TEXT",
    val mediaUrl: String? = null,
    val isSentByMe: Boolean = true,
    val messageStatus: String = "SENT"
)