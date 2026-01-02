package com.sam.ayaana.domain.model

data class LiveComment(
    val id: String,
    val username: String,
    val text: String,
    val timestamp: Long
)