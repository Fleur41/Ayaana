// presentation/screens/reels/ReelsScreen.kt
package com.sam.ayaana.presentation.screens.reels

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sam.ayaana.R
import com.sam.ayaana.domain.model.Reel
import com.sam.ayaana.navigation.NavigationDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelsScreen(
    navController: NavHostController? = null,
    viewModel: ReelsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val scope = rememberCoroutineScope()
    var isSearchActive by remember { mutableStateOf(false) }
    var showSearchSuggestions by remember { mutableStateOf(false) }

    // Load reels on first launch
    LaunchedEffect(Unit) {
        if (uiState.reels.isEmpty()) {
            viewModel.loadReels()
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // CHANGED: Simple TextField with box styling
                TextField(
                    value = searchQuery,
                    onValueChange = {
                        viewModel.searchReels(it)
                        showSearchSuggestions = it.isNotEmpty()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = {
                        Text("Search reels...", color = Color.Gray)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    viewModel.clearSearch()
                                    showSearchSuggestions = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
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
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (navController?.currentDestination?.route == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
                            contentDescription = "Home",
                            tint = if (navController?.currentDestination?.route == "home") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = navController?.currentDestination?.route == "home",
                    onClick = { navController?.navigate("home") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (navController?.currentDestination?.route == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
                            contentDescription = "Reels",
                            tint = if (navController?.currentDestination?.route == "reels") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = navController?.currentDestination?.route == "reels",
                    onClick = { /* Already on reels */ }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (navController?.currentDestination?.route == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
                            contentDescription = "Chat",
                            tint = if (navController?.currentDestination?.route == "chat") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = navController?.currentDestination?.route == "chat",
                    onClick = { navController?.navigate("chat") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (navController?.currentDestination?.route == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
                            contentDescription = "Search",
                            tint = if (navController?.currentDestination?.route == "search") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = navController?.currentDestination?.route == "search",
                    onClick = { navController?.navigate("search") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (navController?.currentDestination?.route == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
                            contentDescription = "Profile",
                            tint = if (navController?.currentDestination?.route == "profile") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = navController?.currentDestination?.route == "profile",
                    onClick = { navController?.navigate("profile") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Error loading reels",
                            color = Color.White
                        )
                    }
                }

                else -> {
                    // Show trending topics
                    if (!uiState.isSearching && searchQuery.isEmpty()) {
                        TrendingTopicsSection(
                            onTopicClick = { topic ->
                                viewModel.searchReels(topic)
                                scope.launch {
                                    viewModel.addToSearchHistory(topic)
                                }
                            }
                        )
                    }

                    // Category filter section
                    if (!uiState.isSearching) {
                        CategoryFilterSection(
                            selectedCategory = uiState.selectedCategory,
                            onCategorySelected = { category ->
                                viewModel.filterByCategory(category)
                            },
                            onClearFilter = {
                                viewModel.clearFilter()
                            }
                        )
                    }

                    // Reels grid
                    ReelsGrid(
                        reels = uiState.filteredReels,
                        onReelClick = { reel ->
                            navController?.navigate(NavigationDestination.ReelDetail.createRoute(reel.id))
                            // navController?.navigate("reel_detail/${reel.id}")
                        },
                        onLikeClick = { reel ->
                            viewModel.likeReel(reel.id)
                        },
                        isLoading = uiState.isSearching,
                        selectedCategory = uiState.selectedCategory,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// =============== COMPONENTS ===============

@Composable
private fun SearchSuggestions(
    searchQuery: String,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = listOf(
        "By A Nationwide Internet Owner",
        "Love Is Peaceful",
        "SHAKE!",
        "Miguna Blasts Matiangi",
        "What killed former",
        "Car videos",
        "Funny moments",
        "Travel destinations",
        "Cooking recipes",
        "Fashion trends"
    )

    val filteredSuggestions = suggestions.filter {
        it.contains(searchQuery, ignoreCase = true)
    }.take(5)

    LazyColumn(
        modifier = modifier.padding(8.dp)
    ) {
        items(filteredSuggestions) { suggestion ->
            Text(
                text = suggestion,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 8.dp)
                    .clickable { onSuggestionClick(suggestion) },
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun TrendingTopicsSection(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val trendingTopics = listOf(
        "#ElectionDay",
        "#Peaceful",
        "#SHAKE",
        "#Miguna",
        "#Revealed",
        "#BreakingNews",
        "#TrendingNow",
        "#ViralVideos",
        "#MustWatch",
        "#HotTopics"
    )

    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "Trending Now",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(trendingTopics) { topic ->
                TopicChip(topic = topic, onClick = { onTopicClick(topic) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TopicChip(
    topic: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF262626))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = topic,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CategoryFilterSection(
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit,
    onClearFilter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "All",
        "Travel",
        "Vehicle",
        "Food",
        "Fashion",
        "Comedy",
        "Music",
        "Sports",
        "Nature",
        "Technology"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        item {
            CategoryChip(
                title = "All",
                isSelected = selectedCategory == null,
                onClick = onClearFilter
            )
        }

        items(categories.filter { it != "All" }) { category ->
            CategoryChip(
                title = category,
                isSelected = selectedCategory == category,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF0095F6) else Color(0xFF262626)
    val textColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.9f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReelsGrid(
    modifier: Modifier = Modifier,
    reels: List<Reel>,
    onReelClick: (Reel) -> Unit,
    onLikeClick: (Reel) -> Unit,
    isLoading: Boolean,
    selectedCategory: String? = null,
) {
    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }

    if (reels.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedCategory != null) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "No results",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No reels found in $selectedCategory",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Try another category",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No results",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No reels found",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Try a different search",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier,
        contentPadding = PaddingValues(2.dp)
    ) {
        items(reels) { reel ->
            ReelCard(
                reel = reel,
                onClick = { onReelClick(reel) },
                onLikeClick = { onLikeClick(reel) },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun ReelCard(
    reel: Reel,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            // Thumbnail
            AsyncImage(
                model = reel.thumbnailUrl,
                contentDescription = reel.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Play button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Duration badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = formatDuration(reel.duration),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Category badge
            if (reel.category.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0095F6).copy(alpha = 0.8f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = reel.category,
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Bottom gradient overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Bottom content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Title
                Text(
                    text = reel.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Username
                Text(
                    text = "@${reel.username}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats row
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Likes
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = null
                        ) { onLikeClick() }
                    ) {
                        Icon(
                            imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (reel.isLiked) Color.Red else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatCount(reel.likes),
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Comments
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💬",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatCount(reel.comments),
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// Helper functions
private fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

@Composable
private fun ReelsScreenPreview() {
    ReelsScreen()
}
