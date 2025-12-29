
package com.sam.ayaana.presentation.screens.search


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.sam.ayaana.domain.model.TrendingItem
import com.sam.ayaana.domain.model.User
import kotlinx.coroutines.delay



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController? = null,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchState by viewModel.searchState.collectAsState()
    val focusManager = LocalFocusManager.current

    // CHANGED: Track if search bar is focused (active state with back arrow)
    var isSearchFocused by remember { mutableStateOf(false) }

    // ADDED: Get current route for bottom navigation highlighting
    val currentRoute = if (navController != null) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        navBackStackEntry?.destination?.route
    } else {
        "search"
    }

    // ADDED: Error handling with auto-clear
    if (searchState.error != null) {
        LaunchedEffect(searchState.error) {
            delay(1000)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // CHANGED: Updated search header with back button when focused
            SearchHeader(
                isSearchFocused = isSearchFocused,
                searchQuery = searchState.searchQuery,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onClearClick = {
                    viewModel.onSearchQueryChange("")
                    focusManager.clearFocus()
                },
                onBackClick = {
                    isSearchFocused = false
                    focusManager.clearFocus()
                },
                onSearchFocusChange = { focused ->
                    isSearchFocused = focused
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.Black)
            ) {
                // CHANGED: Updated logic to show different content based on focus state
                if (isSearchFocused) {
                    // When search bar is focused (active state) - show recent searches
                    ActiveSearchState(
                        searchState = searchState,
                        viewModel = viewModel,
                        onUserClick = { userId: String ->
                            navController?.navigate("profile/$userId")
                        },
                        onFollowClick = { userId: String ->
                            viewModel.followUser(userId)
                        },
                        onUnfollowClick = { userId: String ->
                            viewModel.unfollowUser(userId)
                        },
                        onPostClick = { postId: String ->
                            navController?.navigate("post/$postId")
                        },
                        onSearchClick = { query: String ->
                            viewModel.onSearchQueryChange(query)
                        }
                    )
                } else {
                    // When search bar is not focused (default state) - show discovery or search results
                    if (searchState.searchQuery.isEmpty()) {
                        // Show discovery content (like Search 1 IG)
                        DiscoverySection(
                            trendingContent = getTrendingContent(),
                            onTrendingClick = { query: String ->
                                viewModel.onSearchQueryChange(query)
                            }
                        )
                    } else {
                        // Show search results when there's a query
                        SearchResultsSection(
                            searchState = searchState,
                            viewModel = viewModel,
                            onUserClick = { userId: String ->
                                navController?.navigate("profile/$userId")
                            },
                            onFollowClick = { userId: String ->
                                viewModel.followUser(userId)
                            },
                            onUnfollowClick = { userId: String ->
                                viewModel.unfollowUser(userId)
                            },
                            onPostClick = { postId: String ->
                                navController?.navigate("post/$postId")
                            },
                            onSearchClick = { query: String ->
                                viewModel.onSearchQueryChange(query)
                            }
                        )
                    }
                }
            }
        }

        // CHANGED: Only show bottom navigation when search is NOT focused
        if (!isSearchFocused) {
            NavigationBar(
                modifier = Modifier.align(Alignment.BottomCenter),
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
                            contentDescription = "Home",
                            tint = if (currentRoute == "home") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "home",
                    onClick = { navController?.navigate("home") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
                            contentDescription = "Reels",
                            tint = if (currentRoute == "reels") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "reels",
                    onClick = { navController?.navigate("reels") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
                            contentDescription = "Chat",
                            tint = if (currentRoute == "chat") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "chat",
                    onClick = { navController?.navigate("chat") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
                            contentDescription = "Search",
                            tint = if (currentRoute == "search") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "search",
                    onClick = { /* Already on search screen */ }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
                            contentDescription = "Profile",
                            tint = if (currentRoute == "profile") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "profile",
                    onClick = { navController?.navigate("profile") }
                )
            }
        }
    }
}

// NEW: Search Header with conditional back button
@Composable
fun SearchHeader(
    isSearchFocused: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onBackClick: () -> Unit,
    onSearchFocusChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Show back arrow only when search is focused (active state)
        if (isSearchFocused) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp) //28
                    .clickable { onBackClick() }
                    .padding(end = 12.dp) //16
            )
        }

        // Search Bar
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onClearClick = onClearClick,
            onSearchFocusChange = onSearchFocusChange,
            modifier = Modifier.weight(1f)
        )
    }
}

