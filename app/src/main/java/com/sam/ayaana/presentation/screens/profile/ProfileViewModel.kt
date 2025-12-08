// presentation/screens/profile/ProfileViewModel.kt
package com.sam.ayaana.presentation.screens.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.Utils.ImageUtils
import com.sam.ayaana.datastore.DatastoreRepository
import com.sam.ayaana.domain.model.FollowStatus
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.model.PostType
import com.sam.ayaana.domain.model.ProfileTab
import com.sam.ayaana.domain.model.ProfileUiState
import com.sam.ayaana.domain.model.User
import com.sam.ayaana.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val datastoreRepository: DatastoreRepository,
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    // In ProfileViewModel.kt - UPDATE loadProfile()
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

                // CRITICAL: Load saved image AFTER user is loaded
                loadSavedProfileImage()

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

    fun onTabSelected(tab: ProfileTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun retry() {
        loadProfile(_uiState.value.user?.id.takeIf { user ->
            !(_uiState.value.user?.isCurrentUser ?: true)
        })
    }

    // Load saved profile image from Datastore
    fun loadSavedProfileImage() {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Loading saved profile image...")

            val savedPath = datastoreRepository.getProfileImagePath()
            Log.d("ProfileViewModel", "Saved path from Datastore: $savedPath")

            if (savedPath != null && File(savedPath).exists()) {
                Log.d("ProfileViewModel", "File exists at path: $savedPath")

                // Wait a bit if user is null (might still be loading)
                var retries = 0
                while (_uiState.value.user == null && retries < 10) {
                    delay(100)
                    retries++
                    Log.d("ProfileViewModel", "Waiting for user to load... retry $retries")
                }

                if (_uiState.value.user != null) {
                    updateUiWithLocalImage(savedPath)
                } else {
                    Log.d("ProfileViewModel", "User still null after waiting, cannot update image")
                }
            } else {
                Log.d("ProfileViewModel", "No saved image found")
            }
        }
    }

    // NEW: Helper to update UI with local image
    // In ProfileViewModel.kt - Update updateUiWithLocalImage
    private fun updateUiWithLocalImage(imagePath: String) {

        val file = File(imagePath)

        if (file.exists()) {
            val permanentUri = Uri.fromFile(file)

            // Convert to string for storage (Uri might not be serializable)
            val uriString = permanentUri.toString()
            Log.d("ProfileViewModel", "Uri as string: $uriString")

            _uiState.value.user?.let { currentUser ->
                Log.d("ProfileViewModel", "Current user: ${currentUser.username}")

                // Store as String, not Uri (Uri might have issues with state updates)
                val updatedUser = currentUser.copy(
                    localProfileUri = permanentUri, // Keep as Uri for now
                    profilePicture = null
                )
                Log.d("ProfileViewModel", "Updated user localProfileUri: ${updatedUser.localProfileUri}")

                // Force a new state object
                _uiState.update { currentState ->
                    currentState.copy(
                        user = updatedUser,
                        posts = currentState.posts, // Keep existing posts
                        taggedPosts = currentState.taggedPosts,
                        reels = currentState.reels
                    )
                }
            } ?: run {
                Log.d("ProfileViewModel", "Current user is null!")
            }
        } else {
            Log.d("ProfileViewModel", "File does not exist at path: $imagePath")
        }
    }

    // UPDATED: Save image permanently
    fun updateProfileImage(imageUri: Uri?) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Updating profile image with Uri: $imageUri")
            imageUri?.let { uri ->
                // 1. Copy image to app's private storage
                val imagePath = ImageUtils.copyImageToAppStorage(context, uri)
                Log.d("ProfileViewModel", "Copied image to path: $imagePath")

                imagePath?.let { path ->
                    // 2. Clean up old images (keep only current)
                    ImageUtils.cleanUpOldProfileImages(context, path)

                    userRepository.updateCurrentUserProfileImage(path)
                    // 3. Save path to Datastore (persistent storage)
                    // Log.d("ProfileViewModel", "Saving path to Datastore: $path")
                    // datastoreRepository.saveProfileImagePath(path)

                    // 4. Update UI with the permanent Uri
                    updateUiWithLocalImage(path)
                }
            }
        }
    }

}