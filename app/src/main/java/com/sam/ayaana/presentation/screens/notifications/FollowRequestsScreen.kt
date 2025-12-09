package com.sam.ayaana.presentation.screens.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sam.ayaana.domain.model.FollowRequest
import com.sam.ayaana.domain.model.SuggestedUser

// presentation/screens/notifications/FollowRequestsScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowRequestsScreen(
    navController: NavHostController,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val followRequestsState by viewModel.followRequestsState.collectAsState()
    val suggestedUsersState by viewModel.suggestedUsersState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Follow requests", fontWeight = FontWeight.Bold) },
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
            // Follow Requests Section
            item {
                Text(
                    text = "Follow requests",
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            when (val state = followRequestsState) {
                is NotificationsViewModel.FollowRequestsState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ){
                            CircularProgressIndicator()
                        }
                    }
                }

                is NotificationsViewModel.FollowRequestsState.Success -> {
                    if (state.requests.isEmpty()) {
                        item {
                            Text(
                                text = "No follow requests",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(state.requests) { request ->
                            FollowRequestItem(
                                request = request,
                                onAccept = { viewModel.acceptFollowRequest(request.id) },
                                onDelete = { viewModel.deleteFollowRequest(request.id) }
                            )
                        }
                    }
                }

                is NotificationsViewModel.FollowRequestsState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Suggested for you Section
            item {
                Text(
                    text = "Suggested for you",
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            when (val state = suggestedUsersState) {
                is NotificationsViewModel.SuggestedUsersState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is NotificationsViewModel.SuggestedUsersState.Success -> {
                    if (state.users.isEmpty()) {
                        item {
                            Text(
                                text = "No suggestions",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textAlign = TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(state.users) { user ->
                            SuggestedUserItem(
                                user = user,
                                onFollow = { viewModel.followSuggestedUser(user.id) },
                                onDismiss = { viewModel.dismissSuggestedUser(user.id) }
                            )
                        }
                    }
                }

                is NotificationsViewModel.SuggestedUsersState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FollowRequestItem(
    request: FollowRequest,
    onAccept: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        AsyncImage(
            model = request.profileImage ?: "https://picsum.photos/seed/${request.id}/100/100",
            contentDescription = "${request.fullName}'s profile",
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // User Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = request.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = request.fullName,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            if (request.mutualFollowers.isNotEmpty()) {
                Text(
                    text = if (request.mutualFollowers.size == 1) {
                        "Followed by ${request.mutualFollowers.first()}"
                    } else {
                        "Followed by ${request.mutualFollowers.first()} + ${request.mutualFollowers.size - 1} more"
                    },
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0095F6)
                ),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Confirm",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onDelete,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp
                ),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Delete",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SuggestedUserItem(
    user: SuggestedUser,
    onFollow: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Image
        AsyncImage(
            model = user.profileImage ?: "https://picsum.photos/seed/${user.id}/100/100",
            contentDescription = "${user.fullName}'s profile",
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // User Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.userName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = user.fullName,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = user.reason,
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (user.mutualConnections.isNotEmpty()) {
                Text(
                    text = if (user.mutualConnections.size == 1) {
                        "Followed by ${user.mutualConnections.first()}"
                    } else {
                        "Followed by ${user.mutualConnections.first()} + ${user.mutualConnections.size - 1} more"
                    },
                    color = Color.Gray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Action Buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onFollow,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0095F6)
                ),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Follow",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

//package com.sam.ayaana.presentation.screens.notifications
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.PersonAdd
//import androidx.compose.material3.Button
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
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
//import com.sam.ayaana.domain.model.FollowRequest
//import com.sam.ayaana.domain.model.SuggestedUser
//import com.sam.ayaana.presentation.screens.notifications.NotificationsViewModel.FollowRequestsState
//import com.sam.ayaana.presentation.screens.notifications.NotificationsViewModel.SuggestedUsersState
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FollowRequestsScreen(
//    navController: NavController,
//    viewModel: NotificationsViewModel = hiltViewModel()
//) {
//    val followRequestsState by viewModel.followRequestsState.collectAsState()
//    val suggestedUsersState by viewModel.suggestedUsersState.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Follow requests") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.navigateUp() }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            // Follow Requests Section
//            Text(
//                text = "Follow requests",
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//
//            when (val state = followRequestsState) {
//                is FollowRequestsState.Loading -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//
//                is FollowRequestsState.Success -> {
//                    if (state.requests.isNotEmpty()) {
//                        FollowRequestsList(
//                            requests = state.requests,
//                            onAccept = { requestId -> viewModel.acceptFollowRequest(requestId) },
//                            onDelete = { requestId -> viewModel.deleteFollowRequest(requestId) }
//                        )
//                    } else {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(100.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("No follow requests")
//                        }
//                    }
//                }
//
//                is FollowRequestsState.Error -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = state.message,
//                            color = MaterialTheme.colorScheme.error
//                        )
//                    }
//                }
//            }
//
//            // Suggested for you section
//            Text(
//                text = "Suggested for you",
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//
//            when (val state = suggestedUsersState) {
//                is SuggestedUsersState.Loading -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(200.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//
//                is SuggestedUsersState.Success -> {
//                    if (state.users.isNotEmpty()) {
//                        SuggestedUsersList(
//                            users = state.users,
//                            onFollow = { userId -> viewModel.followSuggestedUser(userId) },
//                            onDismiss = { userId -> viewModel.dismissSuggestedUser(userId) }
//                        )
//                    } else {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(100.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("No suggestions available")
//                        }
//                    }
//                }
//
//                is SuggestedUsersState.Error -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = state.message,
//                            color = MaterialTheme.colorScheme.error
//                        )
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}
//
//@Composable
//fun FollowRequestsList(
//    requests: List<FollowRequest>,
//    onAccept: (String) -> Unit,
//    onDelete: (String) -> Unit
//) {
//    LazyColumn(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        items(requests) { request ->
//            FollowRequestItem(
//                request = request,
//                onAccept = { onAccept(request.id) },
//                onDelete = { onDelete(request.id) }
//            )
//            HorizontalDivider()
//        }
//    }
//}
//
//@Composable
//fun FollowRequestItem(
//    request: FollowRequest,
//    onAccept: () -> Unit,
//    onDelete: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp, vertical = 4.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Profile image
//            request.profileImage?.let { imageUrl ->
//                AsyncImage(
//                    model = imageUrl,
//                    contentDescription = "Profile",
//                    modifier = Modifier
//                        .size(60.dp)
//                        .clip(CircleShape)
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//            } ?: run {
//                Box(
//                    modifier = Modifier
//                        .size(60.dp)
//                        .clip(CircleShape)
//                        .background(Color.LightGray),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Default.PersonAdd,
//                        contentDescription = "Profile",
//                        modifier = Modifier.size(30.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(12.dp))
//            }
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = request.userName,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = request.fullName,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//                if (request.mutualFollowers.isNotEmpty()) {
//                    Text(
//                        text = if (request.mutualFollowers.size == 1) {
//                            "Followed by ${request.mutualFollowers.first()}"
//                        } else {
//                            "Followed by ${request.mutualFollowers.first()} + ${request.mutualFollowers.size - 1} more"
//                        },
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray,
//                        fontSize = 12.sp,
//                        modifier = Modifier.padding(top = 4.dp)
//                    )
//                }
//            }
//
//            // Action buttons
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Button(
//                    onClick = onAccept,
//                    modifier = Modifier.width(100.dp)
//                ) {
//                    Text("Confirm")
//                }
//                OutlinedButton(
//                    onClick = onDelete,
//                    modifier = Modifier.width(100.dp)
//                ) {
//                    Text("Delete")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SuggestedUsersList(
//    users: List<SuggestedUser>,
//    onFollow: (String) -> Unit,
//    onDismiss: (String) -> Unit
//) {
//    LazyColumn(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        items(users) { user ->
//            SuggestedUserItem(
//                user = user,
//                onFollow = { onFollow(user.id) },
//                onDismiss = { onDismiss(user.id) }
//            )
//            HorizontalDivider()
//        }
//    }
//}
//
//@Composable
//fun SuggestedUserItem(
//    user: SuggestedUser,
//    onFollow: () -> Unit,
//    onDismiss: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 8.dp, vertical = 4.dp),
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Profile image
//            user.profileImage?.let { imageUrl ->
//                AsyncImage(
//                    model = imageUrl,
//                    contentDescription = "Profile",
//                    modifier = Modifier
//                        .size(60.dp)
//                        .clip(CircleShape)
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//            } ?: run {
//                Box(
//                    modifier = Modifier
//                        .size(60.dp)
//                        .clip(CircleShape)
//                        .background(Color.LightGray),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        Icons.Default.PersonAdd,
//                        contentDescription = "Profile",
//                        modifier = Modifier.size(30.dp)
//                    )
//                }
//                Spacer(modifier = Modifier.width(12.dp))
//            }
//
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = user.userName,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = user.fullName,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//                Text(
//                    text = user.reason,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray,
//                    fontSize = 12.sp,
//                    modifier = Modifier.padding(top = 4.dp)
//                )
//                if (user.mutualConnections.isNotEmpty()) {
//                    Text(
//                        text = if (user.mutualConnections.size == 1) {
//                            "Followed by ${user.mutualConnections.first()}"
//                        } else {
//                            "Followed by ${user.mutualConnections.first()} + ${user.mutualConnections.size - 1} more"
//                        },
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray,
//                        fontSize = 11.sp,
//                        modifier = Modifier.padding(top = 2.dp)
//                    )
//                }
//            }
//
//            // Action buttons
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                Button(
//                    onClick = onFollow,
//                    modifier = Modifier.width(100.dp)
//                ) {
//                    Text("Follow")
//                }
//                IconButton(
//                    onClick = onDismiss,
//                    modifier = Modifier.size(48.dp)
//                ) {
//                    Icon(
//                        Icons.Default.Close,
//                        contentDescription = "Dismiss",
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }
//        }
//    }
//}