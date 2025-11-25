package com.sam.ayaana.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.datastore.DatastoreRepository
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.model.User
import com.sam.ayaana.domain.repository.IRecentSearchesRepository
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay

// Search State data class
data class SearchState(
    val searchQuery: String = "",
    val users: List<User> = emptyList(),
    val posts: List<Post> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recentSearchesRepository: IRecentSearchesRepository,
    private val datastoreRepository: DatastoreRepository
) : ViewModel() {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    // Mock data for search
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
            username = "o.scarmmj",
            email = "osman@example.com",
            profilePicture = null,
            fullName = "OSMAN | DIGITAL MARKETER",
            bio = "1 new post ●",
            posts = 89,
            followers = 15400,
            following = 342,
            isPrivate = false,
            isFollowing = false
        ),
        User(
            id = "3",
            username = "oscarkipchumbasudi",
            email = "oscar@example.com",
            profilePicture = null,
            fullName = "Oscar Kipchumba Sudi",
            bio = "Politician",
            posts = 203,
            followers = 89200,
            following = 156,
            isPrivate = false,
            isFollowing = false
        ),
        User(
            id = "4",
            username = "harrykappa_",
            email = "kappa@example.com",
            profilePicture = null,
            fullName = "Kappa",
            bio = "Following",
            posts = 56,
            followers = 1200,
            following = 450,
            isPrivate = false,
            isFollowing = true
        ),
        User(
            id = "5",
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
            id = "6",
            username = "chanelle_kittony",
            email = "chanelle@example.com",
            profilePicture = null,
            fullName = "Ms Chanelle J Kittony",
            bio = "17.3K followers",
            posts = 45,
            followers = 17300,
            following = 120,
            isPrivate = false,
            isFollowing = false
        ),
        User(
            id = "7",
            username = "_kenyan_prince",
            email = "prince@example.com",
            profilePicture = null,
            fullName = "KENYAS YOUNGEST SPONSOR",
            bio = "Entrepreneur",
            posts = 78,
            followers = 8900,
            following = 56,
            isPrivate = false,
            isFollowing = false
        ),
        User(
            id = "8",
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
        ),
        User(
            id = "9",
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
            id = "10",
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
        )
    )

    // Mock posts for search results
    private val mockPosts = listOf(
        Post(
            id = "1",
            userId = "1",
            username = "paul.kiprotich",
            userProfileImage = "",
            imageUrl = "https://picsum.photos/400/400",
            caption = "Great times with amazing people!",
            likes = 1250,
            comments = 89,
            reposts = 23,
            isLiked = false,
            isReposted = false,
            timestamp = System.currentTimeMillis() - 1000000,
            location = "Nairobi"
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

    @OptIn(FlowPreview::class)
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
                    // ADDED: Auto-clear error after successful search
                    if (result.error != null) {
                        clearError()
                    }
                }
        }
    }

    // Using mock data for search until backend is ready
    private fun performSearch(query: String) = flow {
        emit(SearchState(isLoading = true))

        try {
            // Simulate network delay
            delay(500)

            // Search in mock users
            val filteredUsers = mockUsers.filter { user ->
                user.username.contains(query, ignoreCase = true) ||
                        user.fullName.contains(query, ignoreCase = true) ||
                        user.bio?.contains(query, ignoreCase = true) == true
            }

            // Search in mock posts
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
            loadRecentSearches() // Reload to update UI
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            try {
                val updatedUsers = _searchState.value.users.map { user ->
                    if (user.id == userId) {
                        user.copy(isFollowing = true)
                    } else {
                        user
                    }
                }
                _searchState.value = _searchState.value.copy(users = updatedUsers)
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(error = "Failed to follow user: ${e.message}")
            }
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            try {
                val updatedUsers = _searchState.value.users.map { user ->
                    if (user.id == userId) {
                        user.copy(isFollowing = false)
                    } else {
                        user
                    }
                }
                _searchState.value = _searchState.value.copy(users = updatedUsers)
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(error = "Failed to unfollow user")
            }
        }
    }

    fun clearError() {
        _searchState.value = _searchState.value.copy(error = null)
    }

    // NEW: Function to remove individual recent search
    fun removeRecentSearch(search: String) {
        viewModelScope.launch {
            // This should call the removeRecentSearch method we just fixed
            datastoreRepository.removeRecentSearch(search)
            loadRecentSearches() // Reload to update UI
        }
    }
