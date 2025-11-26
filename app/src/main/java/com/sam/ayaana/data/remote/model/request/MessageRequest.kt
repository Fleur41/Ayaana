package com.sam.ayaana.data.remote.model.request

data class MessageRequest(
    val chatId: String,
    val content: String,
    val messageType: String,
    val mediaUrl: String? = null
)