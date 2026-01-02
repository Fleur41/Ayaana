package com.sam.ayaana.presentation.screens.create

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sam.ayaana.Utils.GalleryLauncherResult
import com.sam.ayaana.domain.model.CreateOption
import com.sam.ayaana.Utils.CreateOptions
import com.sam.ayaana.Utils.GalleryPicker
import com.sam.ayaana.Utils.MediaUtils
import com.sam.ayaana.domain.model.CreateType
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavHostController? = null,
    viewModel: CreatePostViewModel = hiltViewModel(),

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
        maxSelection = 20,
        // maxSelection = GalleryPicker.MAX_SELECTION_POST,
        isVideoOnly = false
    )

    val storyGalleryLauncherResult = GalleryPicker.rememberGalleryLauncher(
        onMediaSelected = { uris ->
            viewModel.selectMedia(uris)
        },
        maxSelection = 20,
        // maxSelection = GalleryPicker.MAX_SELECTION_STORY,
        isVideoOnly = false
    )

    val reelGalleryLauncherResult = GalleryPicker.rememberGalleryLauncher(
        onMediaSelected = { uris ->
            viewModel.selectMedia(uris)
        },
        maxSelection = 10,
        // maxSelection = GalleryPicker.MAX_SELECTION_REEL,
        isVideoOnly = false
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
                                            type = CreateType.POST
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
                                "Live" -> startLiveStream(navController)
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

private fun startLiveStream(navController: NavHostController?) {
    // Start live streaming - this would open camera or streaming service
    navController?.navigate("live_stream")
}

@Composable
fun SelectedMediaPreview(
    mediaUris: List<Uri>,
    onRemoveMedia: (Uri) -> Unit
) {
    val context = LocalContext.current

    // CHANGED: Check if any of the media is video
    val hasVideos = mediaUris.any { uri ->
        MediaUtils.isVideoUri(context.contentResolver, uri)
    }

    if (mediaUris.isNotEmpty()) {
        Column {
            // CHANGED: Increased height from 120.dp to 200.dp for better video visibility
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // CHANGED: Increased to 200.dp
                horizontalArrangement = Arrangement.spacedBy(12.dp) // CHANGED: Increased spacing
            ) {
                items(mediaUris) { uri ->
                    MediaThumbnail(
                        uri = uri,
                        onRemove = { onRemoveMedia(uri) },
                        size = 180.dp // CHANGED: Pass larger size
                    )
                }
            }

            // CHANGED: Add warning if videos are mixed with images
            if (hasVideos && mediaUris.size > 1) {
                val videoCount = mediaUris.count { uri ->
                    MediaUtils.isVideoUri(context.contentResolver, uri)
                }
                val imageCount = mediaUris.size - videoCount

                if (videoCount > 0 && imageCount > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$videoCount video${if (videoCount > 1) "s" else ""} and $imageCount image${if (imageCount > 1) "s" else ""} selected",
                            color = Color(0xFFFF9800),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaThumbnail(
    uri: Uri,
    onRemove: () -> Unit,
    size: Dp = 180.dp // CHANGED: Default size parameter
) {
    val context = LocalContext.current
    val isVideo = remember(uri) {
        MediaUtils.isVideoUri(context.contentResolver, uri)
    }

    Box(
        modifier = Modifier
            .size(size) // CHANGED: Use parameter
    ) {
        if (isVideo) {
            VideoThumbnail(uri = uri)
        } else {
            // For images, use AsyncImage
            AsyncImage(
                model = uri,
                contentDescription = "Selected media",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )
        }

        // Remove button
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(28.dp)
                .background(Color.Black.copy(alpha = 0.7f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }

        // Video play icon overlay - CHANGED: Bigger and centered
        if (isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play video",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun VideoThumbnail(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var thumbnailBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(uri) {
        // Load video thumbnail in background
        thumbnailBitmap = withContext(kotlinx.coroutines.Dispatchers.IO) {
            MediaUtils.getVideoThumbnail(context, uri)
        }
        isLoading = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .background(Color.LightGray), // Fallback background
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
            thumbnailBitmap != null -> {
                // Display the actual video thumbnail
                androidx.compose.foundation.Image(
                    bitmap = thumbnailBitmap!!.asImageBitmap(),
                    contentDescription = "Video thumbnail",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                // Fallback if thumbnail couldn't be loaded
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Video",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
            }
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

