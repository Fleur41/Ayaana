package com.sam.ayaana.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.model.User
import com.sam.ayaana.domain.repository.IPostRepository
import com.sam.ayaana.domain.repository.IRecentSearchesRepository
import com.sam.ayaana.domain.repository.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.sam.ayaana.Utils.Result

// Search State data class
data class SearchState(
    val searchQuery: String = "",
    val users: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val postRepository: IPostRepository,
    private val recentSearchesRepository: IRecentSearchesRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    // FIXED: Create mock data that will be used for search
    private val mockUsers = listOf(
        User(
            id = "1",
            username = "andrew_queo",
            email = "isabelle@art.design",
            profilePicture = null,
            fullName = "Andrew Queo",
            bio = "Artist | DESIGNER",
            posts = 174,
            followers = 772000,
            following = 714,
            isPrivate = false,
            isFollowing = false
        ),
        User(
            id = "2",
            username = "sarah_m",
            email = "sarah@design.com",
            profilePicture = null,
            fullName = "Sarah Mitchell",
            bio = "UI/UX Designer | Photography",
            posts = 89,
            followers = 15400,
            following = 342,
            isPrivate = false,
            isFollowing = true
        ),
        User(
            id = "3",
            username = "grace_art",
            email = "grace@artstudio.com",
            profilePicture = null,
            fullName = "Grace Williams",
            bio = "Digital Artist | Illustrator",
            posts = 203,
            followers = 89200,
            following = 156,
            isPrivate = false,
            isFollowing = false
        ),
        User(
            id = "4",
            username = "rajesh_k",
            email = "rajesh@tech.com",
            profilePicture = null,
            fullName = "Rajesh Kumar",
            bio = "Software Engineer | Travel",
            posts = 56,
            followers = 1200,
            following = 450,
            isPrivate = true,
            isFollowing = false
        ),
        User(
            id = "5",
            username = "elise_g",
            email = "elise@fashion.com",
            profilePicture = null,
            fullName = "Elise Garcia",
            bio = "Fashion Designer | Model",
            posts = 312,
            followers = 125000,
            following = 289,
            isPrivate = false,
            isFollowing = true
        )
    )

    // FIXED: Mock posts for search results
    private val mockPosts = listOf(
        Post(
            id = "1",
            userId = "1",
            username = "andrew_queo",
            userProfileImage = "",
            imageUrl = "https://picsum.photos/400/400",
            caption = "Great times with amazing people! #art #design",
            likes = 1250,
            comments = 89,
            reposts = 23,
            isLiked = false,
            isReposted = false,
            timestamp = System.currentTimeMillis() - 1000000,
            location = "New York"
        ),
        Post(
            id = "2",
            userId = "3",
            username = "grace_art",
            userProfileImage = "",
            imageUrl = "https://picsum.photos/401/401",
            caption = "Beautiful sunset artwork inspired by nature 🌅 #art #digitalart",
            likes = 890,
            comments = 45,
            reposts = 12,
            isLiked = true,
            isReposted = false,
            timestamp = System.currentTimeMillis() - 2000000,
            location = "California"
        ),
        Post(
            id = "3",
            userId = "5",
            username = "elise_g",
            userProfileImage = "",
            imageUrl = "https://picsum.photos/402/402",
            caption = "New fashion collection coming soon! #fashion #design",
            likes = 2100,
            comments = 156,
            reposts = 89,
            isLiked = false,
            isReposted = false,
            timestamp = System.currentTimeMillis() - 3000000,
            location = "Paris"
        )
    )

    init {
        loadRecentSearches()
        setupSearchDebounce()
    }

    fun onSearchQueryChange(query: String) {
        _searchState.value = _searchState.value.copy(searchQuery = query)

        if (query.isNotEmpty()) {
            viewModelScope.launch {
                recentSearchesRepository.addRecentSearch(query)
            }
        }
    }

    private fun setupSearchDebounce() {
        viewModelScope.launch {
            _searchState
                .debounce(300)
                .distinctUntilChanged { old, new ->
                    old.searchQuery == new.searchQuery
                }
                .filter { it.searchQuery.isNotEmpty() }
                .flatMapLatest { state ->
                    performSearch(state.searchQuery)
                }
                .collect { result ->
                    _searchState.value = _searchState.value.copy(
                        users = result.users,
                        posts = result.posts,
                        isLoading = false,
                        error = result.error
                    )
                }
        }
    }

    // FIXED: Using mock data for search until backend is ready
    private fun performSearch(query: String) = flow {
        emit(SearchState(isLoading = true))

        try {
            // FIXED: Simulate network delay
            kotlinx.coroutines.delay(500)

            // FIXED: Search in mock users
            val filteredUsers = mockUsers.filter { user ->
                user.username.contains(query, ignoreCase = true) ||
                        user.fullName.contains(query, ignoreCase = true) ||
                        user.bio?.contains(query, ignoreCase = true) == true
            }

            // FIXED: Search in mock posts
            val filteredPosts = mockPosts.filter { post ->
                post.caption.contains(query, ignoreCase = true) ||
                        post.username.contains(query, ignoreCase = true) ||
                        post.location?.contains(query, ignoreCase = true) == true
            }

            emit(SearchState(
                users = filteredUsers,
                posts = filteredPosts,
                isLoading = false
            ))

        } catch (e: Exception) {
            emit(SearchState(
                error = "Search failed: ${e.message}",
                isLoading = false
            ))
        }
    }

    private fun loadRecentSearches() {
        viewModelScope.launch {
            recentSearchesRepository.getRecentSearches().collect { searches ->
                _searchState.value = _searchState.value.copy(recentSearches = searches)
            }
        }
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            recentSearchesRepository.clearRecentSearches()
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            try {
                // FIXED: Update mock data for follow state
                val updatedUsers = _searchState.value.users.map { user ->
                    if (user.id == userId) {
                        user.copy(isFollowing = true)
                    } else {
                        user
                    }
                }
                _searchState.value = _searchState.value.copy(users = updatedUsers)

                // In real app, call: userRepository.followUser(userId)
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(error = "Failed to follow user")
            }
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            try {
                // FIXED: Update mock data for unfollow state
                val updatedUsers = _searchState.value.users.map { user ->
                    if (user.id == userId) {
                        user.copy(isFollowing = false)
                    } else {
                        user
                    }
                }
                _searchState.value = _searchState.value.copy(users = updatedUsers)

                // In real app, call: userRepository.unfollowUser(userId)
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(error = "Failed to unfollow user")
            }
        }
    }

    fun clearError() {
        _searchState.value = _searchState.value.copy(error = null)
    }
}
//@HiltViewModel
//class SearchViewModel @Inject constructor(
//    private val userRepository: IUserRepository,
//    private val postRepository: IPostRepository,
//    private val recentSearchesRepository: IRecentSearchesRepository // ADDED: Using real repository
//) : ViewModel() {
//
//    private val _searchState = MutableStateFlow(SearchState())
//    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
//
//    init {
//        loadRecentSearches()
//        setupSearchDebounce()
//    }
//
//    fun onSearchQueryChange(query: String) {
//        _searchState.value = _searchState.value.copy(searchQuery = query)
//
//        // UPDATED: Save to recent searches using real repository
//        if (query.isNotEmpty()) {
//            viewModelScope.launch {
//                recentSearchesRepository.addRecentSearch(query)
//            }
//        }
//    }
//
//    private fun setupSearchDebounce() {
//        viewModelScope.launch {
//            _searchState
//                .debounce(300)
//                .distinctUntilChanged { old, new ->
//                    old.searchQuery == new.searchQuery
//                }
//                .filter { it.searchQuery.isNotEmpty() }
//                .flatMapLatest { state ->
//                    performSearch(state.searchQuery)
//                }
//                .collect { result ->
//                    _searchState.value = _searchState.value.copy(
//                        users = result.users,
//                        posts = result.posts,
//                        isLoading = false,
//                        error = result.error
//                    )
//                }
//        }
//    }
//
//    // UPDATED: In SearchViewModel - fix the performSearch function
//    private fun performSearch(query: String) = flow {
//        emit(SearchState(isLoading = true))
//
//        try {
//            // FIXED: Using actual repository search with proper error handling
//            userRepository.searchUsers(query).collect { usersResult ->
//                when (usersResult) {
//                    is Result.Success -> {
//                        // FIXED: For posts, we'll use explore posts and filter locally for now
//                        // In a real app, you'd have a postRepository.searchPosts(query)
//                        postRepository.getExplorePosts().collect { postsResult ->
//                            when (postsResult) {
//                                is Result.Success -> {
//                                    // FIXED: Filter posts based on search query
//                                    val filteredPosts = postsResult.data.filter { post ->
//                                        post.caption.contains(query, ignoreCase = true) ||
//                                                post.username.contains(query, ignoreCase = true) ||
//                                                post.location?.contains(query, ignoreCase = true) == true
//                                    }
//
//                                    emit(SearchState(
//                                        users = usersResult.data,
//                                        posts = filteredPosts,
//                                        isLoading = false
//                                    ))
//                                }
//                                is Result.Error -> {
//                                    // FIXED: Still show users even if posts fail
//                                    emit(SearchState(
//                                        users = usersResult.data,
//                                        posts = emptyList(),
//                                        isLoading = false,
//                                        error = "Posts: ${postsResult.message}"
//                                    ))
//                                }
//                                is Result.Loading -> {
//                                    // Loading handled by main state
//                                }
//                            }
//                        }
//                    }
//                    is Result.Error -> {
//                        emit(SearchState(
//                            users = emptyList(),
//                            posts = emptyList(),
//                            isLoading = false,
//                            error = "Users: ${usersResult.message}"
//                        ))
//                    }
//                    is Result.Loading -> {
//                        // Loading handled by main state
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            // FIXED: Better error message
//            emit(SearchState(error = "Search failed: ${e.message}", isLoading = false))
//        }
//    }
//
//    // UPDATED: Load recent searches from real DataStore
//    private fun loadRecentSearches() {
//        viewModelScope.launch {
//            recentSearchesRepository.getRecentSearches().collect { searches ->
//                _searchState.value = _searchState.value.copy(recentSearches = searches)
//            }
//        }
//    }
//
//    // UPDATED: Clear recent searches using real repository
//    fun clearRecentSearches() {
//        viewModelScope.launch {
//            recentSearchesRepository.clearRecentSearches()
//            // State will be updated automatically through the flow
//        }
//    }
//
//    fun followUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                val result = userRepository.followUser(userId)
//                when (result) {
//                    is Result.Success -> {
//                        val updatedUsers = _searchState.value.users.map { user ->
//                            if (user.id == userId) {
//                                user.copy(isFollowing = true)
//                            } else {
//                                user
//                            }
//                        }
//                        _searchState.value = _searchState.value.copy(users = updatedUsers)
//                    }
//                    is Result.Error -> {
//                        _searchState.value = _searchState.value.copy(error = result.message)
//                    }
//                    else -> {}
//                }
//            } catch (e: Exception) {
//                _searchState.value = _searchState.value.copy(error = "Failed to follow user")
//            }
//        }
//    }
//
//    fun unfollowUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                val result = userRepository.unfollowUser(userId)
//                when (result) {
//                    is Result.Success -> {
//                        val updatedUsers = _searchState.value.users.map { user ->
//                            if (user.id == userId) {
//                                user.copy(isFollowing = false)
//                            } else {
//                                user
//                            }
//                        }
//                        _searchState.value = _searchState.value.copy(users = updatedUsers)
//                    }
//                    is Result.Error -> {
//                        _searchState.value = _searchState.value.copy(error = result.message)
//                    }
//                    else -> {}
//                }
//            } catch (e: Exception) {
//                _searchState.value = _searchState.value.copy(error = "Failed to unfollow user")
//            }
//        }
//    }
//
//    fun clearError() {
//        _searchState.value = _searchState.value.copy(error = null)
//    }
//}