package com.sam.ayaana.presentation.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.sam.ayaana.R
import com.sam.ayaana.Utils.CreateOptions
import com.sam.ayaana.Utils.GalleryPicker
import com.sam.ayaana.domain.model.CreateOption
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

    // CHANGED: Simple state for popup visibility
    var showCreateBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userId) {
        viewModel.loadProfile(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val isCurrentUser = userId == null

    val galleryLauncher = GalleryPicker.rememberGalleryLauncher(
        onMediaSelected = { uris ->
            uris.firstOrNull()?.let { uri ->
                viewModel.updateProfileImage(uri)
            }
        },
        maxSelection = 1,
        isVideoOnly = false
    )

    var shouldOpenGalleryAfterPermission by remember { mutableStateOf(false) }

    // Handle opening gallery after permission is granted
    LaunchedEffect(galleryLauncher.permissionState.status) {
        if (galleryLauncher.permissionState.status.isGranted && shouldOpenGalleryAfterPermission) {
            galleryLauncher.launchGallery()
        }
    }

    // Remember the current user to update bottom nav
    val currentUser = remember { mutableStateOf(uiState.user) }

    LaunchedEffect(uiState.user) {
        currentUser.value = uiState.user
    }

    // CHANGED: Use Dialog with custom animation for popup window
    if (showCreateBottomSheet) {
        Dialog(
            onDismissRequest = { showCreateBottomSheet = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            // Custom popup with slide up/down animation
            AnimatedVisibility(
                visible = showCreateBottomSheet,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight }, // Starts from bottom
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight }, // Exits to bottom
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                CreatePopupWindow(
                    onDismiss = { showCreateBottomSheet = false },
                    navController = navController
                )
            }
        }
    }

    Scaffold(
        topBar = {
            if (isCurrentUser){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.user?.username ?: "Profile",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically

                    ){
                        // CHANGED: Add icon - shows custom popup window
                        IconButton(
                            onClick = {
                                showCreateBottomSheet = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(35.dp)
                            )
                        }

                        // Menu icon
                        IconButton(
                            onClick = {
                                navController?.navigate("menu_screen")
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_menu),
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(30.dp)

                            )
                        }
                    }
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                        when {
                            galleryLauncher.permissionState.status.isGranted -> {
                                galleryLauncher.launchGallery()
                            }
                            else -> {
                                shouldOpenGalleryAfterPermission = true
                                galleryLauncher.permissionState.launchPermissionRequest()
                            }
                        }
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

// CHANGED: CreatePopupWindow component for half-screen popup
@Composable
fun CreatePopupWindow(
    onDismiss: () -> Unit,
    navController: NavHostController?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(top = 32.dp),
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = MaterialTheme.shapes.extraLarge.topStart,
            bottomEnd = MaterialTheme.shapes.extraLarge.topEnd
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
            // containerColor = Color(0xFFF0F8FF)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header with title and close button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Create",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF262626)
                    )
                )

                // Close button at the end
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                        // tint = Color(0xFF262626)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FIXED: Create options list with proper iteration
            val options = CreateOptions.bottomSheetOptions
            options.forEachIndexed { index, option ->
                CreateOptionRow(
                    option = option,
                    onClick = {
                        onDismiss()
                        navController?.navigate(option.route)
                    }
                )

                // Add divider between items (except after last)
                if (index < options.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        thickness = 0.5.dp,
                        color = Color(0xFFDBDBDB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

// CHANGED: CreateOptionRow component (updated from CreateBottomSheetContent)
@Composable
fun CreateOptionRow(
    option: CreateOption,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = option.title,
            modifier = Modifier.size(28.dp),
            tint = Color(0xFF262626)
            // tint = MaterialTheme.colorScheme.onSurface
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = option.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF262626)
                )
            )
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF8E8E8E)
                )
            )
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

            ProfilePostsGrid(
                user = user,
                posts = uiState.posts,
                taggedPosts = uiState.taggedPosts,
                reels = uiState.reels,
                onPostClick = onPostClick,
                onTabSelected = onTabSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
    Log.d("ProfileScreen", "BottomNavBar - User: $user")
    Log.d("ProfileScreen", "BottomNavBar - localProfileUri: ${user?.localProfileUri}")
    Log.d("ProfileScreen", "BottomNavBar - profilePicture: ${user?.profilePicture}")
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

        // Profile (with profile image or default icon)
        NavigationBarItem(
            icon = {
                val imageModel = user?.let {
                    it.localProfileUri ?: it.profilePicture
                }

                if (imageModel != null && (imageModel is Uri || (imageModel is String && imageModel.isNotEmpty()))) {
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
//import android.net.Uri
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Divider
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
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import coil.compose.AsyncImage
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.google.accompanist.permissions.isGranted
//import com.sam.ayaana.R
//import com.sam.ayaana.Utils.CreateOptions
//import com.sam.ayaana.Utils.GalleryPicker
//import com.sam.ayaana.domain.model.CreateOption
//import com.sam.ayaana.domain.model.ProfileTab
//import com.sam.ayaana.domain.model.User
//import com.sam.ayaana.presentation.component.common.CreateBottomSheet
//import com.sam.ayaana.presentation.components.profile.ProfileHeaderSection
//import com.sam.ayaana.presentation.components.profile.ProfilePostsGrid
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
//@Composable
//fun ProfileScreen(
//    navController: NavHostController? = null,
//    userId: String? = null,
//    viewModel: ProfileViewModel = hiltViewModel()
//) {
//    val currentRoute = if (navController != null) {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        navBackStackEntry?.destination?.route
//    } else {
//        "profile"
//    }
//    var showCreateBottomSheet by remember { mutableStateOf(false) }
//    // val sheetState by rememberModalBottomSheetState()
//
//
//    LaunchedEffect(key1 = userId) {
//        viewModel.loadProfile(userId)
//    }
//
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//    val isCurrentUser = userId == null
//    // var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//
//    val galleryLauncher = GalleryPicker.rememberGalleryLauncher(
//        onMediaSelected = { uris ->
//            uris.firstOrNull()?.let { uri ->
//                // Update ViewModel with local Uri (not mock URL)
//                viewModel.updateProfileImage(uri)
//            }
//        },
//        maxSelection = 1,
//        isVideoOnly = false
//    )
//
//
//    var shouldOpenGalleryAfterPermission by remember { mutableStateOf(false) }
//
//    // Handle opening gallery after permission is granted
//    LaunchedEffect(galleryLauncher.permissionState.status) {
//        if (galleryLauncher.permissionState.status.isGranted && shouldOpenGalleryAfterPermission) {
//            galleryLauncher.launchGallery()
//        }
//    }
//
//    // Remember the current user to update bottom nav
//    val currentUser = remember { mutableStateOf(uiState.user) }
//
//    LaunchedEffect(uiState.user) {
//        currentUser.value = uiState.user
//    }
//
//    if (showCreateBottomSheet && navController != null){
//        CreateBottomSheet(
//            onDismiss = { showCreateBottomSheet = false },
//            navController = navController
//        )
//    }
//    Scaffold(
//        topBar = {
////            if (isCurrentUser) {
////                Box(
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .background(MaterialTheme.colorScheme.background)
////                        .padding(16.dp)
////                ) {
////                    Text(
////                        text = uiState.user?.username ?: "Profile",
////                        color = Color.Black,
////                        fontSize = 20.sp,
////                        fontWeight = FontWeight.Bold
////                    )
////                }
//            if (isCurrentUser){
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .background(MaterialTheme.colorScheme.background)
//                        .padding(horizontal = 16.dp, vertical = 12.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = uiState.user?.username ?: "Profile",
//                        color = MaterialTheme.colorScheme.onBackground,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Row(
//                        horizontalArrangement = Arrangement.spacedBy(8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//
//                    ){
//                        // Add icon
//                        IconButton(
//                            onClick = {
//                                showCreateBottomSheet = true
//                            }
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Add,
//                                contentDescription = "Add",
//                                tint = MaterialTheme.colorScheme.onBackground,
//                                modifier = Modifier.size(35.dp)
//                            )
//                        }
//
//
//                        // Menu icon
//                        IconButton(
//                            onClick = {
//                                navController?.navigate("menu_screen")
//                            }
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_menu),
//                                contentDescription = "Menu",
//                                tint = MaterialTheme.colorScheme.onBackground,
//                                modifier = Modifier.size(30.dp)
//
//                            )
//                        }
//                    }
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
//                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
//                ProfileBottomNavBar(
//                    currentRoute = currentRoute,
//                    navController = navController,
//                    user = uiState.user
//                )
//            }
//        }
//    ) { innerPadding ->
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(Color.White)
//        ) {
//            when {
//                uiState.isLoading -> LoadingState()
//                uiState.user != null -> ProfileContent(
//                    uiState = uiState,
//                    onEditProfileClick = { navController?.navigate("edit_profile") },
//                    onShareProfileClick = { viewModel.shareProfile(context) },
//                    onFollowClick = { viewModel.toggleFollow() },
//                    onMessageClick = {
//                        uiState.user?.let { user ->
//                            navController?.navigate("chat_detail/${user.id}")
//                        }
//                    },
//                    onEmailClick = { viewModel.openEmail(context) },
//                    onAddProfilePhotoClick = {
//                        when {
//                            galleryLauncher.permissionState.status.isGranted -> {
//                                // Permission already granted, open gallery directly
//                                galleryLauncher.launchGallery()
//                            }
//                            else -> {
//                                // Request permission and mark that we should open gallery after
//                                shouldOpenGalleryAfterPermission = true
//                                galleryLauncher.permissionState.launchPermissionRequest()
//                            }
//                        }
//                    },
//                    onPostClick = { postId -> navController?.navigate("post_detail/$postId") },
//                    onTabSelected = { tab -> viewModel.onTabSelected(tab) }
//                )
//                uiState.error != null -> ErrorState(
//                    error = uiState.error,
//                    onRetry = { viewModel.retry() },
//                    onGoBack = { navController?.popBackStack() }
//                )
//            }
//
//
//        }
//    }
//}
//
//
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
//    // Use Column without vertical scroll - let the ProfilePostsGrid handle its own scrolling
//    Column(modifier = modifier.fillMaxSize()) {
//        uiState.user?.let { user ->
//            ProfileHeaderSection(
//                user = user,
//                onEditProfileClick = onEditProfileClick,
//                onShareProfileClick = onShareProfileClick,
//                onFollowClick = onFollowClick,
//                onMessageClick = onMessageClick,
//                onEmailClick = onEmailClick,
//                onAddProfilePhotoClick = onAddProfilePhotoClick,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            // This should handle its own scrolling internally
//            ProfilePostsGrid(
//                user = user,
//                posts = uiState.posts,
//                taggedPosts = uiState.taggedPosts,
//                reels = uiState.reels,
//                onPostClick = onPostClick,
//                onTabSelected = onTabSelected,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f) // Takes remaining space
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
//@Composable
//private fun ProfileBottomNavBar(
//    currentRoute: String?,
//    navController: NavHostController?,
//    user: User?
//) {
//    Log.d("ProfileScreen", "BottomNavBar - User: $user")
//    Log.d("ProfileScreen", "BottomNavBar - localProfileUri: ${user?.localProfileUri}")
//    Log.d("ProfileScreen", "BottomNavBar - profilePicture: ${user?.profilePicture}")
//    NavigationBar(
//        containerColor = Color.Black,
//        contentColor = Color.White
//    ) {
//        // Home
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
//                    contentDescription = "Home",
//                    tint = if (currentRoute == "home") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "home",
//            onClick = { navController?.navigate("home") }
//        )
//
//
//
//        // Reels
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
//                    contentDescription = "Reels",
//                    tint = if (currentRoute == "reels") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "reels",
//            onClick = { navController?.navigate("reels") }
//        )
//
//        // Chat
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
//                    contentDescription = "Chat",
//                    tint = if (currentRoute == "chat") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "chat",
//            onClick = { navController?.navigate("chat") }
//        )
//
//        // Search
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
//                    contentDescription = "Search",
//                    tint = if (currentRoute == "search") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "search",
//            onClick = { navController?.navigate("search") }
//        )
//
//        // Profile (with profile image or default icon)
//        NavigationBarItem(
//            icon = {
//                // Check for local Uri first, then remote URL
//                val imageModel = user?.let {
//                    it.localProfileUri ?: it.profilePicture
//                }
//
//                if (imageModel != null && (imageModel is Uri || (imageModel is String && imageModel.isNotEmpty()))) {
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
//                            model = imageModel,
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
////        NavigationBarItem(
////            icon = {
////                if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
////                    // Show profile image
////                    Box(
////                        modifier = Modifier
////                            .size(24.dp)
////                            .clip(CircleShape)
////                            .border(
////                                1.dp,
////                                if (currentRoute == "profile") Color.White else Color.Transparent,
////                                CircleShape
////                            )
////                    ) {
////                        AsyncImage(
////                            model = profileImageUrl,
////                            contentDescription = "Profile",
////                            modifier = Modifier
////                                .fillMaxSize()
////                                .clip(CircleShape),
////                            contentScale = ContentScale.Crop
////                        )
////                    }
////                } else {
////                    // Show default profile icon
////                    Icon(
////                        painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
////                        contentDescription = "Profile",
////                        tint = if (currentRoute == "profile") Color.White else Color.Gray
////                    )
////                }
////            },
////            label = { Text(text = "", fontSize = 0.sp) },
////            selected = currentRoute == "profile",
////            onClick = { /* Already on profile screen */ }
////        )
////    }
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
