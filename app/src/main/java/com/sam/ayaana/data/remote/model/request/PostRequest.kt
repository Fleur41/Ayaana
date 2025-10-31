package com.sam.ayaana.data.remote.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostRequest(
    @Json(name = "image_url") val imageUrl: String,
    @Json(name = "caption") val caption: String,
    @Json(name = "location") val location: String? = null
)