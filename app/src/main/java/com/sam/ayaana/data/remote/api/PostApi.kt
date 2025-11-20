package com.sam.ayaana.data.remote.api

import com.google.android.gms.common.api.Api
import com.sam.ayaana.data.remote.model.request.PostRequest
import com.sam.ayaana.data.remote.model.response.ApiResponse
import com.sam.ayaana.data.remote.model.response.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface PostApi {
    @GET("posts/feed")
    suspend fun getFeedPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<PostResponse>>

    @GET("posts/explore")
    suspend fun getExplorePosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<PostResponse>>

    @GET("posts/user/{userId}")
    suspend fun getUserPosts(
        @Path("userId") userId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<PostResponse>>

    @GET("posts/{postId}")
    suspend fun getPostById(
        @Path("postId") postId: String
    ): ApiResponse<PostResponse>

    @POST("posts")
    suspend fun createPost(
        @Body request: PostRequest // ← FIXED: Changed PostResponse to PostRequest
    ): ApiResponse<PostResponse>

    @POST("posts/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String
    ): ApiResponse<Boolean>

    @DELETE("posts/{postId}/like")
    suspend fun unlikePost(
        @Path("postId") postId: String
    ): ApiResponse<Boolean>

    @POST("posts/{postId}/repost")
    suspend fun repost(
        @Path("postId") postId: String
    ): ApiResponse<PostResponse>

    @DELETE("posts/{postId}/repost")
    suspend fun deleteRepost(
        @Path("postId") postId: String
    ): ApiResponse<Boolean>

    @DELETE("posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: String
    ): ApiResponse<Boolean>

    // NEW: Save functionality endpoints
    @POST("posts/{postId}/save")
    suspend fun savePost(
        @Path("postId") postId: String
    ): ApiResponse<Boolean>

    @DELETE("posts/{postId}/save")
    suspend fun unsavePost(
        @Path("postId") postId: String
    ): ApiResponse<Boolean>

    @GET("posts/saved")
    suspend fun getSavedPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ApiResponse<List<PostResponse>>

    @Multipart
    @POST("posts")
    suspend fun createPostWithMedia(
        @Part image: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): ApiResponse<PostResponse>

    @Multipart
    @POST("stories")
    suspend fun createStory(
        @Part media: MultipartBody.Part
    ): ApiResponse<PostResponse>

    @Multipart
    @POST("posts/multiple")
    suspend fun createPostWithMultipleMedia(
        @Part media: List<MultipartBody.Part>,
        @Part("caption") caption: RequestBody
    ): ApiResponse<PostResponse>

    @Multipart
    @POST("posts/video")
    suspend fun createVideoPost(
        @Part video: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): ApiResponse<PostResponse>
}