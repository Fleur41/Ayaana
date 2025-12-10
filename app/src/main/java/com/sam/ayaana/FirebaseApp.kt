package com.sam.ayaana

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.sam.ayaana.Utils.FirebaseNotificationService
import com.sam.ayaana.datastore.DatastoreRepository
import com.sam.ayaana.navigation.AppNavigation
import com.sam.ayaana.navigation.NavigationDestination
import com.sam.ayaana.presentation.screens.theme.ThemeViewModel
import com.sam.ayaana.ui.theme.AyaanaTheme

@Composable
fun FirebaseApp(
    startDestination: NavigationDestination
) {
    val themeViewModel = hiltViewModel<ThemeViewModel>()
    val theme by themeViewModel.theme.collectAsState()
    val context = LocalContext.current

    val navController = rememberNavController()
    AyaanaTheme(
        darkTheme = theme == "dark"
    ) {
        AppNavigation(
                navController = navController,
                startDestination = startDestination
        )
//        Box(modifier = Modifier.fillMaxSize()){
//            AppNavigation(
//                navController = navController,
//                startDestination = startDestination
//            )
//
//            Button(
//                onClick = {
//                    FirebaseNotificationService.triggerTestNotification(
//                        context = context,
//                        title = "Test Notification",
//                        message = "This is a notification from Ayaana!"
//                    )
//                    Toast.makeText(context, "Test notification sent!", Toast.LENGTH_SHORT).show()
//                },
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(16.dp)
//                    .width(120.dp)
//                    .height(48.dp)
//            ) {
//                Text("Test Notify")
//            }
//        }
    }
}