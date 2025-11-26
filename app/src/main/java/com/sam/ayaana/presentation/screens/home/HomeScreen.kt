//package com.sam.ayaana.presentation.screens.home
package com.sam.ayaana.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.sam.ayaana.R
import com.sam.ayaana.navigation.NavigationDestination
import com.sam.ayaana.presentation.component.homesection.PostsSection
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController? = null,
    onDetailClick: () -> Unit = {}
) {

    var hasProfilePicture by remember { mutableStateOf(false) }
    val currentRoute = if (navController != null) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        navBackStackEntry?.destination?.route
    } else {
        "home"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            InstagramTopView(
                navController = navController,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InstagramStoryView(
                    navController = navController,
                    hasProfilePicture = hasProfilePicture,
                    onProfilePictureAdded = { hasProfilePicture = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PostsSection(
                    viewModel = viewModel,
                    onProfileClick = { userId ->
                        navController?.navigate("profile/$userId")
                    },
                    onLikeClick = { postId, isCurrentlyLiked ->
                        viewModel.toggleLike(postId, isCurrentlyLiked)
                    },
                    onCommentClick = { postId ->
                        println("Opening comments for post: $postId")
                    },
                    onShareClick = { postId ->
                        viewModel.sharePost(postId)
                    },
                    onRepostClick = { postId, isCurrentlyReposted ->
                        viewModel.toggleRepost(postId, isCurrentlyReposted)
                    },
                    onMoreOptionsClick = { postId ->
                        viewModel.showMoreOptions(postId)
                    },
                    onSaveClick = { postId ->
                        viewModel.savePost(postId)
                    }
                )
            }
        }

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
                onClick = { navController?.navigate("search") }
            )

            NavigationBarItem(
                icon = {
                    if (hasProfilePicture) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                model = "https://picsum.photos/id/100/200/300",
                                contentDescription = "Profile",
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
                            contentDescription = "Profile",
                            tint = if (currentRoute == "profile") Color.White else Color.Gray
                        )
                    }
                },
                label = { Text(text = "", fontSize = 0.sp) },
                selected = currentRoute == "profile",
                onClick = { navController?.navigate("profile") }
            )
        }
    }
}


@Composable
fun InstagramTopView(
    navController: NavHostController? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(start = 16.dp, top = 12.dp, end = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ayaana",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Button(
//                onClick = {
//                    Log.e("NAV_TEST", "🎯 BUTTON CLICKED - Starting navigation...")
//                    try {
//                        navController?.navigate(NavigationDestination.CreatePost.route) {
//                            launchSingleTop = true
//                        }
//                        Log.e("NAV_TEST", "✅ Navigation command sent successfully")
//                    } catch (e: Exception) {
//                        Log.e("NAV_TEST", "❌ Navigation failed: ${e.message}", e)
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text("ADD", color = Color.White)
//            }
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add post",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        navController?.navigate(NavigationDestination.CreatePost.route)
                    }
            )

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { }
            )
        }
    }
}

