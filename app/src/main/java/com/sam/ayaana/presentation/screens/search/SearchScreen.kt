// presentation/screens/search/SearchScreen.kt
package com.sam.ayaana.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sam.ayaana.R
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController? = null,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val currentRoute = if (navController != null) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        navBackStackEntry?.destination?.route
    } else {
        "search"
    }

    val searchState by viewModel.searchState.collectAsState()
    val focusManager = LocalFocusManager.current // ADDED: For managing keyboard

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                Text(
                    text = "Search",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )

                // UPDATED: Using improved SearchBar with clear button
                SearchBar(
                    searchQuery = searchState.searchQuery,
                    onSearchQueryChange = viewModel::onSearchQueryChange,
                    onClearClick = {
                        viewModel.onSearchQueryChange("") // FIXED: Clear search query
                        focusManager.clearFocus() // FIXED: Clear keyboard focus
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                // ... bottom bar items (same as before) ...
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            if (searchState.searchQuery.isEmpty()) {
                RecentAndTrendingSection(
                    recentSearches = searchState.recentSearches,
                    onSearchClick = viewModel::onSearchQueryChange,
                    onClearRecent = viewModel::clearRecentSearches
                )
            } else {
                SearchResultsSection(
                    searchState = searchState,
                    onUserClick = { userId ->
                        navController?.navigate("profile/$userId")
                    },
                    onFollowClick = viewModel::followUser,
                    onUnfollowClick = viewModel::unfollowUser,
                    onPostClick = { postId ->
                        navController?.navigate("post/$postId")
                    }
                )
            }

            if (searchState.error != null) {
                LaunchedEffect(searchState.error) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.clearError()
                }
            }
        }
    }
}

// UPDATED: Improved SearchBar with rounded corners and clear button
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearClick: () -> Unit, // ADDED: Clear button callback
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp)), // FIXED: Added rounded corners
        placeholder = { Text("Search", color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            // FIXED: Show clear button only when there's text
            if (searchQuery.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { onClearClick() }
                )
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFF262626),
            focusedContainerColor = Color(0xFF262626),
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
            cursorColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus() // FIXED: Hide keyboard on search
            }
        )
    )
}

@Composable
fun RecentAndTrendingSection(
    recentSearches: List<String>,
    onSearchClick: (String) -> Unit,
    onClearRecent: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Recent Searches Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (recentSearches.isNotEmpty()) {
                    TextButton(onClick = onClearRecent) {
                        Text("Clear all", color = Color.Blue)
                    }
                }
            }
        }

        items(recentSearches) { search ->
            RecentSearchItem(
                search = search,
                onSearchClick = { onSearchClick(search) }
            )
        }

        // Trending Section
        item {
            Text(
                text = "Discover",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        items(getTrendingContent()) { trending ->
            TrendingItem(
                trending = trending,
                onTrendingClick = { onSearchClick(trending) } // FIXED: Make trending items clickable
            )
        }
    }
}

@Composable
fun RecentSearchItem(
    search: String,
    onSearchClick: (String) -> Unit
) {
    Text(
        text = search,
        color = Color.White,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onSearchClick(search) }
    )
}

// UPDATED: TrendingItem now has click handler
@Composable
fun TrendingItem(
    trending: String,
    onTrendingClick: (String) -> Unit // ADDED: Click handler for trending items
) {
    Text(
        text = trending,
        color = Color.White,
        fontSize = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onTrendingClick(trending) } // FIXED: Make trending items clickable
    )
}

@Composable
fun SearchResultsSection(
    searchState: SearchState,
    onUserClick: (String) -> Unit,
    onFollowClick: (String) -> Unit,
    onUnfollowClick: (String) -> Unit,
    onPostClick: (String) -> Unit
) {
    if (searchState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Users Results
            if (searchState.users.isNotEmpty()) {
                item {
                    Text(
                        text = "Accounts",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(searchState.users) { user ->
                    UserSearchResultItem(
                        user = user,
                        onUserClick = onUserClick,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick
                    )
                }
            }

            // Posts Results - FIXED: Using actual posts from searchState
            if (searchState.posts.isNotEmpty()) {
                item {
                    Text(
                        text = "Posts",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(searchState.posts) { post ->
                    PostSearchResultItem(
                        post = post,
                        onPostClick = onPostClick
                    )
                }
            }

            // FIXED: Show no results only when search is complete and no results
            if (!searchState.isLoading && searchState.users.isEmpty() && searchState.posts.isEmpty() && searchState.searchQuery.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No results found for \"${searchState.searchQuery}\"",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserSearchResultItem(
    user: User,
    onUserClick: (String) -> Unit,
    onFollowClick: (String) -> Unit,
    onUnfollowClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onUserClick(user.id) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.Gray, androidx.compose.foundation.shape.CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.username,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = user.fullName,
                color = Color.Gray,
                fontSize = 12.sp
            )
            // FIXED: Show bio if available
            user.bio?.let { bio ->
                Text(
                    text = bio,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }

        Button(
            onClick = {
                if (user.isFollowing) {
                    onUnfollowClick(user.id)
                } else {
                    onFollowClick(user.id)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (user.isFollowing) Color.Gray else Color.Blue
            ),
            modifier = Modifier.size(height = 32.dp, width = 80.dp)
        ) {
            Text(
                text = if (user.isFollowing) "Following" else "Follow",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun PostSearchResultItem(post: Post, onPostClick: (String) -> Unit) {
    // Simple post item for now
    Text(
        text = "Post by ${post.username}: ${post.caption}",
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onPostClick(post.id) }
    )
}

// FIXED: Updated trending content to be more searchable
private fun getTrendingContent(): List<String> {
    return listOf(
        "Love Is Peaceful",
        "SHAKE!",
        "Miguna",
        "Odinga",
        "Design",
        "Art",
        "Fashion",
        "Travel"
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen()
}