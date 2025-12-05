package com.sam.ayaana.domain.model

data class Hashtag(
    val tag: String,
    val relevanceScore: Double = 0.0,
    val category: String = "General"
)

