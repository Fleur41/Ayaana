package com.sam.ayaana

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.sam.ayaana.navigation.AppNavigation
import com.sam.ayaana.navigation.NavigationDestination
import com.sam.ayaana.ui.theme.AyaanaTheme

@Composable
fun FirebaseApp(
    startDestination: NavigationDestination
) {
    val navController = rememberNavController()
    AyaanaTheme {
        AppNavigation(
            navController = navController,
            startDestination = startDestination
        )
    }
}