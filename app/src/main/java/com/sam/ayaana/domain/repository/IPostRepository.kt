package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.toPost
import com.sam.ayaana.Utils.toPostEntity
import com.sam.ayaana.data.local.dao.PostDao
import com.sam.ayaana.data.remote.api.PostApi
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.data.remote.model.request.PostRequest
import com.sam.ayaana.domain.model.Post
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

    // ADD THESE FOR SAVE FUNCTIONALITY:
    suspend fun savePost(postId: String): Result<Boolean>
    suspend fun unsavePost(postId: String): Result<Boolean>
    suspend fun getSavedPosts(): Flow<Result<List<Post>>>
    suspend fun createPostWithMedia(imageFile: File, caption: String): Result<Post>
    suspend fun createStory(mediaFile: File): Result<Post>
    suspend fun createReel(videoFile: File, caption: String): Result<Post>
    suspend fun createPostWithMultipleMedia(mediaFiles: List<File>, caption: String): Result<Post>
    suspend fun createVideoPost(videoFile: File, caption: String): Result<Post>
}

class PostRepositoryImpl @Inject constructor(
    private val postApi: PostApi,
    private val postDao: PostDao
) : IPostRepository {
    override suspend fun getFeedPosts(): Flow<Result<List<Post>>> = flow{
        emit(Result.Loading)
        try {
            // First try to get from local database
            val localPosts = postDao.getPosts()
            localPosts.collect { posts ->
                if (posts.isNotEmpty()) {
                    emit(Result.Success(posts.map { it.toPost() }))
                }
            }

            // Then fetch from API and update local database
            val response = postApi.getFeedPosts()
            if (response.success && response.data != null) {
                val postEntities = response.data.map { it.toPostEntity() }
                postDao.insertPosts(postEntities)
                emit(Result.Success(response.data.map { it.toPost() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load posts"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun getUserPosts(userId: String): Flow<Result<List<Post>>> = flow {
        emit(Result.Loading)
        try {
            val response = postApi.getUserPosts(userId)
            if (response.success && response.data != null) {
                val postEntities = response.data.map { it.toPostEntity() }
                postDao.insertPosts(postEntities)
                emit(Result.Success(response.data.map { it.toPost() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load user posts"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun getExplorePosts(): Flow<Result<List<Post>>> = flow {
        emit(Result.Loading)
        try {
            val response = postApi.getExplorePosts()
            if (response.success && response.data != null) {
                val postEntities = response.data.map { it.toPostEntity() }
                postDao.insertPosts(postEntities)
                emit(Result.Success(response.data.map { it.toPost() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load explore posts"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun createPost(imageUrl: String, caption: String): Result<Post> {
        return try {
            val response = postApi.createPost(
                PostRequest(
                    imageUrl = imageUrl,
                    caption = caption
                )
            )
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                Result.Success(response.data.toPost())
            } else {
                Result.Error(response.message ?: "Failed to create post")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun likePost(postId: String): Result<Boolean> {
        return try {
            val response = postApi.likePost(postId)
            if (response.success) {
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to like post")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun unlikePost(postId: String): Result<Boolean> {
        return try {
            val response = postApi.unlikePost(postId)
            if (response.success) {
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to unlike post")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun repostPost(postId: String): Result<Post> {
        return try {
            val response = postApi.repost(postId)
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                Result.Success(response.data.toPost())
            } else {
                Result.Error(response.message ?: "Failed to repost")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun deleteRepost(postId: String): Result<Boolean> {
        return try {
            val response = postApi.deleteRepost(postId)
            if (response.success) {
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to delete repost")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun deletePost(postId: String): Result<Boolean> {
        return try {
            val response = postApi.deletePost(postId)
            if (response.success) {
                postDao.deletePost(postId)
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to delete post")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getPostById(postId: String): Flow<Result<Post>> = flow {
        emit(Result.Loading)
        try {
            val response = postApi.getPostById(postId)
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                emit(Result.Success(response.data.toPost()))
            } else {
                emit(Result.Error(response.message ?: "Failed to load post"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error"))
        }
    }

    override suspend fun savePost(postId: String): Result<Boolean> {
        return try {
            val response = postApi.savePost(postId)
            if (response.success) {
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to save post")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error while saving post")
        }
    }

    override suspend fun unsavePost(postId: String): Result<Boolean> {
        return try {
            val response = postApi.unsavePost(postId)
            if (response.success) {
                Result.Success(true)
            } else {
                Result.Error(response.message ?: "Failed to unsave post")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error while unsaving post")
        }
    }

    override suspend fun getSavedPosts(): Flow<Result<List<Post>>> = flow {
        emit(Result.Loading)
        try {
            val response = postApi.getSavedPosts()
            if (response.success && response.data != null) {
                val postEntities = response.data.map { it.toPostEntity() }
                postDao.insertPosts(postEntities)
                emit(Result.Success(response.data.map { it.toPost() }))
            } else {
                emit(Result.Error(response.message ?: "Failed to load saved posts"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Network error while loading saved posts"))
        }
    }

    override suspend fun createPostWithMedia(
        imageFile: File,
        caption: String
    ): Result<Post> {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
            val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = postApi.createPostWithMedia(imagePart, captionBody)
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                Result.Success(response.data.toPost())
            } else {
                Result.Error(response.message ?: "Failed to create post with media")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error during media upload")
        }
    }

    override suspend fun createStory(mediaFile: File): Result<Post> {
        return try {
            val requestFile = mediaFile.asRequestBody("image/*".toMediaTypeOrNull())
            val mediaPart = MultipartBody.Part.createFormData("media", mediaFile.name, requestFile)

            val response = postApi.createStory(mediaPart)
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                Result.Success(response.data.toPost())
            } else {
                Result.Error(response.message ?: "Failed to create story")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error during story upload")
        }
    }

    override suspend fun createReel(
        videoFile: File,
        caption: String
    ): Result<Post> {
        return try {
            val requestFile = videoFile.asRequestBody("video/*".toMediaTypeOrNull())
            val videoPart = MultipartBody.Part.createFormData("video", videoFile.name, requestFile)
            val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())

            // Assuming similar API endpoint for reels
            val response = postApi.createPostWithMedia(videoPart, captionBody)
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                Result.Success(response.data.toPost())
            } else {
                Result.Error(response.message ?: "Failed to create reel")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error during reel upload")
        }
    }

    override suspend fun createPostWithMultipleMedia(
        mediaFiles: List<File>,
        caption: String
    ): Result<Post> {
        return try {
            val mediaParts = mediaFiles.mapIndexed { index, file ->
                val mimeType = if (file.extension.lowercase() in listOf("jpg", "jpeg", "png", "gif")) {
                    "image/*"
                } else {
                    "video/*"
                }
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                MultipartBody.Part.createFormData("media_$index", file.name, requestFile)
            }

            val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())

            // Assuming your API supports multiple media files
            val response = postApi.createPostWithMultipleMedia(mediaParts, captionBody)
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                Result.Success(response.data.toPost())
            } else {
                Result.Error(response.message ?: "Failed to create post with multiple media")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error during multi-media upload")
        }
    }

    override suspend fun createVideoPost(
        videoFile: File,
        caption: String
    ): Result<Post> {
        return try {
            val requestFile = videoFile.asRequestBody("video/*".toMediaTypeOrNull())
            val videoPart = MultipartBody.Part.createFormData("video", videoFile.name, requestFile)
            val captionBody = caption.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = postApi.createVideoPost(videoPart, captionBody)
            if (response.success && response.data != null) {
                val postEntity = response.data.toPostEntity()
                postDao.insertPost(postEntity)
                Result.Success(response.data.toPost())
            } else {
                Result.Error(response.message ?: "Failed to create video post")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error during video upload")
        }
    }

}