//@Composable
//fun InstagramTopView(
//    navController: NavHostController? = null
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 16.dp, top = 12.dp, end = 24.dp, bottom = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(
//            text = "Ayaana",
//            style = MaterialTheme.typography.headlineSmall.copy(
//                fontWeight = FontWeight.Bold,
//                fontFamily = FontFamily.Cursive,
//                fontSize = 24.sp
//            ),
//            color = MaterialTheme.colorScheme.onBackground
//        )
//
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // SIMPLE TEST BUTTON
//            Button(
//                onClick = {
//                    Log.e("NAV_TEST", "🎯 BUTTON CLICKED!")
//                    navController?.navigate("create_post") // ← SIMPLE ROUTE
//                },
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//            ) {
//                Text("ADD POST", color = Color.White)
//            }
//
//            Icon(
//                imageVector = Icons.Default.Favorite,
//                contentDescription = "Notifications",
//                tint = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier
//                    .size(24.dp)
//                    .clickable { }
//            )
//        }
//    }
//}

@Composable
fun InstagramStoryView(
    navController: NavHostController? = null,
    onStoryClick: (Int) -> Unit = {},
    hasProfilePicture: Boolean = false,
    onProfilePictureAdded: () -> Unit = {}
) {
    var stories by remember { mutableStateOf<List<Int>>(emptyList()) }
    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    val lazyRowState = rememberLazyListState()
    val pageSize = 20

    LaunchedEffect(Unit) {
        if (stories.isEmpty()) {
            val initialStories = (1..pageSize).toList()
            stories = initialStories
        }
    }

    LaunchedEffect(lazyRowState) {
        snapshotFlow { lazyRowState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && !isLoading) {
                    val lastVisibleItem = visibleItems.last()
                    if (lastVisibleItem.index >= stories.size - 5) {
                        isLoading = true
                        delay(1000)

                        val newStories = (1..pageSize).map {
                            it + (currentPage * pageSize)
                        }
                        stories = stories + newStories
                        currentPage++
                        isLoading = false
                    }
                }
            }
    }

    LazyRow(
        state = lazyRowState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            UserStoryItem(
                itemId = 0,
                username = "Your Story",
                onItemClick = { Log.d("TAG", "UserStoryItem clicked") },
                hasProfilePicture = hasProfilePicture,
                onProfilePictureAdded = onProfilePictureAdded,
//                onAddPhotoClick = {
//                    Log.e("HOME_DEBUG", "🎯 STEP -1: + Icon clicked in HomeScreen!")
//                    Log.e("HOME_DEBUG", "🎯 navController: $navController")
//                    Log.e("HOME_DEBUG", "🎯 Route: ${NavigationDestination.CreatePost.route}")
//
//                    try {
//                        navController?.navigate(NavigationDestination.CreatePost.route)
//                        Log.e("HOME_DEBUG", "✅ Navigation command sent successfully!")
//                    } catch (e: Exception) {
//                        Log.e("HOME_DEBUG", "❌ Navigation failed: ${e.message}", e)
//                    }
//                }
                onAddPhotoClick = {
                    navController?.navigate(NavigationDestination.CreatePost.route)
                }
            )
        }

        items(
            items = stories,
            key = { itemId -> itemId }
        ) { itemId ->
            val username = when (itemId) {
                1 -> "natasha_mukami"
                2 -> "_wanjau_"
                3 -> "shawn_kip"
                4 -> "hadiya_rajesh"
                5 -> "dr_magnito"
                6 -> "arjun_vyas"
                7 -> "sauro_bhat"
                8 -> "pauline_claude"
                9 -> "john_doe"
                10 -> "jane_smith"
                11 -> "Leonard_Otie"
                12 -> "emily_davis"
                13 -> "joan_johnson"
                14 -> "olivia_wilson"
                15 -> "william_brown"
                else -> "user_$itemId"
            }

            StoryItem(
                itemId = itemId,
                username = username,
                onItemClick = { onStoryClick(itemId) }
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                }
            }
        }
    }
}

