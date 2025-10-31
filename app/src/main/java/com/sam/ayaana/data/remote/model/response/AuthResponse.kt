package com.sam.ayaana.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "token") val token: String,
    @Json(name = "user") val user: UserResponse,
    @Json(name = "expires_in") val expiresIn: Long
)