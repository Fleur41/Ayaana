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
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.sam.ayaana.R
import com.sam.ayaana.navigation.NavigationDestination
import com.sam.ayaana.presentation.component.homesection.PostsSection
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController? = null,
) {

    // COLLECT PROFILE IMAGE PATH FROM SHARED REPOSITORY
    val profileImagePath by viewModel.profileImagePath.collectAsStateWithLifecycle(initialValue = null)

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
                // PASS PROFILE IMAGE PATH TO STORY VIEW
                InstagramStoryView(
                    navController = navController,
                    profileImagePath = profileImagePath,
                    onStoryClick = {},
                    onAddPhotoClick = {
                        navController?.navigate(NavigationDestination.CreatePost.route)
                    }
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
                    // USE SHARED PROFILE IMAGE PATH
                    if (profileImagePath != null) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                model = File(profileImagePath!!).toUri(),
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
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
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


@Composable
fun InstagramStoryView(
    navController: NavHostController? = null,
    profileImagePath: String? = null,
    onStoryClick: (Int) -> Unit = {},
    onAddPhotoClick: () -> Unit = {}
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
                profileImagePath = profileImagePath, // ✅ PASS SHARED IMAGE
                onItemClick = { Log.d("TAG", "UserStoryItem clicked") },
                onAddPhotoClick = onAddPhotoClick
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
    profileImagePath: String?,
    onItemClick: () -> Unit,
    onAddPhotoClick: () -> Unit = {},
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
                    ) { onItemClick() }
            ) {
                // ✅ USE SHARED PROFILE IMAGE PATH
                val imageModel = remember(profileImagePath) {
                    if (profileImagePath != null) {
                        File(profileImagePath).toUri()
                    } else {
                        null
                    }
                }

                if (imageModel != null) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        model = imageModel,
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
        profileImagePath = null,
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


//package com.sam.ayaana.presentation.screens.home
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.outlined.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import coil.compose.AsyncImage
//import com.sam.ayaana.R
//import com.sam.ayaana.navigation.NavigationDestination
//import com.sam.ayaana.presentation.component.homesection.PostsSection
//import kotlinx.coroutines.delay
//
//
//@Composable
//fun HomeScreen(
//    viewModel: HomeViewModel = hiltViewModel(),
//    navController: NavHostController? = null,
//    onDetailClick: () -> Unit = {}
//) {
//
//    val profileImagePath by viewModel.profileImagePath.collectAsStateWithLifecycle(initialValue = null)
//    var hasProfilePicture by remember { mutableStateOf(false) }
//    val currentRoute = if (navController != null) {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        navBackStackEntry?.destination?.route
//    } else {
//        "home"
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            InstagramTopView(
//                navController = navController,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(MaterialTheme.colorScheme.background)
//            )
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .weight(1f)
//                    .background(Color.White),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                InstagramStoryView(
//                    navController = navController,
//                    hasProfilePicture = hasProfilePicture,
//                    onProfilePictureAdded = { hasProfilePicture = true }
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                PostsSection(
//                    viewModel = viewModel,
//                    onProfileClick = { userId ->
//                        navController?.navigate("profile/$userId")
//                    },
//                    onLikeClick = { postId, isCurrentlyLiked ->
//                        viewModel.toggleLike(postId, isCurrentlyLiked)
//                    },
//                    onCommentClick = { postId ->
//                        println("Opening comments for post: $postId")
//                    },
//                    onShareClick = { postId ->
//                        viewModel.sharePost(postId)
//                    },
//                    onRepostClick = { postId, isCurrentlyReposted ->
//                        viewModel.toggleRepost(postId, isCurrentlyReposted)
//                    },
//                    onMoreOptionsClick = { postId ->
//                        viewModel.showMoreOptions(postId)
//                    },
//                    onSaveClick = { postId ->
//                        viewModel.savePost(postId)
//                    }
//                )
//            }
//        }
//
//        NavigationBar(
//            modifier = Modifier.align(Alignment.BottomCenter),
//            containerColor = Color.Black,
//            contentColor = Color.White
//        ) {
//            NavigationBarItem(
//                icon = {
//                    Icon(
//                        painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
//                        contentDescription = "Home",
//                        tint = if (currentRoute == "home") Color.White else Color.Gray
//                    )
//                },
//                label = { Text(text = "", fontSize = 0.sp) },
//                selected = currentRoute == "home",
//                onClick = { navController?.navigate("home") }
//            )
//
//            NavigationBarItem(
//                icon = {
//                    Icon(
//                        painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
//                        contentDescription = "Reels",
//                        tint = if (currentRoute == "reels") Color.White else Color.Gray
//                    )
//                },
//                label = { Text(text = "", fontSize = 0.sp) },
//                selected = currentRoute == "reels",
//                onClick = { navController?.navigate("reels") }
//            )
//
//            NavigationBarItem(
//                icon = {
//                    Icon(
//                        painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
//                        contentDescription = "Chat",
//                        tint = if (currentRoute == "chat") Color.White else Color.Gray
//                    )
//                },
//                label = { Text(text = "", fontSize = 0.sp) },
//                selected = currentRoute == "chat",
//                onClick = { navController?.navigate("chat") }
//            )
//
//            NavigationBarItem(
//                icon = {
//                    Icon(
//                        painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
//                        contentDescription = "Search",
//                        tint = if (currentRoute == "search") Color.White else Color.Gray
//                    )
//                },
//                label = { Text(text = "", fontSize = 0.sp) },
//                selected = currentRoute == "search",
//                onClick = { navController?.navigate("search") }
//            )
//
//            NavigationBarItem(
//                icon = {
//                    if (hasProfilePicture) {
//                        Box(
//                            modifier = Modifier
//                                .size(28.dp)
//                                .clip(CircleShape)
//                        ) {
//                            AsyncImage(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .clip(CircleShape),
//                                model = "https://picsum.photos/id/100/200/300",
//                                contentDescription = "Profile",
//                                contentScale = ContentScale.Crop
//                            )
//                        }
//                    } else {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
//                            contentDescription = "Profile",
//                            tint = if (currentRoute == "profile") Color.White else Color.Gray
//                        )
//                    }
//                },
//                label = { Text(text = "", fontSize = 0.sp) },
//                selected = currentRoute == "profile",
//                onClick = { navController?.navigate("profile") }
//            )
//        }
//    }
//}
//
//
//@Composable
//fun InstagramTopView(
//    modifier: Modifier = Modifier,
//    navController: NavHostController? = null,
//) {
//    Row(
//        modifier = modifier
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
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Add post",
//                tint = MaterialTheme.colorScheme.onBackground,
//                modifier = Modifier
//                    .size(40.dp)
//                    .clickable {
//                        navController?.navigate(NavigationDestination.CreatePost.route)
//                    }
//            )
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
//
//
//@Composable
//fun InstagramStoryView(
//    navController: NavHostController? = null,
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
//    LaunchedEffect(Unit) {
//        if (stories.isEmpty()) {
//            val initialStories = (1..pageSize).toList()
//            stories = initialStories
//        }
//    }
//
//    LaunchedEffect(lazyRowState) {
//        snapshotFlow { lazyRowState.layoutInfo.visibleItemsInfo }
//            .collect { visibleItems ->
//                if (visibleItems.isNotEmpty() && !isLoading) {
//                    val lastVisibleItem = visibleItems.last()
//                    if (lastVisibleItem.index >= stories.size - 5) {
//                        isLoading = true
//                        delay(1000)
//
//                        val newStories = (1..pageSize).map {
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
//        item {
//            UserStoryItem(
//                itemId = 0,
//                username = "Your Story",
//                onItemClick = { Log.d("TAG", "UserStoryItem clicked") },
//                hasProfilePicture = hasProfilePicture,
//                onProfilePictureAdded = onProfilePictureAdded,
//                onAddPhotoClick = {
//                    navController?.navigate(NavigationDestination.CreatePost.route)
//                }
//            )
//        }
//
//        items(
//            items = stories,
//            key = { itemId -> itemId }
//        ) { itemId ->
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
//                else -> "user_$itemId"
//            }
//
//            StoryItem(
//                itemId = itemId,
//                username = username,
//                onItemClick = { onStoryClick(itemId) }
//            )
//        }
//
//        if (isLoading) {
//            item {
//                Box(
//                    modifier = Modifier
//                        .size(64.dp)
//                        .padding(8.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(20.dp),
//                        strokeWidth = 2.dp,
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun StoryItem(
//    modifier: Modifier = Modifier,
//    itemId: Int,
//    username: String,
//    onItemClick: () -> Unit
//) {
//    val gradientColors = listOf(
//        Color(0xFF833AB4), Color(0xFFC13584), Color(0xFFE1306C), Color(0xFFFD1D1D),
//        Color(0xFFF56040), Color(0xFFF77737), Color(0xFFFCAF45), Color(0xFFFFDC80)
//    )
//
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier.width(70.dp)
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
//        modifier = modifier.width(70.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(64.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .size(64.dp)
//                    .clip(CircleShape)
//                    .background(Color.Black, CircleShape)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null
//                    ) { onItemClick() } // Added this
//            ) {
//                if (hasProfilePicture) {
//                    AsyncImage(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .clip(CircleShape),
//                        model = "https://picsum.photos/id/100/200/300",
//                        contentDescription = "Your story",
//                        contentScale = ContentScale.Crop,
//                        placeholder = painterResource(R.drawable.placeholder),
//                        fallback = painterResource(R.drawable.placeholder)
//                    )
//                } else {
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
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add story",
//                    tint = MaterialTheme.colorScheme.onBackground,
//                    modifier = Modifier.size(16.dp)
//                )
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
//    HomeScreen()
//}
//
