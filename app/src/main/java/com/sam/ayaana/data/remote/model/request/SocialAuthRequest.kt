package com.sam.ayaana.data.remote.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialAuthRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

// USE THIS WHEN CONNECTING TO SPRING BOOT
//@JsonClass(generateAdapter = true)
//data class SocialAuthRequest(
//    @Json(name = "firebase_uid") val firebaseUid: String,
//    @Json(name = "firebase_token") val firebaseToken: String,
//    @Json(name = "email") val email: String
//)