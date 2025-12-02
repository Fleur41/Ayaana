package com.sam.ayaana.presentation.screens.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.android.play.integrity.internal.u
import com.sam.ayaana.R
import com.sam.ayaana.Utils.GalleryPicker
import com.sam.ayaana.domain.model.ProfileTab
import com.sam.ayaana.domain.model.User
import com.sam.ayaana.presentation.components.profile.ProfileHeaderSection
import com.sam.ayaana.presentation.components.profile.ProfilePostsGrid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ProfileScreen(
    navController: NavHostController? = null,
    userId: String? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentRoute = if (navController != null) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        navBackStackEntry?.destination?.route
    } else {
        "profile"
    }

    LaunchedEffect(key1 = userId) {
        viewModel.loadProfile(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isCurrentUser = userId == null
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = GalleryPicker.rememberGalleryLauncher(
        onMediaSelected = { uris ->
            uris.firstOrNull()?.let { uri ->
                // Store the actual Uri locally
                selectedImageUri = uri

                // Update ViewModel with local Uri (not mock URL)
                viewModel.updateProfileImage(uri)
            }
        },
//        onMediaSelected = { uris ->
//            uris.firstOrNull()?.let { uri ->
//                // Show temporary mock image while "uploading"
//                // Immediate UI update for better UX
//                viewModel.uiState.value.user?.let { currentUser ->
//                    val tempImageUrl = "https://picsum.photos/id/${(System.currentTimeMillis() % 100).toInt()}/200/200"
//                    val tempUser = currentUser.copy(profilePicture = tempImageUrl)
//                    viewModel.updateUiStateUser(tempUser)
//                }
//
//                // Simulate upload with delay
//                viewModel.updateProfileImage(uri)
//            }
//        },
//
        maxSelection = 1,
        isVideoOnly = false
    )
    // Remember the current user to update bottom nav
    val currentUser = remember { mutableStateOf(uiState.user) }

    LaunchedEffect(uiState.user) {
        currentUser.value = uiState.user
    }
    Scaffold(
        topBar = {
            if (isCurrentUser) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    Text(
                        text = uiState.user?.username ?: "Profile",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.user?.username ?: "Profile",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController?.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Handle more options */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (isCurrentUser) {
                ProfileBottomNavBar(
                    currentRoute = currentRoute,
                    navController = navController,
                    user = uiState.user
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            when {
                uiState.isLoading -> LoadingState()
                uiState.user != null -> ProfileContent(
                    uiState = uiState,
                    onEditProfileClick = { navController?.navigate("edit_profile") },
                    onShareProfileClick = { viewModel.shareProfile(context) },
                    onFollowClick = { viewModel.toggleFollow() },
                    onMessageClick = {
                        uiState.user?.let { user ->
                            navController?.navigate("chat_detail/${user.id}")
                        }
                    },
                    onEmailClick = { viewModel.openEmail(context) },
                    onAddProfilePhotoClick = {
                        if (galleryLauncher.permissionState.status.isGranted){
                            galleryLauncher.launchGallery()
                        } else{
                            galleryLauncher.permissionState.launchPermissionRequest()
                        }
                        // viewModel.pickProfileImage(context)
                    },
                    onPostClick = { postId -> navController?.navigate("post_detail/$postId") },
                    onTabSelected = { tab -> viewModel.onTabSelected(tab) }
                )
                uiState.error != null -> ErrorState(
                    error = uiState.error,
                    onRetry = { viewModel.retry() },
                    onGoBack = { navController?.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    uiState: com.sam.ayaana.domain.model.ProfileUiState,
    onEditProfileClick: () -> Unit,
    onShareProfileClick: () -> Unit,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onEmailClick: () -> Unit,
    onAddProfilePhotoClick: () -> Unit,
    onPostClick: (String) -> Unit,
    onTabSelected: (ProfileTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // Use Column without vertical scroll - let the ProfilePostsGrid handle its own scrolling
    Column(modifier = modifier.fillMaxSize()) {
        uiState.user?.let { user ->
            ProfileHeaderSection(
                user = user,
                onEditProfileClick = onEditProfileClick,
                onShareProfileClick = onShareProfileClick,
                onFollowClick = onFollowClick,
                onMessageClick = onMessageClick,
                onEmailClick = onEmailClick,
                onAddProfilePhotoClick = onAddProfilePhotoClick,
                modifier = Modifier.fillMaxWidth()
            )

            // This should handle its own scrolling internally
            ProfilePostsGrid(
                user = user,
                posts = uiState.posts,
                taggedPosts = uiState.taggedPosts,
                reels = uiState.reels,
                onPostClick = onPostClick,
                onTabSelected = onTabSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Takes remaining space
            )
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF0095F6))
    }
}

@Composable
private fun ErrorState(
    error: String?,
    onRetry: () -> Unit,
    onGoBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = error ?: "An error occurred",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0095F6))
            ) {
                Text("Try Again")
            }
            TextButton(onClick = onGoBack, modifier = Modifier.padding(top = 8.dp)) {
                Text("Go Back")
            }
        }
    }
}

@Composable
private fun ProfileBottomNavBar(
    currentRoute: String?,
    navController: NavHostController?,
    user: User?
) {
    NavigationBar(
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        // Home
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

        // Search
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

        // Reels
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

        // Chat
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

        // Profile (with profile image or default icon)
        NavigationBarItem(
            icon = {
                // Check for local Uri first, then remote URL
                val imageModel = user?.let {
                    it.localProfileUri ?: it.profilePicture
                }

                if (imageModel != null && (imageModel is Uri || (imageModel is String && imageModel.isNotEmpty()))) {
                    // Show profile image
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(
                                1.dp,
                                if (currentRoute == "profile") Color.White else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // Show default profile icon
                    Icon(
                        painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
                        contentDescription = "Profile",
                        tint = if (currentRoute == "profile") Color.White else Color.Gray
                    )
                }
            },
            label = { Text(text = "", fontSize = 0.sp) },
            selected = currentRoute == "profile",
            onClick = { /* Already on profile screen */ }
        )
    }
//        NavigationBarItem(
//            icon = {
//                if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
//                    // Show profile image
//                    Box(
//                        modifier = Modifier
//                            .size(24.dp)
//                            .clip(CircleShape)
//                            .border(
//                                1.dp,
//                                if (currentRoute == "profile") Color.White else Color.Transparent,
//                                CircleShape
//                            )
//                    ) {
//                        AsyncImage(
//                            model = profileImageUrl,
//                            contentDescription = "Profile",
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .clip(CircleShape),
//                            contentScale = ContentScale.Crop
//                        )
//                    }
//                } else {
//                    // Show default profile icon
//                    Icon(
//                        painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
//                        contentDescription = "Profile",
//                        tint = if (currentRoute == "profile") Color.White else Color.Gray
//                    )
//                }
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "profile",
//            onClick = { /* Already on profile screen */ }
//        )
//    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen()
    }
}

//package com.sam.ayaana.presentation.screens.profile
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.sam.ayaana.R
//import com.sam.ayaana.domain.model.ProfileTab
//import com.sam.ayaana.presentation.components.profile.ProfileHeaderSection
//import com.sam.ayaana.presentation.components.profile.ProfilePostsGrid
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileScreen(
//    navController: NavHostController? = null,
//    userId: String? = null,
//    viewModel: ProfileViewModel = hiltViewModel()
//) {
//    Log.d("PROFILE_SCREEN", "ProfileScreen composable called")
//    val currentRoute = if (navController != null) {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        navBackStackEntry?.destination?.route
//    } else {
//        "profile"
//    }
//
//    LaunchedEffect(key1 = userId) {
//        viewModel.loadProfile(userId)
//    }
//
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//    val isCurrentUser = userId == null
//
//    Scaffold(
//        topBar = {
//            if (isCurrentUser) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(Color.Black)
//                        .padding(16.dp)
//                ) {
//                    Text(
//                        text = uiState.user?.username ?: "Profile",
//                        color = Color.White,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            } else {
//                TopAppBar(
//                    title = {
//                        Text(
//                            text = uiState.user?.username ?: "Profile",
//                            fontWeight = FontWeight.Bold
//                        )
//                    },
//                    navigationIcon = {
//                        IconButton(onClick = { navController?.popBackStack() }) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "Back"
//                            )
//                        }
//                    },
//                    actions = {
//                        IconButton(onClick = { /* Handle more options */ }) {
//                            Icon(
//                                imageVector = Icons.Default.MoreVert,
//                                contentDescription = "More options"
//                            )
//                        }
//                    }
//                )
//            }
//        },
//        bottomBar = {
//            if (isCurrentUser) {
//                NavigationBar(
//                    containerColor = Color.Black,
//                    contentColor = Color.White
//                ) {
//                    NavigationBarItem(
//                        icon = {
//                            Icon(
//                                painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
//                                contentDescription = "Home",
//                                tint = if (currentRoute == "home") Color.White else Color.Gray
//                            )
//                        },
//                        label = { Text(text = "", fontSize = 0.sp) },
//                        selected = currentRoute == "home",
//                        onClick = { navController?.navigate("home") }
//                    )
//
//                    NavigationBarItem(
//                        icon = {
//                            Icon(
//                                painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
//                                contentDescription = "Reels",
//                                tint = if (currentRoute == "reels") Color.White else Color.Gray
//                            )
//                        },
//                        label = { Text(text = "", fontSize = 0.sp) },
//                        selected = currentRoute == "reels",
//                        onClick = { navController?.navigate("reels") }
//                    )
//
//                    NavigationBarItem(
//                        icon = {
//                            Icon(
//                                painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
//                                contentDescription = "Chat",
//                                tint = if (currentRoute == "chat") Color.White else Color.Gray
//                            )
//                        },
//                        label = { Text(text = "", fontSize = 0.sp) },
//                        selected = currentRoute == "chat",
//                        onClick = { navController?.navigate("chat") }
//                    )
//
//                    NavigationBarItem(
//                        icon = {
//                            Icon(
//                                painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
//                                contentDescription = "Search",
//                                tint = if (currentRoute == "search") Color.White else Color.Gray
//                            )
//                        },
//                        label = { Text(text = "", fontSize = 0.sp) },
//                        selected = currentRoute == "search",
//                        onClick = { navController?.navigate("search") }
//                    )
//
//                    NavigationBarItem(
//                        icon = {
//                            Icon(
//                                painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
//                                contentDescription = "Profile",
//                                tint = if (currentRoute == "profile") Color.White else Color.Gray
//                            )
//                        },
//                        label = { Text(text = "", fontSize = 0.sp) },
//                        selected = currentRoute == "profile",
//                        onClick = { /* Already on profile screen */ }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(Color.White)
//                .verticalScroll(rememberScrollState())
//        ) {
//            when {
//                uiState.isLoading -> LoadingState()
//                uiState.user != null -> ProfileContent(
//                    uiState = uiState,
//                    onEditProfileClick = { navController?.navigate("edit_profile") },
//                    // FIXED: Remove the user parameter
//                    onShareProfileClick = { viewModel.shareProfile(context) },
//                    onFollowClick = { viewModel.toggleFollow() },
//                    onMessageClick = {
//                        uiState.user?.let { user ->
//                            navController?.navigate("chat_detail/${user.id}")
//                        }
//                    },
//                    // FIXED: Remove the email parameter
//                    onEmailClick = { viewModel.openEmail(context) },
//                    onAddProfilePhotoClick = { viewModel.pickProfileImage(context) },
//                    onPostClick = { postId -> navController?.navigate("post_detail/$postId") },
//                    onTabSelected = { tab -> viewModel.onTabSelected(tab) }
//                )
//                uiState.error != null -> ErrorState(
//                    error = uiState.error,
//                    onRetry = { viewModel.retry() },
//                    onGoBack = { navController?.popBackStack() }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun ProfileContent(
//    uiState: com.sam.ayaana.domain.model.ProfileUiState,
//    onEditProfileClick: () -> Unit,
//    onShareProfileClick: () -> Unit,
//    onFollowClick: () -> Unit,
//    onMessageClick: () -> Unit,
//    onEmailClick: () -> Unit,
//    onAddProfilePhotoClick: () -> Unit,
//    onPostClick: (String) -> Unit,
//    onTabSelected: (ProfileTab) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(modifier = modifier) {
//        uiState.user?.let { user ->
//            ProfileHeaderSection(
//                user = user,
//                onEditProfileClick = onEditProfileClick,
//                onShareProfileClick = onShareProfileClick,
//                onFollowClick = onFollowClick,
//                onMessageClick = onMessageClick,
//                onEmailClick = onEmailClick,
//                onAddProfilePhotoClick = onAddProfilePhotoClick
//            )
//
//            ProfilePostsGrid(
//                user = user,
//                posts = uiState.posts,
//                taggedPosts = uiState.taggedPosts,
//                reels = uiState.reels,
//                onPostClick = onPostClick,
//                onTabSelected = onTabSelected
//            )
//        }
//    }
//}
//
//@Composable
//private fun LoadingState(modifier: Modifier = Modifier) {
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator(color = Color(0xFF0095F6))
//    }
//}
//
//@Composable
//private fun ErrorState(
//    error: String?,
//    onRetry: () -> Unit,
//    onGoBack: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = error ?: "An error occurred",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = Color.Gray
//            )
//            Button(
//                onClick = onRetry,
//                modifier = Modifier.padding(top = 16.dp),
//                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0095F6))
//            ) {
//                Text("Try Again")
//            }
//            TextButton(onClick = onGoBack, modifier = Modifier.padding(top = 8.dp)) {
//                Text("Go Back")
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun ProfileScreenPreview() {
//    MaterialTheme {
//        ProfileScreen()
//    }
//}
//
