package com.sam.ayaana.presentation.screens.notifications


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSettingsScreen(
    navController: NavHostController
) {
    // State for switches
    var pauseAll by remember { mutableStateOf(false) }
    var sleepMode by remember { mutableStateOf(false) }
    var postsStoriesComments by remember { mutableStateOf(true) }
    var followingFollowers by remember { mutableStateOf(true) }
    var messages by remember { mutableStateOf(true) }
    var calls by remember { mutableStateOf(false) }
    var liveReels by remember { mutableStateOf(true) }
    var fundraisers by remember { mutableStateOf(false) }
    var fromInstagram by remember { mutableStateOf(true) }
    var birthdays by remember { mutableStateOf(true) }
    var otherNotifications by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Push notifications header
            item {
                Text(
                    text = "Push notifications",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            // Pause all section
            item {
                NotificationSettingItem(
                    icon = Icons.Default.Pause,
                    title = "Pause all",
                    description = "Temporarily pause notifications",
                    checked = pauseAll,
                    onCheckedChange = { pauseAll = it },
                    showDivider = false
                )
            }

            // Sleep mode section
            item {
                NotificationSettingItem(
                    icon = Icons.Default.Schedule,
                    title = "Sleep mode",
                    description = "Automatically mute notifications at night or whenever you need to focus",
                    checked = sleepMode,
                    onCheckedChange = { sleepMode = it },
                    showDivider = true
                )
            }

            // Posts, stories and comments
            item {
                Text(
                    text = "Posts, stories and comments",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.NotificationsActive,
                    title = "Following and followers",
                    description = "Get notified about new followers and follow requests",
                    checked = followingFollowers,
                    onCheckedChange = { followingFollowers = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            // Messages section
            item {
                Text(
                    text = "Messages",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Direct messages",
                    description = "Notify me about new messages",
                    checked = messages,
                    onCheckedChange = { messages = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            // Calls section
            item {
                Text(
                    text = "Calls",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Voice and video calls",
                    description = "Get notified about incoming calls",
                    checked = calls,
                    onCheckedChange = { calls = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            // Live and reels section
            item {
                Text(
                    text = "Live and reels",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.NotificationsActive,
                    title = "Live videos and reels",
                    description = "Notify me when people I follow go live or post reels",
                    checked = liveReels,
                    onCheckedChange = { liveReels = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            // From Instagram section
            item {
                Text(
                    text = "From Instagram",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Fundraisers",
                    description = "Updates about fundraisers you follow or participate in",
                    checked = fundraisers,
                    onCheckedChange = { fundraisers = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Instagram updates",
                    description = "News, tips and announcements from Instagram",
                    checked = fromInstagram,
                    onCheckedChange = { fromInstagram = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.NotificationsActive,
                    title = "Birthdays",
                    description = "Reminders for friends' birthdays",
                    checked = birthdays,
                    onCheckedChange = { birthdays = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            // Other notification types
            item {
                Text(
                    text = "Other notification types",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.Notifications,
                    title = "Reminders and suggestions",
                    description = "Get reminders and personalized suggestions",
                    checked = otherNotifications,
                    onCheckedChange = { otherNotifications = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            // Email notifications section
            item {
                Text(
                    text = "Email notifications",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            item {
                NotificationSettingItem(
                    icon = Icons.Default.NotificationsOff,
                    title = "Email updates",
                    description = "Receive notifications via email",
                    checked = emailNotifications,
                    onCheckedChange = { emailNotifications = it },
                    showDivider = false,
                    showSwitch = true
                )
            }

            // Add some bottom padding
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun NotificationSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    showDivider: Boolean = true,
    showSwitch: Boolean = true
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.padding(start = 16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )

                    if (description != null) {
                        Text(
                            text = description,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (showSwitch) {
                    Spacer(modifier = Modifier.padding(start = 16.dp))
                    Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF0095F6),
                            checkedTrackColor = Color(0xFF0095F6).copy(alpha = 0.5f)
                        )
                    )
                }
            }

            if (showDivider) {
                Spacer(modifier = Modifier.height(1.dp))
            }
        }
    }
}