// presentation/screens/reels/ReelDetailScreen.kt
package com.sam.ayaana.presentation.screens.reels

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sam.ayaana.Utils.DownloadState
import com.sam.ayaana.domain.model.Reel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReelDetailScreen(
    navController: NavHostController,
    viewModel: ReelDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val context = LocalContext.current
    var showMoreOptions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadReel()
    }

    // FIXED: Handle download state changes
    LaunchedEffect(downloadState) {
        when (downloadState) {
            is DownloadState.Success -> {
                // Show success toast
                Toast.makeText(
                    context,
                    "✅ Reel thumbnail saved to gallery!",
                    Toast.LENGTH_SHORT
                ).show()

                // Reset state after delay
                delay(2000)
                // Note: You might want to add a reset function in ViewModel
            }
            is DownloadState.Failed -> {
                Toast.makeText(
                    context,
                    "❌ ${(downloadState as DownloadState.Failed).message}",
                    Toast.LENGTH_SHORT
                ).show()
                delay(2000)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.reel?.username ?: "Reel",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // 1. SHARE BUTTON
                    IconButton(
                        onClick = {
                            uiState.reel?.let { reel ->
                                shareReel(context, reel)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White
                        )
                    }

                    // 2. MORE OPTIONS DROPDOWN
                    Box {
                        IconButton(onClick = { showMoreOptions = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreOptions,
                            onDismissRequest = { showMoreOptions = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (downloadState is DownloadState.Downloading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = Color.Blue
                                            )
                                        } else {
                                            Icon(
                                                Icons.Default.Download,
                                                contentDescription = "Download",
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text = when (downloadState) {
                                                is DownloadState.Downloading -> "Downloading..."
                                                is DownloadState.Success -> "Downloaded"
                                                else -> "Download"
                                            },
                                            modifier = Modifier.padding(start = 8.dp),
                                            color = Color.Black
                                        )
                                    }
                                },
                                onClick = {
                                    uiState.reel?.let { reel ->
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                            viewModel.downloadReelThumbnail(context, reel)
                                            showMoreOptions = false
                                        } else {
                                            android.widget.Toast.makeText(
                                                context,
                                                "📱 Requires Android 10+ for download",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                            showMoreOptions = false
                                        }
                                    }
                                },
                                enabled = downloadState !is DownloadState.Downloading &&
                                        downloadState !is DownloadState.Success &&
                                        uiState.reel != null
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f)
                )
            )
        }
    ) { innerPadding ->
        Box(
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
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error loading reel",
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                uiState.reel != null -> {
                    ReelDetailContent(
                        reel = uiState.reel!!,
                        onLikeClick = { viewModel.likeReel() }
                    )
                }
            }
        }
    }
}

// Share function - Opens Android's native share sheet
private fun shareReel(context: Context, reel: Reel) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, reel.title)
        putExtra(Intent.EXTRA_TEXT, "Check out this reel on Ayaana: ${reel.title}\n${reel.description}\n\n#Ayaana #InstagramClone")
    }

    val chooserIntent = Intent.createChooser(shareIntent, "Share via")
    chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooserIntent)
}

@Composable
fun ReelDetailContent(
    reel: Reel,
    onLikeClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Video/Image content
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
                .size(80.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        // Bottom content overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
                .padding(20.dp)
        ) {
            // Title
            Text(
                text = reel.title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // Description
            Text(
                text = reel.description,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
            ) {
                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (reel.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (reel.isLiked) Color.Red else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = reelFormatCount(reel.likes),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Comments
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💬",
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = reelFormatCount(reel.comments),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Shares
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        // Share action
                        // You could add share functionality here too
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Shares",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = reelFormatCount(reel.shares),
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

// Helper function for formatting counts
private fun reelFormatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

