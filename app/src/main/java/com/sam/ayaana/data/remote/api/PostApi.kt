package com.sam.ayaana.data.remote.api

import com.google.android.gms.common.api.Api
import com.sam.ayaana.data.remote.model.request.PostRequest
import com.sam.ayaana.data.remote.model.response.ApiResponse
import com.sam.ayaana.data.remote.model.response.PostResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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
        @Path("userId") userId: String, // ← FIXED: Changed @Query to @Path
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
}