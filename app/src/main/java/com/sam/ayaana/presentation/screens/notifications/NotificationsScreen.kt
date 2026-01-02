package com.sam.ayaana.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sam.ayaana.domain.model.Notification
import java.util.Date
import kotlin.collections.filter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    navController: NavHostController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notificationsState by viewModel.notificationsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllData()
    }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = notificationsState) {
                is NotificationsViewModel.NotificationsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 16.dp)
                    )
                }

                is NotificationsViewModel.NotificationsState.Success -> {
                    NotificationsList(
                        notifications = state.notifications,
                        onFollowRequestClick = {
                            navController.navigate("followRequests")
                        },
                        onNotificationClick = { notification ->
                            // Handle other notification clicks
                            when (notification.type) {
                                Notification.NotificationType.LIKE,
                                Notification.NotificationType.COMMENT,
                                Notification.NotificationType.MENTION -> {
                                    // Navigate to post
                                }

                                else -> {}
                            }
                        }
                    )
                }

                is NotificationsViewModel.NotificationsState.Error -> {
                    Text(
                        text = state.message,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


//@Composable
//fun NotificationsList(
//    notifications: List<Notification>,
//    onFollowRequestClick: () -> Unit,
//    onNotificationClick: (Notification) -> Unit
//) {
//    LazyColumn(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // New section
//        val newNotifications = notifications.filter {
//            !it.isRead && it.timeGroup == Notification.TimeGroup.TODAY
//        }
//        if (newNotifications.isNotEmpty()) {
//            item {
//                Text(
//                    text = "New",
//                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp),
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 14.sp,
//                    color = Color.Black
//                )
//            }
//            items(newNotifications) { notification ->
//                NotificationItem(
//                    notification = notification,
//                    onClick = {
//                        when (notification.type) {
//                            Notification.NotificationType.FOLLOW_REQUEST -> onFollowRequestClick()
//                            else -> onNotificationClick(notification)
//                        }
//                    }
//                )
//            }
//        }
//
//        // Today section
//        val todayNotifications = notifications.filter {
//            it.timeGroup == Notification.TimeGroup.TODAY && it.isRead
//        }
//        if (todayNotifications.isNotEmpty()) {
//            item {
//                Text(
//                    text = "Today",
//                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//            }
//            items(todayNotifications) { notification ->
//                NotificationItem(
//                    notification = notification,
//                    onClick = {
//                        when (notification.type) {
//                            Notification.NotificationType.FOLLOW_REQUEST -> onFollowRequestClick()
//                            else -> onNotificationClick(notification)
//                        }
//                    }
//                )
//            }
//        }
//
//        // This week section
//        val thisWeekNotifications = notifications.filter {
//            it.timeGroup == Notification.TimeGroup.LAST_7_DAYS
//        }
//        if (thisWeekNotifications.isNotEmpty()) {
//            item {
//                Text(
//                    text = "This week",
//                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//            }
//            items(thisWeekNotifications) { notification ->
//                NotificationItem(
//                    notification = notification,
//                    onClick = {
//                        when (notification.type) {
//                            Notification.NotificationType.FOLLOW_REQUEST -> onFollowRequestClick()
//                            else -> onNotificationClick(notification)
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
@Composable
fun NotificationsList(
    notifications: List<Notification>,
    onFollowRequestClick: () -> Unit,
    onNotificationClick: (Notification) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // New section
        val newNotifications = notifications.filter {
            !it.isRead && it.timeGroup == Notification.TimeGroup.TODAY
        }
        if (newNotifications.isNotEmpty()) {
            item {
                Text(
                    text = "New",
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            items(newNotifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        when (notification.type) {
                            Notification.NotificationType.FOLLOW_REQUEST -> onFollowRequestClick()
                            Notification.NotificationType.LIVE_STREAM -> {
                                // Extract streamId from notification data
                                // This depends on how you structure your notification data
                                val streamId = notification.targetUserId ?: "live_1"
                                // Navigate to LiveViewerScreen
                                onNotificationClick(notification)
                            }
                            else -> onNotificationClick(notification)
                        }
                    }
                )
            }
        }

        // ... rest of your code remains the same
        // Today section
        val todayNotifications = notifications.filter {
            it.timeGroup == Notification.TimeGroup.TODAY && it.isRead
        }
        if (todayNotifications.isNotEmpty()) {
            item {
                Text(
                    text = "Today",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            items(todayNotifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        when (notification.type) {
                            Notification.NotificationType.FOLLOW_REQUEST -> onFollowRequestClick()
                            else -> onNotificationClick(notification)
                        }
                    }
                )
            }
        }

        // This week section
        val thisWeekNotifications = notifications.filter {
            it.timeGroup == Notification.TimeGroup.LAST_7_DAYS
        }
        if (thisWeekNotifications.isNotEmpty()) {
            item {
                Text(
                    text = "This week",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            items(thisWeekNotifications) { notification ->
                NotificationItem(
                    notification = notification,
                    onClick = {
                        when (notification.type) {
                            Notification.NotificationType.FOLLOW_REQUEST -> onFollowRequestClick()
                            else -> onNotificationClick(notification)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile image or icon - UPDATE THIS SECTION
            when (notification.type) {
                Notification.NotificationType.FOLLOW_REQUEST -> {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0095F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Follow request",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Notification.NotificationType.LIKE -> {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFED4956)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Notification.NotificationType.COMMENT -> {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF8E8E8E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Notification.NotificationType.LIVE_STREAM -> { // ADD THIS CASE
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Red), // Red for live indicator
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Live stream",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                else -> {
                    AsyncImage(
                        model = notification.userProfileImage ?: "https://picsum.photos/id/${notification.id.hashCode() % 100}/200/200",
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Notification content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )

                    // Add blue dot for unread notifications
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0095F6))
                        )
                    }

                    // ADD LIVE BADGE for live stream notifications
                    if (notification.type == Notification.NotificationType.LIVE_STREAM) {
                        Box(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Red)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LIVE",
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = notification.message,
                    color = Color.Gray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = formatTimeAgo(notification.timestamp),
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right side content - UPDATE THIS SECTION
            when {
                // Forward arrow for follow requests
                notification.type == Notification.NotificationType.FOLLOW_REQUEST -> {
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "View requests",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
                // Follow button for suggested friends
                notification.type == Notification.NotificationType.SUGGESTED_FRIEND -> {
                    Button(
                        onClick = { /* Follow action */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0095F6)
                        ),
                        modifier = Modifier
                            .height(32.dp)
                            .width(88.dp),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Follow",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Watch button for live streams - ADD THIS
                notification.type == Notification.NotificationType.LIVE_STREAM -> {
                    Button(
                        onClick = { /* Navigate to live stream */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        modifier = Modifier
                            .height(32.dp)
                            .width(88.dp),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "Watch",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                // Post image for likes/comments/mentions
                notification.postImage != null && notification.type != Notification.NotificationType.SUGGESTED_FRIEND -> {
                    AsyncImage(
                        model = notification.postImage,
                        contentDescription = "Post",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
                // For other notifications, just show a small arrow if needed
                else -> {
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "View",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}



//@Composable
//fun NotificationItem(
//    notification: Notification,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(horizontal = 8.dp, vertical = 4.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = Color.White
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Profile image or icon
//            when (notification.type) {
//                Notification.NotificationType.FOLLOW_REQUEST -> {
//                    Box(
//                        modifier = Modifier
//                            .size(44.dp)
//                            .clip(CircleShape)
//                            .background(Color(0xFF0095F6)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.PersonAdd,
//                            contentDescription = "Follow request",
//                            tint = Color.White,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
//                Notification.NotificationType.LIKE -> {
//                    Box(
//                        modifier = Modifier
//                            .size(44.dp)
//                            .clip(CircleShape)
//                            .background(Color(0xFFED4956)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.FavoriteBorder,
//                            contentDescription = "Like",
//                            tint = Color.White,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
//                Notification.NotificationType.COMMENT -> {
//                    Box(
//                        modifier = Modifier
//                            .size(44.dp)
//                            .clip(CircleShape)
//                            .background(Color(0xFF8E8E8E)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.ChatBubbleOutline,
//                            contentDescription = "Comment",
//                            tint = Color.White,
//                            modifier = Modifier.size(20.dp)
//                        )
//                    }
//                }
//                else -> {
//                    AsyncImage(
//                        model = notification.userProfileImage ?: "https://picsum.photos/id/${notification.id.hashCode() % 100}/200/200",
//                        contentDescription = "Profile",
//                        modifier = Modifier
//                            .size(44.dp)
//                            .clip(CircleShape)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // Notification content
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Row(verticalAlignment = Alignment.CenterVertically) {
//                    Text(
//                        text = notification.title,
//                        fontWeight = FontWeight.SemiBold,
//                        fontSize = 14.sp,
//                        color = Color.Black
//                    )
//
//                    // Add blue dot for unread notifications
//                    if (!notification.isRead) {
//                        Box(
//                            modifier = Modifier
//                                .padding(start = 4.dp)
//                                .size(8.dp)
//                                .clip(CircleShape)
//                                .background(Color(0xFF0095F6))
//                        )
//                    }
//                }
//
//                Text(
//                    text = notification.message,
//                    color = Color.Gray,
//                    fontSize = 13.sp,
//                    modifier = Modifier.padding(top = 2.dp)
//                )
//
//                Text(
//                    text = formatTimeAgo(notification.timestamp),
//                    color = Color.LightGray,
//                    fontSize = 11.sp,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }
//
//            // Right side content
//            when {
//                // Forward arrow for follow requests
//                notification.type == Notification.NotificationType.FOLLOW_REQUEST -> {
//                    Icon(
//                        imageVector = Icons.Filled.ArrowForwardIos,
//                        contentDescription = "View requests",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(16.dp)
//                    )
//                }
//                // Follow button for suggested friends - FIXED: Now at far right
//                notification.type == Notification.NotificationType.SUGGESTED_FRIEND -> {
//                    Button(
//                        onClick = { /* Follow action */ },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFF0095F6)
//                        ),
//                        modifier = Modifier
//                            .height(32.dp)
//                            .width(88.dp),
//                        shape = RoundedCornerShape(4.dp),
//                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
//                    ) {
//                        Text(
//                            text = "Follow",
//                            fontSize = 12.sp,
//                            fontWeight = FontWeight.Bold
//                        )
//                    }
//                }
//                // Post image for likes/comments/mentions
//                notification.postImage != null && notification.type != Notification.NotificationType.SUGGESTED_FRIEND -> {
//                    AsyncImage(
//                        model = notification.postImage,
//                        contentDescription = "Post",
//                        modifier = Modifier
//                            .size(44.dp)
//                            .clip(RoundedCornerShape(4.dp))
//                    )
//                }
//            }
//        }
//    }
//}
@Composable
fun formatTimeAgo(timestamp: Date): String {
    val now = Date()
    val diff = now.time - timestamp.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        days < 30 -> "${days / 7}w ago"
        else -> "${days / 30}mo ago"
    }
}


//package com.sam.ayaana.presentation.screens.notifications
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//import com.sam.ayaana.domain.model.Notification
//import com.sam.ayaana.presentation.screens.notifications.NotificationsViewModel.NotificationsState
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationsScreen(
//    navController: NavController,
//    viewModel: NotificationsViewModel = hiltViewModel()
//) {
//    val notificationsState by viewModel.notificationsState.collectAsState()
//    val selectedNotification by viewModel.selectedNotification.collectAsState()
//
//    LaunchedEffect(Unit) {
//        viewModel.loadAllData()
//    }
//
//    // Navigate to follow requests when a follow request notification is clicked
//    LaunchedEffect(selectedNotification) {
//        selectedNotification?.let { notification ->
//            if (notification.type == Notification.NotificationType.FOLLOW_REQUEST) {
//                navController.navigate("followRequests")
//                viewModel.clearSelectedNotification()
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Notifications") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.navigateUp() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            when (val state = notificationsState) {
//                is NotificationsState.Loading -> {
//                    CircularProgressIndicator(
//                        modifier = Modifier.align(Alignment.Center)
//                    )
//                }
//
//                is NotificationsState.Success -> {
//                    NotificationsList(
//                        notifications = state.notifications,
//                        onNotificationClick = { notification ->
//                            viewModel.selectNotification(notification)
//                            viewModel.markNotificationAsRead(notification.id)
//                        }
//                    )
//                }
//
//                is NotificationsState.Error -> {
//                    Text(
//                        text = state.message,
//                        modifier = Modifier.align(Alignment.Center),
//                        color = MaterialTheme.colorScheme.error
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun NotificationsList(
//    notifications: List<Notification>,
//    onNotificationClick: (Notification) -> Unit
//) {
//    LazyColumn(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Group notifications by time
//        val todayNotifications = notifications.filter { it.timeGroup == Notification.TimeGroup.TODAY }
//        val yesterdayNotifications = notifications.filter { it.timeGroup == Notification.TimeGroup.YESTERDAY }
//        val last7DaysNotifications = notifications.filter { it.timeGroup == Notification.TimeGroup.LAST_7_DAYS }
//        val last30DaysNotifications = notifications.filter { it.timeGroup == Notification.TimeGroup.LAST_30_DAYS }
//
//        if (todayNotifications.isNotEmpty()) {
//            item {
//                TimeSectionHeader("Today")
//            }
//            items(todayNotifications) { notification ->
//                NotificationItem(
//                    notification = notification,
//                    onClick = { onNotificationClick(notification) }
//                )
//                HorizontalDivider()
//            }
//        }
//
//        if (yesterdayNotifications.isNotEmpty()) {
//            item {
//                TimeSectionHeader("Yesterday")
//            }
//            items(yesterdayNotifications) { notification ->
//                NotificationItem(
//                    notification = notification,
//                    onClick = { onNotificationClick(notification) }
//                )
//                HorizontalDivider()
//            }
//        }
//
//        if (last7DaysNotifications.isNotEmpty()) {
//            item {
//                TimeSectionHeader("Last 7 days")
//            }
//            items(last7DaysNotifications) { notification ->
//                NotificationItem(
//                    notification = notification,
//                    onClick = { onNotificationClick(notification) }
//                )
//                HorizontalDivider()
//            }
//        }
//
//        if (last30DaysNotifications.isNotEmpty()) {
//            item {
//                TimeSectionHeader("Last 30 days")
//            }
//            items(last30DaysNotifications) { notification ->
//                NotificationItem(
//                    notification = notification,
//                    onClick = { onNotificationClick(notification) }
//                )
//                HorizontalDivider()
//            }
//        }
//    }
//}
//
//@Composable
//fun TimeSectionHeader(title: String) {
//    Text(
//        text = title,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp),
//        style = MaterialTheme.typography.titleSmall,
//        fontWeight = FontWeight.Bold,
//        color = MaterialTheme.colorScheme.primary
//    )
//}
//
//@Composable
//fun NotificationItem(
//    notification: Notification,
//    onClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp, vertical = 4.dp)
//            .clickable(onClick = onClick),
//        colors = CardDefaults.cardColors(
//            containerColor = if (notification.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant
//        ),
//        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Profile image if available
//            notification.userProfileImage?.let { imageUrl ->
//                AsyncImage(
//                    model = imageUrl,
//                    contentDescription = "Profile",
//                    modifier = Modifier
//                        .size(48.dp)
//                        .clip(CircleShape)
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//            }
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = notification.title,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = notification.message,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//                Text(
//                    text = formatTimestamp(notification.timestamp),
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray,
//                    fontSize = 12.sp,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//            }
//
//            // Unread indicator
//            if (!notification.isRead) {
//                Box(
//                    modifier = Modifier
//                        .size(8.dp)
//                        .clip(CircleShape)
//                        .background(Color.Blue)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun formatTimestamp(timestamp: java.util.Date): String {
//    // TODO: Implement proper timestamp formatting
//    return "2h ago" // Placeholder
//}