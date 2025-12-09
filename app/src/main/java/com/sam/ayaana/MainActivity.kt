package com.sam.ayaana

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.sam.ayaana.settings.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.concurrent.thread


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val settingsViewModel by viewModels<SettingsViewModel>()
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted){
            Toast.makeText(this, "Notifications enabled! You'll get updates on new messages and activities.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notifications disabled! You'll not get updates on new messages and activities.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        setContent {
            val startDestination by settingsViewModel.startDestination.collectAsState()
            FirebaseApp(startDestination)
        }
    }

    private fun requestNotificationPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val permission = Manifest.permission.POST_NOTIFICATIONS

            when {
                ContextCompat.checkSelfPermission(this, permission) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    Toast.makeText(this, "Notifications are enabled", Toast.LENGTH_SHORT).show()
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    showPermissionRationale()
                }
                else -> {
                    requestPermissionLauncher.launch(permission)
                }
            }
        }
    }

    private fun showPermissionRationale() {
        Toast.makeText(
            this,
            "Enable notifications to get updates on new messages, follow requests, and activities",
            Toast.LENGTH_LONG
        ).show()

        // Small delay before requesting permission
        Handler(mainLooper).postDelayed({
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }, 2000) // 2 second delay
    }
}



//
//import android.Manifest
//import android.os.Build
//import android.os.Bundle
//import android.widget.Toast
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.window.Dialog
//import androidx.core.content.ContextCompat
//import com.sam.ayaana.settings.SettingsViewModel
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity() {
//
//    companion object {
//        private const val NOTIFICATION_PERMISSION_CODE = 1001
//    }
//
//    private val settingsViewModel by viewModels<SettingsViewModel>()
//
//    // Use Activity Result API for permission request (modern approach)
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            Toast.makeText(
//                this,
//                "Notifications enabled!",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//        // Don't show anything if denied - user made their choice
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            val startDestination by settingsViewModel.startDestination.collectAsState()
//            var showPermissionDialog by remember { mutableStateOf(false) }
//
//            // Check permission when composable is first composed
//            LaunchedEffect(Unit) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    val permission = Manifest.permission.POST_NOTIFICATIONS
//                    val hasPermission = ContextCompat.checkSelfPermission(
//                        this@MainActivity,
//                        permission
//                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
//
//                    if (!hasPermission && !shouldShowRequestPermissionRationale(permission)) {
//                        // Show rationale dialog before requesting
//                        showPermissionDialog = true
//                    } else if (!hasPermission) {
//                        // Request directly if rationale already shown
//                        requestPermissionLauncher.launch(permission)
//                    }
//                }
//            }
//
//            // Permission rationale dialog
//            if (showPermissionDialog) {
//                PermissionRationaleDialog(
//                    onDismiss = { showPermissionDialog = false },
//                    onConfirm = {
//                        showPermissionDialog = false
//                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//                    }
//                )
//            }
//
//            FirebaseApp(startDestination)
//        }
//    }
//}
//
//@Composable
//fun PermissionRationaleDialog(
//    onDismiss: () -> Unit,
//    onConfirm: () -> Unit
//) {
//    Dialog(onDismissRequest = onDismiss) {
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            shape = MaterialTheme.shapes.large
//        ) {
//            Column(
//                modifier = Modifier.padding(24.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    text = "Enable Notifications",
//                    style = MaterialTheme.typography.headlineSmall,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//
//                Text(
//                    text = "Stay updated with:\n• New messages\n• Follow requests\n• Likes and comments\n• Activity updates",
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(bottom = 24.dp)
//                )
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.End,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    TextButton(
//                        onClick = onDismiss,
//                        modifier = Modifier.padding(end = 8.dp)
//                    ) {
//                        Text("Maybe Later")
//                    }
//
//                    Button(onClick = onConfirm) {
//                        Text("Enable Notifications")
//                    }
//                }
//            }
//        }
//    }
//}