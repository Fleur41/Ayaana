package com.sam.ayaana.presentation.screens.liveviewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sam.ayaana.presentation.screens.livestream.LiveViewerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveViewerScreen(
    navController: NavHostController,
    streamId: String? = null,
    viewModel: LiveViewerViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    // Viewer state
    val viewerState by viewModel.viewerState.collectAsState()

    // Load stream when screen opens
    LaunchedEffect(streamId) {
        streamId?.let { id ->
            viewModel.loadLiveStream(id)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Live",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
        ) {
            // Video Player Area
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (viewerState.isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else if (viewerState.stream != null) {
                    // Mock video player
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Live Stream",
                                tint = Color.White,
                                modifier = Modifier.size(64.dp)
                            )
                            Text(
                                text = viewerState.stream?.title ?: "Live Stream",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${viewerState.viewerCount} viewers",
                                color = Color.Red,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = viewerState.error ?: "Stream not available",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                // Live indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(Color.Red, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Stream info overlay (bottom left)
            StreamInfoOverlay(
                stream = viewerState.stream,
                viewerCount = viewerState.viewerCount,
                isFollowing = viewerState.isFollowing,
                onFollow = { viewModel.followStreamer() },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )

            // Viewer controls overlay (right side)
            ViewerControlsOverlay(
                likes = viewerState.likes,
                onLike = { viewModel.sendLike() },
                onComment = { viewModel.toggleComments() },
                onShare = { /* Share */ },
                onGift = { /* Send gift */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )

            // Comments overlay
            if (viewerState.showComments) {
                CommentsOverlay(
                    comments = viewerState.comments,
                    onSendComment = { comment ->
                        viewModel.sendComment(comment)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 100.dp)
                        .width(250.dp)
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
fun StreamInfoOverlay(
    stream: com.sam.ayaana.domain.model.LiveStream?,
    viewerCount: Int,
    isFollowing: Boolean,
    onFollow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Streamer info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Profile image
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray, CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stream?.hostUsername ?: "Streamer",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "$viewerCount viewers",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            // Follow button
            Button(
                onClick = onFollow,
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFollowing) Color.Gray else Color(0xFF0095F6)
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (isFollowing) "Following" else "Follow",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Stream title
        stream?.title?.let { title ->
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ViewerControlsOverlay(
    likes: Int,
    onLike: () -> Unit,
    onComment: () -> Unit,
    onShare: () -> Unit,
    onGift: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile button
        IconButton(
            onClick = { /* Go to profile */ },
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Like button with count
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = onLike,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = formatCount(likes),
                color = Color.White,
                fontSize = 10.sp
            )
        }

        // Comment button
        IconButton(
            onClick = onComment,
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubbleOutline,
                contentDescription = "Comments",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Share button
        IconButton(
            onClick = onShare,
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        // Gift button
        IconButton(
            onClick = onGift,
            modifier = Modifier
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.CardGiftcard,
                contentDescription = "Send Gift",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CommentsOverlay(
    comments: List<com.sam.ayaana.domain.model.LiveComment>,
    onSendComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        // Comments header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Live Comments",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${comments.size}",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )
        }

        // Comments list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(comments.reversed()) { comment ->
                Text(
                    text = "${comment.username}: ${comment.text}",
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        // Comment input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Add a comment...", color = Color.White.copy(alpha = 0.7f)) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                singleLine = true
            )

            IconButton(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onSendComment(commentText)
                        commentText = ""
                    }
                },
                enabled = commentText.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send comment",
                    tint = if (commentText.isNotBlank()) Color.White else Color.White.copy(alpha = 0.3f)
                )
            }
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000 -> "${count / 1000}K"
        else -> count.toString()
    }
}