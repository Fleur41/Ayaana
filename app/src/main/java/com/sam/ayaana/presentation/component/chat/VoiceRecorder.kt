package com.sam.ayaana.presentation.components.chat

import android.Manifest
import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VoiceRecorder(
    onRecordingComplete: (File) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recordAudioPermission = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )

    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var recordingTime by remember { mutableIntStateOf(0) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var recordingAmplitude by remember { mutableFloatStateOf(0f) }

    // Check permissions when component is created
    LaunchedEffect(Unit) {
        if (!recordAudioPermission.status.isGranted) {
            recordAudioPermission.launchPermissionRequest()
        }
    }

    // Visual feedback simulation during recording
    LaunchedEffect(isRecording, isPaused) {
        while (isRecording && !isPaused) {
            recordingAmplitude = Random.nextFloat() * 100f
            delay(100)
        }
    }

    // Timer for recording
    LaunchedEffect(isRecording, isPaused) {
        while (isRecording && !isPaused) {
            delay(1000)
            recordingTime++
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show permission message if not granted
        if (!recordAudioPermission.status.isGranted) {
            Text(
                text = "Microphone permission required for voice recording",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
            return
        }

        // Title
        Text(
            text = when {
                isPaused -> "Recording Paused"
                isRecording -> "Recording..."
                audioFile != null -> "Recording Complete"
                else -> "Voice Message"
            },
            style = MaterialTheme.typography.titleMedium,
            color = when {
                isPaused -> Color(0xFFFF9800)
                isRecording -> Color.Red
                else -> Color.Black
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Recording timer
        Text(
            text = formatTime(recordingTime),
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Visual amplitude indicator during recording
        if (isRecording && !isPaused) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(10) { index ->
                    val barHeight = if (index * 10 < recordingAmplitude) {
                        (10 + (recordingAmplitude / 10)).dp
                    } else {
                        4.dp
                    }

                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(barHeight)
                            .background(
                                color = if (index * 10 < recordingAmplitude) Color.Red else Color.LightGray,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 1.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main control buttons - TRASH → PAUSE → SEND
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TRASH/DELETE button (left side)
            IconButton(
                onClick = {
                    if (audioFile != null) {
                        // Delete recording
                        audioFile?.delete()
                        audioFile = null
                        isRecording = false
                        isPaused = false
                        recordingTime = 0
                        mediaRecorder?.release()
                        mediaRecorder = null
                    } else {
                        // Cancel
                        stopRecording(mediaRecorder)
                        audioFile?.delete()
                        onCancel()
                    }
                },
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (audioFile != null) Color.Red else Color.LightGray,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = if (audioFile != null) "Delete" else "Cancel",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // PAUSE/RESUME button (center) - only show when recording
            if (isRecording) {
                IconButton(
                    onClick = {
                        if (isPaused) {
                            // Resume recording
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                mediaRecorder?.resume()
//                            }
//                            isPaused = false
                            mediaRecorder?.resume()
                            isPaused = false
                        } else {
                            // Pause recording
                            mediaRecorder?.pause()
                            isPaused = true
                        }
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFFF9800), CircleShape),
                    enabled = true
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (isPaused) "Resume" else "Pause",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else {
                // Empty space when not recording for layout balance
                Box(modifier = Modifier.size(80.dp))
            }

            // SEND/RECORD button (right side)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = when {
                            audioFile != null -> Color(0xFF3797F0) // Blue for send
                            isRecording -> Color.Red // Red for recording
                            else -> Color(0xFF3797F0) // Blue for ready to record
                        },
                        shape = CircleShape
                    )
                    .noRippleClickable {
                        when {
                            audioFile != null -> {
                                // Send recording
                                audioFile?.let { onRecordingComplete(it) }
                            }
                            isRecording -> {
                                // Stop recording
                                stopRecording(mediaRecorder)
                                isRecording = false
                                isPaused = false
                            }
                            else -> {
                                // Start recording
                                if (recordAudioPermission.status.isGranted) {
                                    audioFile = startRecording(context, mediaRecorder) { recorder ->
                                        mediaRecorder = recorder
                                        isRecording = true
                                        isPaused = false
                                        recordingTime = 0
                                    }
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        audioFile != null -> Icons.Default.Send // Send icon
                        isRecording -> Icons.Default.Stop // Stop icon
                        else -> Icons.Default.Mic // Mic icon
                    },
                    contentDescription = when {
                        audioFile != null -> "Send"
                        isRecording -> "Stop"
                        else -> "Record"
                    },
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }

        // Simple instructions
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = when {
                audioFile != null -> "Tap send to share your voice message"
                isRecording && !isPaused -> "Tap pause or stop when finished"
                isPaused -> "Tap resume to continue recording"
                else -> "Tap record to start voice message"
            },
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }

    // Cleanup
    LaunchedEffect(Unit) {
        if (!isRecording && audioFile == null) {
            mediaRecorder?.release()
        }
    }
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Suppress("DEPRECATION")
private fun startRecording(
    context: Context,
    currentRecorder: MediaRecorder?,
    onRecorderCreated: (MediaRecorder) -> Unit
): File? {
    return try {
        currentRecorder?.release()

        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(null)
        val audioFile = File.createTempFile("VOICE_${timeStamp}_", ".3gp", storageDir)

        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile.absolutePath)
            prepare()
            start()
        }

        onRecorderCreated(mediaRecorder)
        audioFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun stopRecording(mediaRecorder: MediaRecorder?) {
    try {
        mediaRecorder?.apply {
            stop()
            release()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null
) {
    onClick()
}

