package com.sam.ayaana.presentation.screens.livestream

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sam.ayaana.Utils.RTMPStreamer
import com.sam.ayaana.domain.model.LiveComment
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveStreamScreen(
    navController: NavHostController,
    viewModel: LiveStreamViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // Live stream state
    val liveState by viewModel.liveStreamState.collectAsState()

    // RTMP Streamer instance from ViewModel (if available)
    val rtmpStreamer = remember { viewModel.getRTMPStreamer() }

    // Camera permissions
    val cameraPermissionState = remember { mutableStateOf(false) }
    val audioPermissionState = remember { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        cameraPermissionState.value = permissions[Manifest.permission.CAMERA] == true
        audioPermissionState.value = permissions[Manifest.permission.RECORD_AUDIO] == true
    }

    // Check permissions when screen loads
    LaunchedEffect(Unit) {
        val requiredPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )

        val hasPermissions = requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermissions) {
            permissionsLauncher.launch(requiredPermissions)
        } else {
            cameraPermissionState.value = true
            audioPermissionState.value = true
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (liveState.isLive) "Live Now" else "Go Live",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (liveState.isLive) {
                                // Show confirmation dialog before ending
                                viewModel.showEndLiveConfirmation()
                            } else {
                                navController.navigateUp()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                actions = {
                    if (liveState.isLive) {
                        // Live indicator with viewer count
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Red dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                            Text(
                                text = "LIVE • ${liveState.viewerCount} viewers",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
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
            // Camera Preview (only show if we have permissions)
            if (cameraPermissionState.value && audioPermissionState.value) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    rtmpStreamer = if (liveState.isLive || liveState.isLoading) rtmpStreamer else null,
                    onCameraReady = { previewView, cameraProvider ->
                        // Setup camera here (only if not using RTMP streamer)
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)

                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )
            } else {
                // Show permission request message
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Camera and microphone permissions required for live streaming",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Before going live - show setup options
            if (!liveState.isLive && !liveState.isLoading) {
                LiveSetupOptions(
                    title = liveState.title,
                    isPrivate = liveState.isPrivate,
                    onTitleChange = { viewModel.updateTitle(it) },
                    onPrivacyToggle = { viewModel.togglePrivacy() },
                    onStartLive = { viewModel.startLiveStream(context) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(16.dp)
                )
            }

            // Live Controls (when live or loading)
            if (liveState.isLive || liveState.isLoading) {
                LiveControls(
                    isLive = liveState.isLive,
                    isLoading = liveState.isLoading,
                    viewerCount = liveState.viewerCount,
                    onStartLive = { viewModel.startLiveStream(context) },
                    onEndLive = { viewModel.showEndLiveConfirmation() },
                    onSwitchCamera = {
                        if (liveState.isLive) {
                            // Use RTMP streamer to switch camera
                            rtmpStreamer?.switchCamera()
                        } else {
                            // TODO: Implement camera switch for preview mode
                        }
                    },
                    onToggleFlash = {
                        if (liveState.isLive) {
                            // Use RTMP streamer to toggle flash
                            rtmpStreamer?.toggleFlash()
                        } else {
                            // TODO: Implement flash toggle for preview mode
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // Live Comments Overlay
            if (liveState.isLive) {
                LiveCommentsOverlay(
                    comments = liveState.comments,
                    onSendComment = { comment ->
                        viewModel.sendLiveComment(comment)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 100.dp)
                        .width(250.dp)
                        .height(200.dp)
                )
            }

            // Loading overlay
            if (liveState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // Error overlay
            liveState.error?.let { error ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Red.copy(alpha = 0.8f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 2,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearError() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close error",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // End Live Confirmation Dialog
        if (liveState.showEndConfirmation) {
            AlertDialog(
                onDismissRequest = { viewModel.hideEndLiveConfirmation() },
                title = { Text("End Live Stream?") },
                text = { Text("Are you sure you want to end your live stream?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.endLiveStream()
                                navController.navigateUp()
                            }
                        }
                    ) {
                        Text("End Live", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.hideEndLiveConfirmation() }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    isLive: Boolean = false,
    rtmpStreamer: RTMPStreamer? = null,
    onCameraReady: (PreviewView, ProcessCameraProvider) -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier,
        update = { previewView ->
            if (isLive && rtmpStreamer != null) {
                // When live, the RTMP streamer handles the camera
                // We don't need to do anything here as RTMP handles its own preview
                // The RTMP library's startStream() method starts its own preview
            } else {
                // Use CameraX for preview mode (before going live)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    onCameraReady(previewView, cameraProvider)
                }, ContextCompat.getMainExecutor(context))
            }
        }
    )
}
//@Composable
//fun CameraPreview(
//    modifier: Modifier = Modifier,
//    rtmpStreamer: RTMPStreamer? = null,
//    onCameraReady: (PreviewView, ProcessCameraProvider) -> Unit
//) {
//    val context = LocalContext.current
//    val lifecycleOwner = LocalLifecycleOwner.current
//
//    AndroidView(
//        factory = { ctx ->
//            PreviewView(ctx).apply {
//                scaleType = PreviewView.ScaleType.FILL_CENTER
//
//                // If we have RTMP streamer (when streaming is active), use its camera preview
//                rtmpStreamer?.getRtmpCamera()?.let { rtmpCamera ->
//                    try {
//                        // Start preview on RTMP camera
//                        rtmpCamera.startPreview()
//                        // Attach the GLInterface view for preview
//                        rtmpCamera.glInterface.attachView(this)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//        },
//        modifier = modifier,
//        update = { previewView ->
//            // Only use CameraX if we don't have an RTMP streamer (preview mode)
//            if (rtmpStreamer == null) {
//                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
//                cameraProviderFuture.addListener({
//                    val cameraProvider = cameraProviderFuture.get()
//                    onCameraReady(previewView, cameraProvider)
//                }, ContextCompat.getMainExecutor(context))
//            }
//        }
//    )
//}

@Composable
fun LiveSetupOptions(
    title: String,
    isPrivate: Boolean,
    onTitleChange: (String) -> Unit,
    onPrivacyToggle: () -> Unit,
    onStartLive: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title input
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Add a title (optional)", color = Color.White.copy(alpha = 0.7f)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                unfocusedIndicatorColor = Color.White.copy(alpha = 0.3f)
            ),
            singleLine = true
        )

        // Privacy toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Private Stream",
                color = Color.White,
                fontSize = 14.sp
            )
            Switch(
                checked = isPrivate,
                onCheckedChange = { onPrivacyToggle() }
            )
        }

        // Start Live button
        Button(
            onClick = onStartLive,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00D100)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Start Live Stream",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun LiveControls(
    isLive: Boolean,
    isLoading: Boolean,
    viewerCount: Int,
    onStartLive: () -> Unit,
    onEndLive: () -> Unit,
    onSwitchCamera: () -> Unit,
    onToggleFlash: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top controls row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Camera controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera switch button
                IconButton(
                    onClick = onSwitchCamera,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Flash toggle button
                IconButton(
                    onClick = onToggleFlash,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Toggle Flash",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Right side - Viewer count
            if (isLive) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = "Viewers",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${viewerCount}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Bottom center - Start/Stop button
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(64.dp)
                    )
                }
                isLive -> {
                    // Red Stop button when live
                    IconButton(
                        onClick = onEndLive,
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color.Red, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop Live",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                else -> {
                    // Green Start button when not live
                    IconButton(
                        onClick = onStartLive,
                        modifier = Modifier
                            .size(70.dp)
                            .background(Color(0xFF00D100), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Start Live",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveCommentsOverlay(
    comments: List<LiveComment>,
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

