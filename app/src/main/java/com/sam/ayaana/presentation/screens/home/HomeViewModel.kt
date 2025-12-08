
package com.sam.ayaana.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sam.ayaana.data.paging.ExplorePostPagingSource
import com.sam.ayaana.data.paging.FeedPostPagingSource
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.repository.IPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.repository.IUserRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: IPostRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    val profileImagePath: Flow<String?> = userRepository.currentUserProfileImage
//    private val _currentTimeline = MutableStateFlow(TimelineType.HOME)
//    val currentTimeline = _currentTimeline.asStateFlow()

    // Posts for home timeline (from users you follow) - using getFeedPosts()
    val homePosts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        )
    ) {
        FeedPostPagingSource(postRepository)
    }.flow.cachedIn(viewModelScope)

    // Posts for explore timeline (public/trending content) - using getExplorePosts()
//    val explorePosts: Flow<PagingData<Post>> = Pager(
//        config = PagingConfig(
//            pageSize = 10,
//            enablePlaceholders = false
//        )
//    ) {
//        ExplorePostPagingSource(postRepository)
//    }.flow.cachedIn(viewModelScope)

    // Get current posts based on selected timeline
//    fun getCurrentPosts(): Flow<PagingData<Post>> {
//        return when (_currentTimeline.value) {
//            TimelineType.HOME -> homePosts
//            TimelineType.EXPLORE -> explorePosts
//        }
//    }

    // Switch between home and explore timeline
//    fun switchTimeline(timelineType: TimelineType) {
//        _currentTimeline.value = timelineType
//    }

    fun toggleLike(postId: String, isCurrentlyLiked: Boolean) {
        viewModelScope.launch {
            val result = if (isCurrentlyLiked) {
                postRepository.unlikePost(postId)
            } else {
                postRepository.likePost(postId)
            }

            when (result) {
                is Result.Success -> {
                    val action = if (isCurrentlyLiked) "unliked" else "liked"
                    println("Successfully $action post: $postId")
                }
                is Result.Error -> {
                    val action = if (isCurrentlyLiked) "unlike" else "like"
                    println("Failed to $action post: ${result.message}")
                }
                else -> {}
            }
        }
    }

    // Repost/Unrepost toggle -
    fun toggleRepost(postId: String, isCurrentlyReposted: Boolean) {
        viewModelScope.launch {
            val result = if (isCurrentlyReposted) {
                postRepository.deleteRepost(postId)
            } else {
                postRepository.repostPost(postId)
            }

            when (result) {
                is Result.Success -> {
                    val action = if (isCurrentlyReposted) "unreposted" else "reposted"
                    println("Successfully $action post: $postId")
                }
                is Result.Error -> {
                    val action = if (isCurrentlyReposted) "unrepost" else "repost"
                    println("Failed to $action post: ${result.message}")
                }
                else -> {}
            }
        }
    }
//     Post interaction methods
//    fun likePost(postId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = postRepository.likePost(postId)
//            when (result){
//                is Result.Success -> {
//                    println("Successfully liked post: $postId")
//                }
//                is Result.Error -> {
//                    println("Failed to like post: $postId")
//                }
//                else -> {
//
//                    // println("Unknown error occurred")
//                }
//            }
//        }
//    }
//
//    fun unlikePost(postId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = postRepository.unlikePost(postId)
//            when (result){
//                is Result.Success -> {
//                    println("Successfully unliked post: $postId")
//                }
//                is Result.Error -> {
//                    println("Failed to unlike post: $postId")
//                }
//                else -> {
//                    //Hii sio lazima
//                    // println("Unknown error occurred")
//                }
//            }
//        }
//    }

    fun sharePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Handle share logic
            println("Sharing post: $postId")
        }
    }

    fun savePost(postId: String) {
        viewModelScope.launch {
           when (val result = postRepository.savePost(postId)) {
                is Result.Success -> {
                    println("Successfully saved post: $postId")
                }
                is Result.Error -> {
                    println("Failed to save post: ${result.message}")
                }
                else -> {}
            }
        }
    }

    fun showMoreOptions(postId: String) {
        viewModelScope.launch {
            // Handle more options logic
            println("Showing options for post: $postId")
        }
    }
}

//enum class TimelineType{
//    HOME, EXPLORE
//}