//    fun removeRecentSearch(search: String) {
//        viewModelScope.launch {
//            // This would require adding a new method to the repository
//            // For now, we'll clear all and re-add the ones we want to keep
//            val currentSearches = _searchState.value.recentSearches.toMutableList()
//            currentSearches.remove(search)
//            recentSearchesRepository.clearRecentSearches()
//            currentSearches.forEach { recentSearch ->
//                recentSearchesRepository.addRecentSearch(recentSearch)
//            }
//            loadRecentSearches()
//        }
//    }
}

//package com.sam.ayaana.presentation.screens.search
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.sam.ayaana.domain.model.Post
//import com.sam.ayaana.domain.model.User
//import com.sam.ayaana.domain.repository.IPostRepository
//import com.sam.ayaana.domain.repository.IRecentSearchesRepository
//import com.sam.ayaana.domain.repository.IUserRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.debounce
//import kotlinx.coroutines.flow.distinctUntilChanged
//import kotlinx.coroutines.flow.filter
//import kotlinx.coroutines.flow.flatMapLatest
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//import com.sam.ayaana.Utils.Result
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.FlowPreview
//
//// Search State data class
//data class SearchState(
//    val searchQuery: String = "",
//    val users: List<User> = emptyList(),
//    val posts: List<Post> = emptyList(),
//    val recentSearches: List<String> = emptyList(),
//    val isLoading: Boolean = false,
//    val error: String? = null
//)
//
//@OptIn(ExperimentalCoroutinesApi::class)
//@HiltViewModel
//class SearchViewModel @Inject constructor(
////    private val userRepository: IUserRepository,
////    private val postRepository: IPostRepository,
//    private val recentSearchesRepository: IRecentSearchesRepository
//) : ViewModel() {
//
//    private val _searchState = MutableStateFlow(SearchState())
//    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()
//
//    // FIXED: Create mock data that will be used for search
//    private val mockUsers = listOf(
//        User(
//            id = "1",
//            username = "andrew_queo",
//            email = "isabelle@art.design",
//            profilePicture = null,
//            fullName = "Andrew Queo",
//            bio = "Artist | DESIGNER",
//            posts = 174,
//            followers = 772000,
//            following = 714,
//            isPrivate = false,
//            isFollowing = false
//        ),
//        User(
//            id = "2",
//            username = "sarah_m",
//            email = "sarah@design.com",
//            profilePicture = null,
//            fullName = "Sarah Mitchell",
//            bio = "UI/UX Designer | Photography",
//            posts = 89,
//            followers = 15400,
//            following = 342,
//            isPrivate = false,
//            isFollowing = true
//        ),
//        User(
//            id = "3",
//            username = "grace_art",
//            email = "grace@artstudio.com",
//            profilePicture = null,
//            fullName = "Grace Williams",
//            bio = "Digital Artist | Illustrator",
//            posts = 203,
//            followers = 89200,
//            following = 156,
//            isPrivate = false,
//            isFollowing = false
//        ),
//        User(
//            id = "4",
//            username = "rajesh_k",
//            email = "rajesh@tech.com",
//            profilePicture = null,
//            fullName = "Rajesh Kumar",
//            bio = "Software Engineer | Travel",
//            posts = 56,
//            followers = 1200,
//            following = 450,
//            isPrivate = true,
//            isFollowing = false
//        ),
//        User(
//            id = "5",
//            username = "elise_g",
//            email = "elise@fashion.com",
//            profilePicture = null,
//            fullName = "Elise Garcia",
//            bio = "Fashion Designer | Model",
//            posts = 312,
//            followers = 125000,
//            following = 289,
//            isPrivate = false,
//            isFollowing = true
//        )
//    )
//
//    // FIXED: Mock posts for search results
//    private val mockPosts = listOf(
//        Post(
//            id = "1",
//            userId = "1",
//            username = "andrew_queo",
//            userProfileImage = "",
//            imageUrl = "https://picsum.photos/400/400",
//            caption = "Great times with amazing people! #art #design",
//            likes = 1250,
//            comments = 89,
//            reposts = 23,
//            isLiked = false,
//            isReposted = false,
//            timestamp = System.currentTimeMillis() - 1000000,
//            location = "New York"
//        ),
//        Post(
//            id = "2",
//            userId = "3",
//            username = "grace_art",
//            userProfileImage = "",
//            imageUrl = "https://picsum.photos/401/401",
//            caption = "Beautiful sunset artwork inspired by nature 🌅 #art #digitalart",
//            likes = 890,
//            comments = 45,
//            reposts = 12,
//            isLiked = true,
//            isReposted = false,
//            timestamp = System.currentTimeMillis() - 2000000,
//            location = "California"
//        ),
//        Post(
//            id = "3",
//            userId = "5",
//            username = "elise_g",
//            userProfileImage = "",
//            imageUrl = "https://picsum.photos/402/402",
//            caption = "New fashion collection coming soon! #fashion #design",
//            likes = 2100,
//            comments = 156,
//            reposts = 89,
//            isLiked = false,
//            isReposted = false,
//            timestamp = System.currentTimeMillis() - 3000000,
//            location = "Paris"
//        )
//    )
//
//    init {
//        loadRecentSearches()
//        setupSearchDebounce()
//    }
//
//    fun onSearchQueryChange(query: String) {
//        _searchState.value = _searchState.value.copy(searchQuery = query)
//
//        if (query.isNotEmpty()) {
//            viewModelScope.launch {
//                recentSearchesRepository.addRecentSearch(query)
//            }
//        }
//    }
//
//
//
//    @OptIn(FlowPreview::class)
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
//    // FIXED: Using mock data for search until backend is ready
//    private fun performSearch(query: String) = flow {
//        emit(SearchState(isLoading = true))
//
//        try {
//            // FIXED: Simulate network delay
//            kotlinx.coroutines.delay(500)
//
//            // FIXED: Search in mock users
//            val filteredUsers = mockUsers.filter { user ->
//                user.username.contains(query, ignoreCase = true) ||
//                        user.fullName.contains(query, ignoreCase = true) ||
//                        user.bio?.contains(query, ignoreCase = true) == true
//            }
//
//            // FIXED: Search in mock posts
//            val filteredPosts = mockPosts.filter { post ->
//                post.caption.contains(query, ignoreCase = true) ||
//                        post.username.contains(query, ignoreCase = true) ||
//                        post.location?.contains(query, ignoreCase = true) == true
//            }
//
//            emit(SearchState(
//                users = filteredUsers,
//                posts = filteredPosts,
//                isLoading = false
//            ))
//
//        } catch (e: Exception) {
//            emit(SearchState(
//                error = "Search failed: ${e.message}",
//                isLoading = false
//            ))
//        }
//    }
//
//    private fun loadRecentSearches() {
//        viewModelScope.launch {
//            recentSearchesRepository.getRecentSearches().collect { searches ->
//                _searchState.value = _searchState.value.copy(recentSearches = searches)
//            }
//        }
//    }
//
//    fun clearRecentSearches() {
//        viewModelScope.launch {
//            recentSearchesRepository.clearRecentSearches()
//        }
//    }
//
//    fun followUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                // FIXED: Update mock data for follow state
//                val updatedUsers = _searchState.value.users.map { user ->
//                    if (user.id == userId) {
//                        user.copy(isFollowing = true)
//                    } else {
//                        user
//                    }
//                }
//                _searchState.value = _searchState.value.copy(users = updatedUsers)
//
//                // In real app, call: userRepository.followUser(userId)
//            } catch (e: Exception) {
//                _searchState.value = _searchState.value.copy(error = "Failed to follow user")
//            }
//        }
//    }
//
//    fun unfollowUser(userId: String) {
//        viewModelScope.launch {
//            try {
//                // FIXED: Update mock data for unfollow state
//                val updatedUsers = _searchState.value.users.map { user ->
//                    if (user.id == userId) {
//                        user.copy(isFollowing = false)
//                    } else {
//                        user
//                    }
//                }
//                _searchState.value = _searchState.value.copy(users = updatedUsers)
//
//                // In real app, call: userRepository.unfollowUser(userId)
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
