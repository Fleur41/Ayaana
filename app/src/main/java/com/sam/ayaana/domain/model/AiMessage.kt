package com.sam.ayaana.domain.model

import java.util.Date

data class AiMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

enum class AiCommand {
    GREETING,
    HELP,
    ABOUT_APP,
    WEATHER,
    JOKE,
    UNKNOWN
}