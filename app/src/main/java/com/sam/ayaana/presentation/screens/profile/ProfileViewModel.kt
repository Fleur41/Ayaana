// presentation/screens/profile/ProfileViewModel.kt
package com.sam.ayaana.presentation.screens.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.domain.model.FollowStatus
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.model.PostType
import com.sam.ayaana.domain.model.ProfileTab
import com.sam.ayaana.domain.model.ProfileUiState
import com.sam.ayaana.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfile(userId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                delay(1000) // Simulate API call
                val isCurrentUser = userId == null

                // Mock user data
                val mockUser = createMockUser(isCurrentUser, userId)
                val mockPosts = createMockPosts(isCurrentUser, mockUser)
                val mockTaggedPosts = if (!isCurrentUser) createMockTaggedPosts() else emptyList()
                val mockReels = createMockReels(mockUser)

                _uiState.update {
                    it.copy(
                        user = mockUser,
                        posts = mockPosts,
                        taggedPosts = mockTaggedPosts,
                        reels = mockReels,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load profile"
                    )
                }
            }
        }
    }

    private fun createMockUser(isCurrentUser: Boolean, userId: String?): User {
        return if (isCurrentUser) {
            // My Profile (PHOTO-2025)
            User(
                id = "current_user_123",
                username = "fle_ur41",
                email = "fleur@example.com",
                profilePicture = null,
                fullName = "Fleur Kings",
                bio = null,
                posts = 3,
                followers = 174,
                following = 604,
                isPrivate = false,
                isFollowing = false,
                followStatus = FollowStatus.NOT_FOLLOWING,
                isCurrentUser = true,
                website = null
            )
        } else {
            // Other User Profile (IG2)
            User(
                id = userId ?: "other_user_456",
                username = "andrew",
                email = "andrew@example.com",
                profilePicture = "https://picsum.photos/id/100/200/200",
                fullName = "Andrew Queo",
                bio = "Artist\nDESIGNER\nIsabelle@art.design",
                posts = 174,
                followers = 772000,
                following = 714,
                isPrivate = false,
                isFollowing = false,
                followStatus = FollowStatus.NOT_FOLLOWING,
                isCurrentUser = false,
                website = "isabelle.art.design"
            )
        }
    }

    private fun createMockPosts(isCurrentUser: Boolean, user: User): List<Post> {
        return if (isCurrentUser) {
            (1..3).map { index ->
                Post(
                    id = "post_$index",
                    userId = user.id,
                    username = user.username,
                    userProfileImage = user.profilePicture ?: "",
                    imageUrl = "https://picsum.photos/id/${index + 100}/300/300",
                    caption = "My post #$index",
                    likes = 100 + index * 10,
                    comments = 10 + index,
                    reposts = 2,
                    isLiked = false,
                    isReposted = false,
                    timestamp = System.currentTimeMillis() - (index * 86400000L),
                    type = PostType.ORIGINAL
                )
            }
        } else {
            (1..12).map { index ->
                Post(
                    id = "post_$index",
                    userId = user.id,
                    username = user.username,
                    userProfileImage = user.profilePicture ?: "",
                    imageUrl = "https://picsum.photos/id/${index + 50}/300/300",
                    caption = "Amazing content #$index",
                    likes = 500 + index * 50,
                    comments = 30 + index,
                    reposts = 5,
                    isLiked = false,
                    isReposted = false,
                    timestamp = System.currentTimeMillis() - (index * 86400000L),
                    type = if (index % 4 == 0) PostType.REPOST else PostType.ORIGINAL,
                    videoUrl = if (index % 5 == 0) "video_url" else null
                )
            }
        }
    }

    private fun createMockTaggedPosts(): List<Post> {
        return (1..6).map { index ->
            Post(
                id = "tagged_$index",
                userId = "other_user_$index",
                username = "user_$index",
                userProfileImage = "https://picsum.photos/id/${index + 200}/100/100",
                imageUrl = "https://picsum.photos/id/${index + 300}/300/300",
                caption = "Tagged in this post #$index",
                likes = 200 + index * 20,
                comments = 15 + index,
                reposts = 3,
                isLiked = false,
                isReposted = false,
                timestamp = System.currentTimeMillis() - (index * 172800000L),
                type = PostType.ORIGINAL
            )
        }
    }

    private fun createMockReels(user: User): List<Post> {
        return (1..4).map { index ->
            Post(
                id = "reel_$index",
                userId = user.id,
                username = user.username,
                userProfileImage = user.profilePicture ?: "",
                imageUrl = "https://picsum.photos/id/${index + 400}/300/500",
                caption = "Check out my reel #$index",
                likes = 1000 + index * 100,
                comments = 50 + index,
                reposts = 8,
                isLiked = false,
                isReposted = false,
                timestamp = System.currentTimeMillis() - (index * 259200000L),
                type = PostType.ORIGINAL,
                videoUrl = "video_url_$index"
            )
        }
    }

    fun toggleFollow() {
        viewModelScope.launch {
            _uiState.value.user?.let { currentUser ->
                val newFollowStatus = when (currentUser.followStatus) {
                    FollowStatus.FOLLOWING -> FollowStatus.NOT_FOLLOWING
                    FollowStatus.REQUESTED -> FollowStatus.NOT_FOLLOWING
                    FollowStatus.NOT_FOLLOWING -> {
                        if (currentUser.isPrivate) {
                            FollowStatus.REQUESTED
                        } else {
                            FollowStatus.FOLLOWING
                        }
                    }
                }

                val updatedUser = currentUser.copy(
                    followStatus = newFollowStatus,
                    isFollowing = newFollowStatus == FollowStatus.FOLLOWING,
                    followers = if (newFollowStatus == FollowStatus.FOLLOWING) {
                        currentUser.followers + 1
                    } else if (currentUser.followStatus == FollowStatus.FOLLOWING) {
                        currentUser.followers - 1
                    } else {
                        currentUser.followers
                    }
                )

                _uiState.update { it.copy(user = updatedUser) }
            }
        }
    }

    fun shareProfile(context: Context) {
        _uiState.value.user?.let { user ->
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out ${user.username}'s profile!")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share profile"))
        }
    }

    fun openEmail(context: Context) {  // NO email parameter
        _uiState.value.user?.email?.let { email ->  // Get email from state
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:$email".toUri()
//                data = Uri.parse("mailto:$email")
            }
            context.startActivity(intent)
        }
    }

    fun pickProfileImage(context: Context) {
        viewModelScope.launch {
            _uiState.value.user?.let { currentUser ->
                val updatedUser = currentUser.copy(
                    profilePicture = "https://picsum.photos/id/999/200/200"
                )
                _uiState.update { it.copy(user = updatedUser) }
            }
        }
    }

    fun onTabSelected(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun retry() {
        loadProfile(_uiState.value.user?.id.takeIf { user ->
            !(_uiState.value.user?.isCurrentUser ?: true)
        })
    }

    // UpdateProfileImage
    fun updateProfileImage(imageUri: Uri?) {
        viewModelScope.launch {
            imageUri?.let { uri ->
                _uiState.value.user?.let { currentUser ->
                    // Store the actual Uri, not a mock URL
                    val updatedUser = currentUser.copy(
                        localProfileUri = uri,
                        profilePicture = null // Clear any previous mock URL
                    )
                    _uiState.update { it.copy(user = updatedUser) }
                }
            }
        }
    }

    // Helper function to get the displayable profile image
    fun getProfileImageToDisplay(): Any? {
        return _uiState.value.user?.let { user ->
            // Return local Uri if available, otherwise return remote URL
            user.localProfileUri ?: user.profilePicture
        }
    }
//    fun updateProfileImage(imageUri: Uri?) {
//        viewModelScope.launch {
//            imageUri?.let { uri ->
//                // In real app, you would upload to backend here
//                // For mock data, simulate upload delay
//                delay(1000)
//
//                _uiState.value.user?.let { currentUser ->
//                    // Convert Uri to mock URL for demo
//                    val mockImageUrl = "https://picsum.photos/id/${System.currentTimeMillis() % 1000}/200/200"
//                    val updatedUser = currentUser.copy(
//                        profilePicture = mockImageUrl
//                    )
//                    _uiState.update { it.copy(user = updatedUser) }
//                }
//            }
//        }
//    }
//
//    fun updateUiStateUser(user: User){
//        _uiState.update { it.copy(user = user) }
//    }

}