package com.sam.ayaana.presentation.screens.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sam.ayaana.Utils.GalleryLauncherResult
import com.sam.ayaana.domain.model.CreateOption
import com.sam.ayaana.Utils.CreateOptions
import com.sam.ayaana.Utils.GalleryPicker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavHostController? = null,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // State collection
    val uiState by viewModel.uiState.collectAsState()
    val selectedMedia by viewModel.selectedMedia.collectAsState()
    val uploadProgress by viewModel.uploadProgress.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Gallery launchers for different post types
    val postGalleryLauncherResult = GalleryPicker.rememberGalleryLauncher(
        onMediaSelected = { uris ->
            viewModel.selectMedia(uris)
        },
        maxSelection = GalleryPicker.MAX_SELECTION_POST,
        isVideoOnly = false
    )

    val storyGalleryLauncherResult = GalleryPicker.rememberGalleryLauncher(
        onMediaSelected = { uris ->
            viewModel.selectMedia(uris)
        },
        maxSelection = GalleryPicker.MAX_SELECTION_STORY,
        isVideoOnly = false
    )

    val reelGalleryLauncherResult = GalleryPicker.rememberGalleryLauncher(
        onMediaSelected = { uris ->
            viewModel.selectMedia(uris)
        },
        maxSelection = GalleryPicker.MAX_SELECTION_REEL,
        isVideoOnly = true
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Create",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController?.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    // Show upload progress or action button
                    when (uiState) {
                        is CreatePostUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        is CreatePostUiState.MediaSelected -> {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.createPost(
                                            context = context,
                                            mediaUris = selectedMedia,
                                            caption = "Sample caption",
                                            type = com.sam.ayaana.domain.model.CreateType.POST
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Next"
                                )
                            }
                        }
                        else -> {
                            // Empty actions for other states
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Show selected media preview
            if (selectedMedia.isNotEmpty()) {
                SelectedMediaPreview(
                    mediaUris = selectedMedia,
                    onRemoveMedia = { uri ->
                        // Remove media from selection
                        val updatedMedia = selectedMedia.toMutableList().apply {
                            remove(uri)
                        }
                        viewModel.selectMedia(updatedMedia)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Show upload progress if loading
            if (uiState is CreatePostUiState.Loading) {
                val progress = (uiState as CreatePostUiState.Loading).progress
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Uploading... ${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Create Options
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(CreateOptions.defaultOptions) { option ->
                    CreateOptionCard(
                        option = option,
                        onClick = {
                            // This will trigger gallery/camera picker based on the option
                            when (option.title) {
                                "Post" -> openGalleryForPost(postGalleryLauncherResult)
                                "Story" -> openGalleryForStory(storyGalleryLauncherResult)
                                "Reel" -> openGalleryForReel(reelGalleryLauncherResult)
                                "Live" -> startLiveStream()
                            }
                        }
                    )
                }
            }

            // Recent Gallery Items (Placeholder)
            Text(
                text = "Recent",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            // Gallery grid would go here
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when {
                    selectedMedia.isNotEmpty() -> {
                        Text(
                            text = "${selectedMedia.size} media selected",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    uiState is CreatePostUiState.Loading -> {
                        Text(
                            text = "Upload in progress...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                    else -> {
                        Text(
                            text = "Select media to create a post",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// Implemented gallery functions
private fun openGalleryForPost(galleryLauncherResult: GalleryLauncherResult) {
    galleryLauncherResult.launchGallery()
}

private fun openGalleryForStory(galleryLauncherResult: GalleryLauncherResult) {
    galleryLauncherResult.launchGallery()
}

private fun openGalleryForReel(galleryLauncherResult: GalleryLauncherResult) {
    galleryLauncherResult.launchGallery()
}

private fun startLiveStream() {
    // Start live streaming - this would open camera or streaming service
    // For now, we'll show a message or navigate to live stream screen
}

@Composable
fun SelectedMediaPreview(
    mediaUris: List<android.net.Uri>,
    onRemoveMedia: (android.net.Uri) -> Unit
) {
    // Enhanced preview with actual media thumbnails
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(mediaUris) { uri ->
            MediaThumbnail(uri = uri, onRemove = { onRemoveMedia(uri) })
        }
    }
}

@Composable
fun MediaThumbnail(uri: android.net.Uri, onRemove: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
    ) {
        // Using Coil for image loading
        AsyncImage(
            model = uri,
            contentDescription = "Selected media",
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CreateOptionCard(
    option: CreateOption,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = option.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = option.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 2
            )
        }
    }
}

//package com.sam.ayaana.presentation.screens.create
//
//import android.util.Log
//import android.widget.TextView
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowForward
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import coil.compose.AsyncImage
//import com.sam.ayaana.Utils.GalleryLauncherResult
//import com.sam.ayaana.domain.model.CreateOption
//import com.sam.ayaana.Utils.CreateOptions
//import com.sam.ayaana.Utils.GalleryPicker
//import kotlinx.coroutines.launch
//
////@OptIn(ExperimentalMaterial3Api::class)
////@Composable
////fun CreatePostScreen(
////    navController: NavHostController? = null
////    // REMOVE: viewModel: CreatePostViewModel = hiltViewModel()
////) {
////    LaunchedEffect(Unit) {
////        Log.e("CREATE_POST_TEST", "🎉 CreatePostScreen LOADED!")
////    }
////
////    Scaffold(
////        topBar = {
////            CenterAlignedTopAppBar(
////                title = { Text("Create Post") },
////                navigationIcon = {
////                    IconButton(onClick = { navController?.popBackStack() }) {
////                        Icon(Icons.Default.Close, "Close")
////                    }
////                }
////            )
////        }
////    ) { innerPadding ->
////        Box(
////            modifier = Modifier
////                .fillMaxSize()
////                .padding(innerPadding)
////                .background(Color.White),
////            contentAlignment = Alignment.Center
////        ) {
////            Text("CREATE POST SCREEN - IT WORKED! 🎉", color = Color.Black)
////        }
////    }
////}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CreatePostScreen(
//    navController: NavHostController? = null,
//    viewModel: CreatePostViewModel = hiltViewModel()
//) {
//    LaunchedEffect(Unit) {
//        Log.e("CREATE_POST_DEBUG", "🎉 STEP 1: CreatePostScreen LaunchedEffect executed!")
//    }
//
//    Log.d("CREATE_POST_DEBUG", "🎉 STEP 2: CreatePostScreen Composable function called!")
////    AndroidView(factory = { context ->
////        TextView(context).apply {
////            text = "CreatePostScreen Debug"
////            setBackgroundColor(android.graphics.Color.TRANSPARENT)
////            Log.e("CREATE_POST", "🎉 CreatePostScreen LOADED!")
////        }
////    })
//    val context = LocalContext.current
//    val uiState by viewModel.uiState.collectAsState()
//    val selectedMedia by viewModel.selectedMedia.collectAsState()
//    val uploadProgress by viewModel.uploadProgress.collectAsState()
//    val coroutineScope = rememberCoroutineScope()
//
//    // Gallery launchers for different post types
//    val postGalleryLauncher = GalleryPicker.rememberGalleryLauncher(
//        onMediaSelected = { uris ->
//            viewModel.selectMedia(uris)
//        },
//        maxSelection = GalleryPicker.MAX_SELECTION_POST,
//        isVideoOnly = false
//    )
//
//    val storyGalleryLauncher = GalleryPicker.rememberGalleryLauncher(
//        onMediaSelected = { uris ->
//            viewModel.selectMedia(uris)
//        },
//        maxSelection = GalleryPicker.MAX_SELECTION_STORY,
//        isVideoOnly = false
//    )
//
//    val reelGalleryLauncher = GalleryPicker.rememberGalleryLauncher(
//        onMediaSelected = { uris ->
//            viewModel.selectMedia(uris)
//        },
//        maxSelection = GalleryPicker.MAX_SELECTION_REEL,
//        isVideoOnly = true
//    )
//
//    LaunchedEffect(Unit) {
//        Log.e("CREATE_POST_TEST", "🎉 CreatePostScreen LOADED!")
//    }
//    // Handle UI state changes
////    LaunchedEffect(uiState) {
////        Log.d("NAVIGATION_DEBUG", "🎉 CreatePostScreen LOADED - Navigation SUCCESS!")
////        when (uiState) {
////            is CreatePostUiState.Success -> {
////                // Navigate back to home screen after successful upload
////                navController?.popBackStack()
////            }
////            is CreatePostUiState.Failed -> {
////                // Show error message (we'll implement a snackbar later)
////                val errorMessage = (uiState as CreatePostUiState.Failed).message
////                // You can show a snackbar here: scaffoldState.snackbarHostState.showSnackbar(errorMessage)
////            }
////            else -> {
////                // Handle other states if needed
////            }
////        }
////    }
//
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = {
//                    Text(
//                        text = "Create",
//                        style = MaterialTheme.typography.headlineSmall
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = { navController?.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.Close,
//                            contentDescription = "Close"
//                        )
//                    }
//                },
//                actions = {
//                    // Show upload progress or action button
//                    when (uiState) {
//                        is CreatePostUiState.Loading -> {
//                            CircularProgressIndicator(
//                                modifier = Modifier.size(24.dp),
//                                strokeWidth = 2.dp
//                            )
//                        }
//                        is CreatePostUiState.MediaSelected -> {
//                            IconButton(
//                                onClick = {
//                                    coroutineScope.launch {
//                                        viewModel.createPost(
//                                            context = context,
//                                            mediaUris = selectedMedia,
//                                            caption = "Sample caption", // This would come from user input
//                                            type = com.sam.ayaana.domain.model.CreateType.POST
//                                        )
//                                    }
//                                }
//                            ) {
//                                Icon(
//                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                                    contentDescription = "Next"
//                                )
//                            }
//                        }
//                        else -> {
//                            // Empty actions for other states
//                        }
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        //Test
////        Box(
////            modifier = Modifier
////                .fillMaxSize()
////                .padding(innerPadding)
////                .background(Color.White),
////            contentAlignment = Alignment.Center
////        ) {
////            Text("CREATE POST SCREEN - IT WORKED! 🎉", color = Color.Black)
////        }
//
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(24.dp)
//        ) {
//            // Show selected media preview
//            if (selectedMedia.isNotEmpty()) {
//                SelectedMediaPreview(
//                    mediaUris = selectedMedia,
//                    onRemoveMedia = { uri ->
//                        // Remove media from selection
//                        val updatedMedia = selectedMedia.toMutableList().apply {
//                            remove(uri)
//                        }
//                        viewModel.selectMedia(updatedMedia)
//                    }
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            // Show upload progress if loading
//            if (uiState is CreatePostUiState.Loading) {
//                val progress = (uiState as CreatePostUiState.Loading).progress
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    verticalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    Text(
//                        text = "Uploading... ${(progress * 100).toInt()}%",
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                    LinearProgressIndicator(
//                        progress = progress,
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            // Create Options
//            LazyRow(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(CreateOptions.defaultOptions) { option ->
//                    CreateOptionCard(
//                        option = option,
//                        onClick = {
//                            // This will trigger gallery/camera picker based on the option
//                            when (option.title) {
//                                "Post" -> openGalleryForPost(postGalleryLauncher)
//                                "Story" -> openGalleryForStory(storyGalleryLauncher)
//                                "Reel" -> openGalleryForReel(reelGalleryLauncher)
//                                "Live" -> startLiveStream()
//                            }
//                        }
//                    )
//                }
//            }
//
//            // Recent Gallery Items (Placeholder)
//            Text(
//                text = "Recent",
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.align(Alignment.Start)
//            )
//
//            // Gallery grid would go here
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                contentAlignment = Alignment.Center
//            ) {
//                when {
//                    selectedMedia.isNotEmpty() -> {
//                        Text(
//                            text = "${selectedMedia.size} media selected",
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
//                    uiState is CreatePostUiState.Loading -> {
//                        Text(
//                            text = "Upload in progress...",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Gray
//                        )
//                    }
//                    else -> {
//                        Text(
//                            text = "Select media to create a post",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = Color.Gray
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Implemented gallery functions
//private fun openGalleryForPost(galleryLauncher: GalleryLauncherResult) {
//    galleryLauncher.launchGallery()
//}
//
//private fun openGalleryForStory(galleryLauncher: GalleryLauncherResult) {
//    galleryLauncher.launchGallery()
//}
//
//private fun openGalleryForReel(galleryLauncher: GalleryLauncherResult) {
//    galleryLauncher.launchGallery()
//}
//
//private fun startLiveStream() {
//    // Start live streaming - this would open camera or streaming service
//    // For now, we'll show a message or navigate to live stream screen
//}
//
//
//@Composable
//fun SelectedMediaPreview(
//    mediaUris: List<android.net.Uri>,
//    onRemoveMedia: (android.net.Uri) -> Unit
//) {
//    // Enhanced preview with actual media thumbnails
//    LazyRow(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(120.dp),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(mediaUris) { uri ->
//            MediaThumbnail(uri = uri, onRemove = { onRemoveMedia(uri) })
//        }
//    }
//}
//
//@Composable
//fun MediaThumbnail(uri: android.net.Uri, onRemove: () -> Unit) {
//    Box(
//        modifier = Modifier
//            .size(100.dp)
//    ) {
//        // Using Coil for image loading
//        AsyncImage(
//            model = uri,
//            contentDescription = "Selected media",
//            modifier = Modifier
//                .size(100.dp)
//                .clip(MaterialTheme.shapes.medium)
//        )
//
//        // Remove button
//        IconButton(
//            onClick = onRemove,
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .size(24.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Close,
//                contentDescription = "Remove",
//                tint = Color.White
//            )
//        }
//    }
//}
//
//@Composable
//fun CreateOptionCard(
//    option: CreateOption,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .width(120.dp)
//            .height(140.dp),
//        onClick = onClick,
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.surfaceVariant
//        )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Icon(
//                imageVector = option.icon,
//                contentDescription = option.title,
//                modifier = Modifier.size(40.dp),
//                tint = MaterialTheme.colorScheme.primary
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = option.title,
//                style = MaterialTheme.typography.bodyMedium,
//                fontWeight = FontWeight.Medium
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = option.description,
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray,
//                maxLines = 2
//            )
//        }
//    }
//}