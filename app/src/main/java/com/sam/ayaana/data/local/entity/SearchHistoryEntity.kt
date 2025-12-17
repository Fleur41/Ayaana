package com.sam.ayaana.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val id: String,
    val query: String,
    val timestamp: Long
)