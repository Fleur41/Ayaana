package com.sam.ayaana

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
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

    val navController = rememberNavController()
    AyaanaTheme(
        darkTheme = theme == "dark"
    ) {
        AppNavigation(
            navController = navController,
            startDestination = startDestination
        )
    }
}