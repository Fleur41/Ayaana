
package com.sam.ayaana.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sam.ayaana.data.paging.FeedPostPagingSource
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.repository.IPostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.StoryUiModel
import com.sam.ayaana.domain.model.StoryViewState
import com.sam.ayaana.domain.repository.IUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: IPostRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _storyState = MutableStateFlow(StoryViewState())
    val storyState: StateFlow<StoryViewState> = _storyState.asStateFlow()
    val profileImagePath: Flow<String?> = userRepository.currentUserProfileImage

    // Posts for home timeline (from users you follow) - using getFeedPosts()
    val homePosts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false
        )
    ) {
        FeedPostPagingSource(postRepository)
    }.flow.cachedIn(viewModelScope)


    private val usernames = listOf(
        "natasha_mukami", "_wanjau_", "shawn_kip", "hadiya_rajesh", "dr_magnito",
        "arjun_vyas", "sauro_bhat", "pauline_claude", "john_doe", "jane_smith",
        "leonard_otie", "emily_davis", "joan_johnson", "olivia_wilson", "william_brown",
        "sarah_jones", "michael_taylor", "lisa_anderson", "david_miller", "maria_garcia"
    )

    init {
        loadStories()
    }

    // Load stories with your existing picsum link
    private fun loadStories() {
        viewModelScope.launch {
            val stories = usernames.mapIndexed { index, username ->
                val storyId = index + 1
                StoryUiModel(
                    id = storyId,
                    username = username,
                    // Profile image (small version)
                    profileImageUrl = "https://picsum.photos/id/${storyId}/200/300",
                    // Story image (larger version for full view)
                    storyImageUrl = "https://picsum.photos/id/${storyId + 100}/800/1400", // Different ID for larger image
                    isViewed = false
                )
            }

            _storyState.update { it.copy(stories = stories) }
        }
    }

    // Handle story click
    fun onStoryClick(storyId: Int) {
        viewModelScope.launch {
            val story = _storyState.value.stories.find { it.id == storyId }
            story?.let {
                // Mark as viewed
                val updatedStories = _storyState.value.stories.map { storyItem ->
                    if (storyItem.id == storyId) {
                        storyItem.copy(isViewed = true)
                    } else {
                        storyItem
                    }
                }

                _storyState.update { state ->
                    state.copy(
                        stories = updatedStories,
                        selectedStory = it,
                        isStoryViewerVisible = true
                    )
                }
            }
        }
    }

    // Close story viewer
    fun closeStoryViewer() {
        _storyState.update { it.copy(
            isStoryViewerVisible = false,
            selectedStory = null)
        }
    }
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