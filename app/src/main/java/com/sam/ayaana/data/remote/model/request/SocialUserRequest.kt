package com.sam.ayaana.data.remote.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SocialUserRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "username") val username: String,
    @Json(name = "full_name") val fullName: String
)

// USE THIS WHEN CONNECTING TO SPRING BOOT
//@JsonClass(generateAdapter = true)
//data class SocialUserCreateRequest(
//    @Json(name = "firebase_uid") val firebaseUid: String,
//    @Json(name = "email") val email: String,
//    @Json(name = "username") val username: String,
//    @Json(name = "full_name") val fullName: String
//)