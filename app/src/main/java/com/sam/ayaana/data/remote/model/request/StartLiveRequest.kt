package com.sam.ayaana.data.remote.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartLiveRequest(
    @Json(name = "title") val title: String?,
    @Json(name = "is_private") val isPrivate: Boolean,
    @Json(name = "tags") val tags: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SendCommentRequest(
    @Json(name = "stream_id") val streamId: String,
    @Json(name = "text") val text: String
)