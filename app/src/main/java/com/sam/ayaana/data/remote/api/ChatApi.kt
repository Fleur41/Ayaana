package com.sam.ayaana.data.remote.api

import com.sam.ayaana.data.remote.model.request.MessageRequest
import com.sam.ayaana.data.remote.model.response.ApiResponse
import com.sam.ayaana.data.remote.model.response.ChatResponse
import com.sam.ayaana.data.remote.model.response.MessageResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {

    @GET("chats")
    suspend fun getChats(): ApiResponse<List<ChatResponse>>

    @GET("chats/{chatId}/messages")
    suspend fun getMessages(@Path("chatId") chatId: String): ApiResponse<List<MessageResponse>>

    @POST("chats/{chatId}/messages")
    suspend fun sendMessage(
        @Path("chatId") chatId: String,
        @Body message: MessageRequest
    ): ApiResponse<MessageResponse>

    @POST("chats/{chatId}/read")
    suspend fun markAsRead(@Path("chatId") chatId: String): ApiResponse<Unit>

    @GET("chats/search")
    suspend fun searchChats(@Query("query") query: String): ApiResponse<List<ChatResponse>>
}