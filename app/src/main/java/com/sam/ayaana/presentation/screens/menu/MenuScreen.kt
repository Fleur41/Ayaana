package com.sam.ayaana.presentation.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sam.ayaana.navigation.NavigationDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavHostController,
    onBackClick: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val isPrivateAccount by viewModel.isPrivateAccount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar with back button
        TopAppBar(
            title = {
                Text(
                    text = "Settings and Activity",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        // Search Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Search",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium
//                )
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = "Search",
//                    modifier = Modifier.size(24.dp)
//                )
//            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Menu Items
        // 1. Chats
        MenuItem(
            icon = Icons.AutoMirrored.Filled.Chat,
            title = "Chats",
            onClick = { navController.navigate("chat") }
        )

        // 2. Saved (using BookmarkBorder icon)
        MenuItem(
            icon = Icons.Default.BookmarkBorder,
            title = "Saved",
            onClick = { /* Navigate to saved items */ }
        )

        // 3. Notifications
        MenuItem(
            icon = Icons.Default.Notifications,
            title = "Notifications",
            onClick = { /* Navigate to notifications */ }
        )

        // 4. Comments
        MenuItem(
            icon = Icons.AutoMirrored.Filled.Comment,
            title = "Comments",
            onClick = { /* Navigate to comments */ }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

        // 5. Privacy Settings with Toggle
        MenuItem(
            icon = Icons.Default.Settings,
            title = "Privacy Settings",
            onClick = {
                navController.navigate(NavigationDestination.Privacy.route)
            }
        )
//        MenuItemWithToggle(
//            icon = Icons.Default.Settings,
//            title = "Privacy Settings",
//            isChecked = isPrivateAccount,
//            onCheckedChange = { isPrivate ->
//                viewModel.togglePrivacySetting(isPrivate)
//            }
//        )

        // 6. Theme - Navigates to ThemeScreen (not toggle here)
        MenuItem(
            icon = Icons.Default.DarkMode,
            title = "Theme",
            onClick = {
                navController.navigate("theme")
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
        // Spacer
        Box(modifier = Modifier.weight(1f))

        // 7. Logout (Red text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    // Call ViewModel to handle logout
                    viewModel.signOut()
                    navController.navigate("sign_in") {
                        popUpTo(0)
                    }
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Logout",
                    color = Color.Red,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Navigate",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun MenuItemWithToggle(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF0095F6),
                    checkedTrackColor = Color(0xFF0095F6).copy(alpha = 0.5f)
                )
            )
        }
    }
}