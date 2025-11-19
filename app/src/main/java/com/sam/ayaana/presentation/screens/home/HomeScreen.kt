package com.sam.ayaana.presentation.screens.home


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sam.ayaana.R
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.sam.ayaana.presentation.component.homesection.PostsSection
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
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
        "home" // Default route for previews
    }


    Scaffold(
        topBar = {
            InstagramTopView()
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                // Home Tab - 1st position
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
                            contentDescription = "Home",
                            tint = if (currentRoute == "home") Color.White else Color.Gray
                        )
                    },
                    label = {
                        Text(text = "", fontSize = 0.sp)
                    },
                    selected = currentRoute == "home",
                    onClick = {
                        //selectedItem = 0
                        navController?.navigate("home")
                    }
                )

                // Reels Tab - 2nd position
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
                            contentDescription = "Reels",
                            tint = if (currentRoute == "reels") Color.White else Color.Gray
                        )
                    },
                    label = {
                        Text(text = "", fontSize = 0.sp)
                    },
                    selected = currentRoute == "reels",
                    onClick = {
                        //selectedItem = 1
                        navController?.navigate("reels")
                    }
                )

                // Chat Tab - 3rd position
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
                            contentDescription = "Chat",
                            tint = if (currentRoute == "chat") Color.White else Color.Gray
                        )
                    },
                    label = {
                        Text(text = "", fontSize = 0.sp)
                    },
                    selected = currentRoute == "chat",
                    onClick = {
                        //selectedItem = 2
                        navController?.navigate("chat")
                    }
                )

                // Search Tab - 4th position
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
                            contentDescription = "Search",
                            tint = if (currentRoute == "search") Color.White else Color.Gray
                        )
                    },
                    label = {
                        Text(text = "", fontSize = 0.sp)
                    },
                    selected = currentRoute == "search",
                    onClick = {
                        //selectedItem = 3
                        navController?.navigate("search")
                    }
                )

                // Profile Tab - 5th position (with 2 icons)
                NavigationBarItem(
                    icon = {
                        if (hasProfilePicture) {
                            // Profile picture when available
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
                            // Default person icon when no profile picture
                            Icon(
                                painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
                                contentDescription = "Profile",
                                tint = if (currentRoute == "profile") Color.White else Color.Gray
                            )
                        }
                    },
                    label = {
                        Text(text = "", fontSize = 0.sp)
                    },
                    selected = currentRoute == "profile",
                    onClick = {
                        //selectedItem = 4
                        navController?.navigate("profile")
                    }
                )
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            InstagramStoryView(
                hasProfilePicture = hasProfilePicture,
                onProfilePictureAdded = { hasProfilePicture = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PostsSection(
                viewModel = viewModel,
                onProfileClick = { userId ->
                    navController?.navigate("profile/$userId")
                },
                onLikeClick = {postId, isCurrentlyLiked ->
                    viewModel.toggleLike(postId, isCurrentlyLiked)
//                    viewModel.likePost(postId)
                },
                onCommentClick = {postId ->
                    // Navigate to comments screen
                    // navController?.navigate("comments/$postId")
                    println("Opening comments for post: $postId")
                },
                onShareClick = {postId ->
                    viewModel.sharePost(postId)
                },
                onRepostClick = { postId, isCurrentlyReposted ->
                    viewModel.toggleRepost(postId, isCurrentlyReposted)

                },
                onMoreOptionsClick = {postId ->
                    viewModel.showMoreOptions(postId)
                },
                onSaveClick = {postId ->
                    viewModel.savePost(postId)
                }

            )
            // Temporary placeholder for posts
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("Posts feed will be here")
//            }

        }
    }
}


@Composable
fun InstagramTopView() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 12.dp, end = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically

    ) {
        // Instagram Logo with custom font
        Text(
            text = "Ayaana",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                fontSize = 24.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Icons Row
        Row (
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            // Add icon
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add post",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {/* Handle add post */ }
            )

            //Notification icon
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { /* Handle notifications */ }
            )
        }

    }
}

@Composable
fun InstagramStoryView(
    onStoryClick: (Int) -> Unit = {},
    hasProfilePicture: Boolean = false,
    onProfilePictureAdded: () -> Unit = {}
) {
    var stories by remember { mutableStateOf<List<Int>>(emptyList()) }
    var currentPage by remember { mutableIntStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }
    val lazyRowState = rememberLazyListState()
    val pageSize = 20

    // Load initial stories
    LaunchedEffect(Unit) {
        if (stories.isEmpty()){
            val initialStories = (1..pageSize).toList()
            stories = initialStories
        }
    }

    // Detect when we're near the end to load more stories
    LaunchedEffect(lazyRowState) {
        snapshotFlow { lazyRowState.layoutInfo.visibleItemsInfo}
            .collect { visibleItems ->
                if (visibleItems.isNotEmpty() && !isLoading){
                    val lastVisibleItem = visibleItems.last()
                    if (lastVisibleItem.index >= stories.size - 5){
                        isLoading = true
                        delay(1000)

                        val newStories = (1..pageSize).map{
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

        // First item - User's own story with add button
        item{
            UserStoryItem(
                itemId = 0,
                username = "Your Story",
                onItemClick = {
                    Log.d("TAG", "UserStoryItem clicked")
                },
                hasProfilePicture = hasProfilePicture,
                onProfilePictureAdded = onProfilePictureAdded
            )
        }

        // Other stories - using actual usernames from your image
        items(
            items = stories,
            key = { itemId -> itemId }
        ) { itemId ->
            // Use different usernames for first few items to match your design
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
                onItemClick = {onStoryClick(itemId)}
            )
        }

            // Loading indicator at the end
            if (isLoading){
                item{
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center

                    ){
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
    val gradientColors =
        listOf(
            Color(0xFF833AB4), Color(0xFFC13584), Color(0xFFE1306C), Color(0xFFFD1D1D),
            Color(0xFFF56040), Color(0xFFF77737), Color(0xFFFCAF45), Color(0xFFFFDC80)
        )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(70.dp)
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
        modifier = modifier
            .width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clickable (
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                )
                { onItemClick() }
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black, CircleShape)
            ) {
                if (hasProfilePicture) {
                    // CHANGED: Show actual profile picture if available
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        model = "https://picsum.photos/id/100/200/300", // Same image as bottom bar
                        contentDescription = "Your story",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.placeholder),
                        fallback = painterResource(R.drawable.placeholder)
                    )
                } else {
                    // CHANGED: Show placeholder when no profile picture
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

            // Small add icon overlay

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
                        // TODO: Add logic to open photo picker/camera
                    },
                contentAlignment = Alignment.Center
            ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add story",
                        tint = MaterialTheme.colorScheme.onBackground,
                        //tint = Color.Black,
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
    HomeScreen(
        onDetailClick = {
            println("Navigate to detail")
        }
    )

//    val navController = rememberNavController()
//     HomeScreen(navController = navController)
}
