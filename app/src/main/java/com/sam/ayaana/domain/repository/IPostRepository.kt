package com.sam.ayaana.domain.repository

import com.sam.ayaana.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface IPostRepository{
    suspend fun getFeedPosts(): Flow<Result<List<Post>>>
    suspend fun getUserPosts(userId: String): Flow<Result<List<Post>>>
    suspend fun getExplorePosts(): Flow<Result<List<Post>>>
    suspend fun createPost(imageUrl: String, caption: String): Result<Post>
    suspend fun likePost(postId: String): Result<Boolean>
    suspend fun unlikePost(postId: String): Result<Boolean>
    suspend fun repostPost(postId: String): Result<Post>
    suspend fun deleteRepost(postId: String): Result<Boolean>
    suspend fun deletePost(postId: String): Result<Boolean>
    suspend fun getPostById(postId: String): Flow<Result<Post>>





}