// UPDATED: SearchBar with focus tracking
@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // ADDED: Notify parent when focus changes
    LaunchedEffect(isFocused) {
        onSearchFocusChange(isFocused)
    }
    // ADDED: Track if search bar is focused (active state)
    TextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp)),
        placeholder = {
            Text(
                "Search",
                color = Color.Gray,
                fontSize = 16.sp,

            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isFocused) Color.Gray else Color.White,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search",
                    tint = if (isFocused) Color.Gray else Color.White,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onClearClick()
                        }
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
        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        interactionSource = interactionSource
    )
}

// Active search state (when search bar is focused)
@Composable
fun ActiveSearchState(
    searchState: SearchState,
    viewModel: SearchViewModel,
    onUserClick: (String) -> Unit,
    onFollowClick: (String) -> Unit,
    onUnfollowClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onSearchClick: (String) -> Unit
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
            // Show recent searches when search is active
            if (searchState.recentSearches.isNotEmpty()) {
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
                        TextButton(onClick = viewModel::clearRecentSearches) {
                            Text("Clear all", color = Color.Blue)
                        }
                    }
                }

                items(searchState.recentSearches) { search ->
                    RecentSearchItem(
                        search = search,
                        onSearchClick = onSearchClick,
                        onRemoveClick = { viewModel.removeRecentSearch(search) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFF262626))
                }
            }

            // Show search results if there's a query
            if (searchState.searchQuery.isNotEmpty()) {
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

                if (!searchState.isLoading && searchState.users.isEmpty() && searchState.posts.isEmpty()) {
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
}


@Composable
fun DiscoverySection(
    trendingContent: List<TrendingItem>,
    onTrendingClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(trendingContent) { trending ->
            TrendingDiscoveryItem(
                trending = trending,
                onTrendingClick = onTrendingClick
            )
        }
    }
}

@Composable
fun TrendingDiscoveryItem(
    trending: TrendingItem,
    onTrendingClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTrendingClick(trending.title) }
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        if (trending.category.isNotEmpty()) {
            Text(
                text = trending.category.uppercase(),
                color = Color(0xFFA8A8A8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Text(
            text = trending.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        trending.description?.let { description ->
            Text(
                text = description,
                color = Color(0xFFA8A8A8),
                fontSize = 12.sp,
                maxLines = 2
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFF262626), thickness = 0.5.dp)
    }
}

@Composable
fun SearchResultsSection(
    searchState: SearchState,
    viewModel: SearchViewModel,
    onUserClick: (String) -> Unit,
    onFollowClick: (String) -> Unit,
    onUnfollowClick: (String) -> Unit,
    onPostClick: (String) -> Unit,
    onSearchClick: (String) -> Unit
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
fun RecentSearchItem(
    search: String,
    onSearchClick: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearchClick(search) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Recent search",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = search,
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove search",
            tint = Color.Gray,
            modifier = Modifier
                .size(18.dp)
                .clickable { onRemoveClick() }
        )
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
            .clickable { onUserClick(user.id) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
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
                fontWeight = FontWeight.Normal
            )
            Text(
                text = user.fullName,
                color = Color(0xFFA8A8A8),
                fontSize = 14.sp
            )
            if (user.followers > 0) {
                Text(
                    text = "${user.followers} followers",
                    color = Color(0xFFA8A8A8),
                    fontSize = 12.sp
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
                containerColor = if (user.isFollowing) Color.Transparent else Color(0xFF3797F0)
            ),
            modifier = Modifier.size(height = 30.dp, width = 80.dp),
            shape = RoundedCornerShape(6.dp),
            border = if (user.isFollowing) {
                ButtonDefaults.outlinedButtonBorder
            } else null
        ) {
            Text(
                text = if (user.isFollowing) "Following" else "Follow",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun PostSearchResultItem(post: Post, onPostClick: (String) -> Unit) {
    Text(
        text = "Post by ${post.username}: ${post.caption.take(50)}...",
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onPostClick(post.id) }
    )
}

private fun getTrendingContent(): List<TrendingItem> {
    return listOf(
        TrendingItem(
            title = "Elon Musk launches Space X",
            category = "Technology"
        ),
        TrendingItem(
            title = "Love Is Peaceful",
            category = "Trending"
        ),
        TrendingItem(
            title = "SHAKE!",
            category = "Music"
        ),
        TrendingItem(
            title = "Miguna Blasts Matiang'i Over \"Insensitive\" Gift to Ida Odinga",
            category = "Politics"
        ),
        TrendingItem(
            title = "A guide builds WhatsApps",
            category = "Technology",
            description = "30:06:22"
        ),
        TrendingItem(
            title = "BIGGEST",
            category = "Entertainment",
            description = "Youtube: [gz@l]m"
        ),
        TrendingItem(
            title = "Rigathi Gachagua",
            category = "Politics"
        )
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen()
}