@Composable
fun StoryItem(
    modifier: Modifier = Modifier,
    itemId: Int,
    username: String,
    onItemClick: () -> Unit
) {
    val gradientColors = listOf(
        Color(0xFF833AB4), Color(0xFFC13584), Color(0xFFE1306C), Color(0xFFFD1D1D),
        Color(0xFFF56040), Color(0xFFF77737), Color(0xFFFCAF45), Color(0xFFFFDC80)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(colors = gradientColors),
                    shape = CircleShape
                )
                .padding(2.dp)
                .background(Color.White, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onItemClick() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                model = "https://picsum.photos/id/${itemId}/200/300",
                contentDescription = "Story",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                fallback = painterResource(R.drawable.placeholder)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = username,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun UserStoryItem(
    modifier: Modifier = Modifier,
    itemId: Int,
    username: String,
    onItemClick: () -> Unit,
    onAddPhotoClick: () -> Unit = {},
    hasProfilePicture: Boolean = false,
    onProfilePictureAdded: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onItemClick() } // Added this
            ) {
                if (hasProfilePicture) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        model = "https://picsum.photos/id/100/200/300",
                        contentDescription = "Your story",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.placeholder),
                        fallback = painterResource(R.drawable.placeholder)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Add profile picture",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(20.dp)
                    .background(Color.White, CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onAddPhotoClick()
                        onProfilePictureAdded()
                        Log.d("TAG", "Add photo clicked - will open photo picker")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add story",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = username,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Preview
@Composable
private fun StoryItemPreview() {
    StoryItem(
        itemId = 1,
        username = "Sammir Nasri",
        onItemClick = {}
    )
}

@Preview
@Composable
private fun UserStoryItemPreview() {
    UserStoryItem(
        itemId = 0,
        username = "Your Story",
        onItemClick = {}
    )
}

@Preview
@Composable
private fun InstagramStoryViewPreview() {
    InstagramStoryView()
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}


//import android.util.Log
//import android.widget.TextView
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.remember
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.sam.ayaana.R
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.outlined.Person
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.runtime.snapshotFlow
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import coil.compose.AsyncImage
//import com.sam.ayaana.navigation.NavigationDestination
//import com.sam.ayaana.presentation.component.homesection.PostsSection
//import com.sam.ayaana.presentation.screens.home.HomeViewModel
//import kotlinx.coroutines.delay


//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen(
//    viewModel: HomeViewModel = hiltViewModel(),
//    navController: NavHostController? = null,
//    onDetailClick: () -> Unit = {}
//) {
//    LaunchedEffect(Unit) {
//        Log.e("HOME_SCREEN", "🎬 HomeScreen is COMPOSED! navController = $navController")
//    }
//
//    AndroidView(factory = { context ->
//        TextView(context).apply {
//            text = "Debug View"
//            setBackgroundColor(android.graphics.Color.TRANSPARENT)
//            Log.e("HOME_SCREEN", "🎬 HomeScreen is COMPOSED! navController = $navController")
//        }
//    })
//
//    var hasProfilePicture by remember { mutableStateOf(false) }
//    val currentRoute = if (navController != null) {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        navBackStackEntry?.destination?.route
//    } else {
//        "home" // Default route for previews
//    }
//
//
//    Scaffold(
//        topBar = {
//            InstagramTopView(navController = navController)
//        },
//        bottomBar = {
//            NavigationBar(
//                containerColor = Color.Black,
//                contentColor = Color.White
//            ) {
//                // Home Tab - 1st position
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
//                            contentDescription = "Home",
//                            tint = if (currentRoute == "home") Color.White else Color.Gray
//                        )
//                    },
//                    label = {
//                        Text(text = "", fontSize = 0.sp)
//                    },
//                    selected = currentRoute == "home",
//                    onClick = {
//                        //selectedItem = 0
//                        navController?.navigate("home")
//                    }
//                )
//
//                // Reels Tab - 2nd position
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
//                            contentDescription = "Reels",
//                            tint = if (currentRoute == "reels") Color.White else Color.Gray
//                        )
//                    },
//                    label = {
//                        Text(text = "", fontSize = 0.sp)
//                    },
//                    selected = currentRoute == "reels",
//                    onClick = {
//                        //selectedItem = 1
//                        navController?.navigate("reels")
//                    }
//                )
//
//                // Chat Tab - 3rd position
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
//                            contentDescription = "Chat",
//                            tint = if (currentRoute == "chat") Color.White else Color.Gray
//                        )
//                    },
//                    label = {
//                        Text(text = "", fontSize = 0.sp)
//                    },
//                    selected = currentRoute == "chat",
//                    onClick = {
//                        //selectedItem = 2
//                        navController?.navigate("chat")
//                    }
//                )
//
//                // Search Tab - 4th position
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
//                            contentDescription = "Search",
//                            tint = if (currentRoute == "search") Color.White else Color.Gray
//                        )
//                    },
//                    label = {
//                        Text(text = "", fontSize = 0.sp)
//                    },
//                    selected = currentRoute == "search",
//                    onClick = {
//                        //selectedItem = 3
//                        navController?.navigate("search")
//                    }
//                )
//
//                // Profile Tab - 5th position (with 2 icons)
//                NavigationBarItem(
//                    icon = {
//                        if (hasProfilePicture) {
//                            // Profile picture when available
//                            Box(
//                                modifier = Modifier
//                                    .size(28.dp)
//                                    .clip(CircleShape)
//                            ) {
//                                AsyncImage(
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .clip(CircleShape),
//                                    model = "https://picsum.photos/id/100/200/300",
//                                    contentDescription = "Profile",
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
//                        } else {
//                            // Default person icon when no profile picture
//                            Icon(
//                                painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
//                                contentDescription = "Profile",
//                                tint = if (currentRoute == "profile") Color.White else Color.Gray
//                            )
//                        }
//                    },
//                    label = {
//                        Text(text = "", fontSize = 0.sp)
//                    },
//                    selected = currentRoute == "profile",
//                    onClick = {
//                        //selectedItem = 4
//                        navController?.navigate("profile")
//                    }
//                )
//            }
//        }
//
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(Color.White)
//                .clickable{
//                    Log.e("COLUMN_TEST", "🎯 MAIN COLUMN CLICKED!")
//                    throw RuntimeException("MAIN COLUMN CLICK TEST")
//                },
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            InstagramStoryView(
//                hasProfilePicture = hasProfilePicture,
//                onProfilePictureAdded = { hasProfilePicture = true }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            PostsSection(
//                viewModel = viewModel,
//                onProfileClick = { userId ->
//                    navController?.navigate("profile/$userId")
//                },
//                onLikeClick = {postId, isCurrentlyLiked ->
//                    viewModel.toggleLike(postId, isCurrentlyLiked)
////                    viewModel.likePost(postId)
//                },
//                onCommentClick = {postId ->
//                    // Navigate to comments screen
//                    // navController?.navigate("comments/$postId")
//                    println("Opening comments for post: $postId")
//                },
//                onShareClick = {postId ->
//                    viewModel.sharePost(postId)
//                },
//                onRepostClick = { postId, isCurrentlyReposted ->
//                    viewModel.toggleRepost(postId, isCurrentlyReposted)
//
//                },
//                onMoreOptionsClick = {postId ->
//                    viewModel.showMoreOptions(postId)
//                },
//                onSaveClick = {postId ->
//                    viewModel.savePost(postId)
//                }
//
//            )
//            // Temporary placeholder for posts
////            Box(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .weight(1f),
////                contentAlignment = Alignment.Center
////            ) {
////                Text("Posts feed will be here")
////            }
//
//        }
//    }
//}
//
//
//
//@Composable
//fun InstagramTopView(
//    navController: NavHostController? = null
//) {
//    LaunchedEffect(Unit) {
//        Log.d("NAV_TEST", "🎬 InstagramTopView is being composed!")
//    }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(start = 16.dp, top = 12.dp, end = 24.dp, bottom = 12.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//
//    ) {
//        // Instagram Logo with custom font
//        Text(
//            text = "Ayaana",
//            style = MaterialTheme.typography.headlineSmall.copy(
//                fontWeight = FontWeight.Bold,
//                fontFamily = FontFamily.Cursive,
//                fontSize = 24.sp
//            ),
//            color = MaterialTheme.colorScheme.onBackground
//        )
//
//        // Icons Row
//        Row (
//            horizontalArrangement = Arrangement.spacedBy(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            // Replace your add icon with this:
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Test with FloatingActionButton instead of Icon
//                FloatingActionButton(
//                    onClick = {
//                        Log.e("FAB_TEST", "🎯 FLOATING BUTTON CLICKED!")
//                        throw RuntimeException("FLOATING BUTTON CLICK TEST")
//                    },
//                    modifier = Modifier.size(40.dp),
//                    containerColor = Color.Red
//                ) {
//                    Icon(Icons.Default.Add, "Add", tint = Color.White)
//                }
//
//                // Keep your existing notification icon
//                Icon(
//                    imageVector = Icons.Default.Favorite,
//                    contentDescription = "Notifications",
//                    tint = MaterialTheme.colorScheme.onBackground,
//                    modifier = Modifier
//                        .size(24.dp)
//                        .clickable { /* Handle notifications */ }
//                )
//            }
//
//            // Add icon
////            Icon(
////                imageVector = Icons.Default.Add,
////                contentDescription = "Add post",
////                tint = MaterialTheme.colorScheme.onBackground,
////                modifier = Modifier
////                    .size(40.dp)
////                    .background(Color.Red)
////                    .clickable {
////                        throw RuntimeException("🔥 ADD BUTTON DEFINITELY CLICKED - NAVIGATION TEST")
////                         navController?.navigate(NavigationDestination.CreatePost.route)
//////                        Log.e("NAV_TEST", "🔥 ADD BUTTON CLICKED - FORCE CRASH FOR TEST")
//////                        throw RuntimeException("TEST: Add button was clicked!")
////
//////                        Log.d("NAVIGATION_DEBUG", "🎯 Add button CLICKED!")
//////                        Log.d("NAVIGATION_DEBUG", "📱 navController = $navController")
//////                        Log.d("NAVIGATION_DEBUG", "🛣️ Route = ${NavigationDestination.CreatePost.route}")
//////
//////                        if (navController != null) {
//////                            Log.d("NAVIGATION_DEBUG", "🚀 Attempting navigation to create_post")
//////                            try {
//////                                navController.navigate(NavigationDestination.CreatePost.route)
//////                                Log.d("NAVIGATION_DEBUG", "✅ Navigation command sent successfully")
//////                            } catch (e: Exception) {
//////                                Log.e("NAVIGATION_DEBUG", "❌ Navigation failed: ${e.message}", e)
//////                            }
//////                        } else {
//////                            Log.e("NAVIGATION_DEBUG", "💥 CRITICAL: navController is NULL!")
//////                        }
////////
////                        //navController?.navigate(NavigationDestination.CreatePost.route)
////                    }
////            )
//
//            //Notification icon
//            Icon(
//                imageVector = Icons.Default.Favorite,
//                contentDescription = "Notifications",
//                tint = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier
//                    .size(24.dp)
//                    .clickable { /* Handle notifications */ }
//            )
//        }
//
//    }
//}
//
////@Composable
////fun InstagramTopView(
////    navController: NavHostController? = null
////) {
////    // TEMPORARILY remove all styling and use a simple Box
////    Box(
////        modifier = Modifier
////            .fillMaxWidth()
////            .height(60.dp)
////            .background(Color.Yellow) // Make it very visible
////            .clickable {
////                Log.e("TOP_BAR_TEST", "🎯 ENTIRE TOP BAR CLICKED!")
////                throw RuntimeException("TOP BAR CLICK TEST")
////            }
////    ) {
////        Text(
////            text = "Ayaana - CLICK ANYWHERE HERE",
////            modifier = Modifier.align(Alignment.Center),
////            color = Color.Black,
////            fontWeight = FontWeight.Bold
////        )
////    }
////}
//
//@Composable
//fun InstagramStoryView(
//    onStoryClick: (Int) -> Unit = {},
//    hasProfilePicture: Boolean = false,
//    onProfilePictureAdded: () -> Unit = {}
//) {
//    var stories by remember { mutableStateOf<List<Int>>(emptyList()) }
//    var currentPage by remember { mutableIntStateOf(1) }
//    var isLoading by remember { mutableStateOf(false) }
//    val lazyRowState = rememberLazyListState()
//    val pageSize = 20
//
//    // Load initial stories
//    LaunchedEffect(Unit) {
//        if (stories.isEmpty()){
//            val initialStories = (1..pageSize).toList()
//            stories = initialStories
//        }
//    }
//
//    // Detect when we're near the end to load more stories
//    LaunchedEffect(lazyRowState) {
//        snapshotFlow { lazyRowState.layoutInfo.visibleItemsInfo}
//            .collect { visibleItems ->
//                if (visibleItems.isNotEmpty() && !isLoading){
//                    val lastVisibleItem = visibleItems.last()
//                    if (lastVisibleItem.index >= stories.size - 5){
//                        isLoading = true
//                        delay(1000)
//
//                        val newStories = (1..pageSize).map{
//                            it + (currentPage * pageSize)
//                        }
//                        stories = stories + newStories
//                        currentPage++
//                        isLoading = false
//                    }
//                }
//            }
//    }
//
//    LazyRow(
//        state = lazyRowState,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//
//        // First item - User's own story with add button
//        item{
//            UserStoryItem(
//                itemId = 0,
//                username = "Your Story",
//                onItemClick = {
//                    Log.d("TAG", "UserStoryItem clicked")
//                },
//                hasProfilePicture = hasProfilePicture,
//                onProfilePictureAdded = onProfilePictureAdded
//            )
//        }
//
//        // Other stories - using actual usernames from your image
//        items(
//            items = stories,
//            key = { itemId -> itemId }
//        ) { itemId ->
//            // Use different usernames for first few items to match your design
//            val username = when (itemId) {
//                1 -> "natasha_mukami"
//                2 -> "_wanjau_"
//                3 -> "shawn_kip"
//                4 -> "hadiya_rajesh"
//                5 -> "dr_magnito"
//                6 -> "arjun_vyas"
//                7 -> "sauro_bhat"
//                8 -> "pauline_claude"
//                9 -> "john_doe"
//                10 -> "jane_smith"
//                11 -> "Leonard_Otie"
//                12 -> "emily_davis"
//                13 -> "joan_johnson"
//                14 -> "olivia_wilson"
//                15 -> "william_brown"
//
//                else -> "user_$itemId"
//            }
//
//            StoryItem(
//                itemId = itemId,
//                username = username,
//                onItemClick = {onStoryClick(itemId)}
//            )
//        }
//
//            // Loading indicator at the end
//            if (isLoading){
//                item{
//                    Box(
//                        modifier = Modifier
//                            .size(64.dp)
//                            .padding(8.dp),
//                        contentAlignment = Alignment.Center
//
//                    ){
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(20.dp),
//                            strokeWidth = 2.dp,
//                        )
//                    }
//                }
//            }
//        }
//
//}
//
//@Composable
//fun StoryItem(
//    modifier: Modifier = Modifier,
//    itemId: Int,
//    username: String,
//    onItemClick: () -> Unit
//) {
//    val gradientColors =
//        listOf(
//            Color(0xFF833AB4), Color(0xFFC13584), Color(0xFFE1306C), Color(0xFFFD1D1D),
//            Color(0xFFF56040), Color(0xFFF77737), Color(0xFFFCAF45), Color(0xFFFFDC80)
//        )
//
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
//            .width(70.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(64.dp)
//                .border(
//                    width = 2.dp,
//                    brush = Brush.linearGradient(colors = gradientColors),
//                    shape = CircleShape
//                )
//                .padding(2.dp)
//                .background(Color.White, CircleShape)
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                ) { onItemClick() },
//            contentAlignment = Alignment.Center
//        ) {
//            AsyncImage(
//                modifier = Modifier
//                    .size(60.dp)
//                    .clip(CircleShape),
//                model = "https://picsum.photos/id/${itemId}/200/300",
//                contentDescription = "Story",
//                contentScale = ContentScale.Crop,
//                placeholder = painterResource(R.drawable.placeholder),
//                fallback = painterResource(R.drawable.placeholder)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(4.dp))
//
//        Text(
//            text = username,
//            style = MaterialTheme.typography.bodySmall,
//            fontSize = 12.sp,
//            maxLines = 1
//        )
//    }
//}
//
//@Composable
//fun UserStoryItem(
//    modifier: Modifier = Modifier,
//    itemId: Int,
//    username: String,
//    onItemClick: () -> Unit,
//    onAddPhotoClick: () -> Unit = {},
//    hasProfilePicture: Boolean = false,
//    onProfilePictureAdded: () -> Unit = {}
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
//            .width(70.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(64.dp)
//                .clickable (
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                )
//                { onItemClick() }
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(64.dp)
//                    .clip(CircleShape)
//                    .background(Color.Black, CircleShape)
//            ) {
//                if (hasProfilePicture) {
//                    // CHANGED: Show actual profile picture if available
//                    AsyncImage(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(CircleShape),
//                        model = "https://picsum.photos/id/100/200/300", // Same image as bottom bar
//                        contentDescription = "Your story",
//                        contentScale = ContentScale.Crop,
//                        placeholder = painterResource(R.drawable.placeholder),
//                        fallback = painterResource(R.drawable.placeholder)
//                    )
//                } else {
//                    // CHANGED: Show placeholder when no profile picture
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(Color.LightGray),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Outlined.Person,
//                            contentDescription = "Add profile picture",
//                            tint = Color.White,
//                            modifier = Modifier.size(32.dp)
//                        )
//                    }
//                }
//            }
//
//            // Small add icon overlay
//
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .size(20.dp)
//                    .background(Color.White, CircleShape)
//                    .border(2.dp, Color.White, CircleShape)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) {
//                        onAddPhotoClick()
//                        onProfilePictureAdded()
//                        Log.d("TAG", "Add photo clicked - will open photo picker")
//                        // TODO: Add logic to open photo picker/camera
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                    Icon(
//                        imageVector = Icons.Default.Add,
//                        contentDescription = "Add story",
//                        tint = MaterialTheme.colorScheme.onBackground,
//                        //tint = Color.Black,
//                        modifier = Modifier.size(16.dp)
//                    )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(4.dp))
//
//        Text(
//            text = username,
//            style = MaterialTheme.typography.bodySmall,
//            fontSize = 12.sp,
//            maxLines = 1
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun StoryItemPreview() {
//    StoryItem(
//        itemId = 1,
//        username = "Sammir Nasri",
//        onItemClick = {}
//    )
//}
//
//@Preview
//@Composable
//private fun UserStoryItemPreview() {
//    UserStoryItem(
//        itemId = 0,
//        username = "Your Story",
//        onItemClick = {}
//    )
//}
//
//@Preview
//@Composable
//private fun InstagramStoryViewPreview() {
//    InstagramStoryView()
//}
//
//@Preview
//@Composable
//private fun HomeScreenPreview() {
//    HomeScreen(
//        onDetailClick = {
//            println("Navigate to detail")
//        }
//    )
//
////    val navController = rememberNavController()
////     HomeScreen(navController = navController)
//}
