package com.sam.ayaana.data.remote.api


import com.sam.ayaana.data.remote.model.request.SendCommentRequest
import com.sam.ayaana.data.remote.model.request.StartLiveRequest
import com.sam.ayaana.data.remote.model.response.ApiResponse
import com.sam.ayaana.data.remote.model.response.LiveCommentResponse
import com.sam.ayaana.data.remote.model.response.LiveStreamResponse
import com.sam.ayaana.data.remote.model.response.LiveStreamSessionResponse
import retrofit2.http.*

interface LiveStreamApi {

    @POST("live/start")
    suspend fun startLiveStream(
        @Body request: StartLiveRequest
    ): ApiResponse<LiveStreamSessionResponse>

    @POST("live/{streamId}/end")
    suspend fun endLiveStream(
        @Path("streamId") streamId: String
    ): ApiResponse<Unit>

    @GET("live")
    suspend fun getLiveStreams(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<LiveStreamResponse>>

    @GET("live/{streamId}")
    suspend fun getLiveStreamById(
        @Path("streamId") streamId: String
    ): ApiResponse<LiveStreamResponse>

    @POST("live/{streamId}/join")
    suspend fun joinLiveStream(
        @Path("streamId") streamId: String
    ): ApiResponse<Unit>

    @POST("live/{streamId}/leave")
    suspend fun leaveLiveStream(
        @Path("streamId") streamId: String
    ): ApiResponse<Unit>

    @POST("live/{streamId}/comment")
    suspend fun sendLiveComment(
        @Path("streamId") streamId: String,
        @Body request: SendCommentRequest
    ): ApiResponse<Unit>

    @GET("live/{streamId}/comments")
    suspend fun getLiveComments(
        @Path("streamId") streamId: String
    ): ApiResponse<List<LiveCommentResponse>>

    @GET("live/{streamId}/viewers")
    suspend fun getViewerCount(
        @Path("streamId") streamId: String
    ): ApiResponse<Int>

    @POST("live/{streamId}/save-recording")
    suspend fun saveLiveRecording(
        @Path("streamId") streamId: String
    ): ApiResponse<String>
}