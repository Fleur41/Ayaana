package com.sam.ayaana.domain.model

import java.util.Date

data class FollowRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val fullName: String,
    val profileImage: String? = null,
    val mutualFollowers: List<String> = emptyList(),
    val timestamp: Date
)