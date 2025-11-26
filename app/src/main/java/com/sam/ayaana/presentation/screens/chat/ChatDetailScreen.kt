package com.sam.ayaana.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.Message
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    navController: NavController,
    chatId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val selectedChat by viewModel.selectedChat.collectAsState()
    val messagesState by viewModel.messagesState.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(chatId) {
        if (selectedChat?.id != chatId) {
            viewModel.loadMessages(chatId)
        }
    }

    LaunchedEffect(messagesState) {
        if (messagesState is Result.Success) {
            delay(100)
            if (messagesState is Result.Success<List<Message>>) {
                val messages = (messagesState as Result.Success<List<Message>>).data
                if (messages.isNotEmpty()) {
                    lazyListState.animateScrollToItem(0)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = selectedChat?.profileImage ?: "https://picsum.photos/id/103/200/300",
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedChat?.username ?: "Silvester...",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedChat?.username?.lowercase() ?: "silvester_mu...",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedChat()
                        navController.popBackStack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Voice call functionality
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                    IconButton(onClick = {
                        // Video call functionality
                    }) {
                        Icon(Icons.Default.Videocam, contentDescription = "Video call")
                    }
                    IconButton(onClick = {
                        // More options
                    }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            when (messagesState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error loading messages")
                    }
                }
                is Result.Success -> {
                    val messages = (messagesState as Result.Success<List<Message>>).data

                    LazyColumn(
                        state = lazyListState,
                        reverseLayout = true,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Top
                    ) {
                        items(messages.reversed()) { message ->
                            MessageBubble(message = message)
                        }
                    }

                    // Chat input bar
                    ChatInputBar(
                        onSendMessage = viewModel::sendMessage,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalAlignment = if (message.isSentByMe) Alignment.End else Alignment.Start
    ) {
        // Timestamp
        Text(
            text = "Jul 9, ${if (message.isSentByMe) "12:09 PM" else "12:09 AM"}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Message bubble
        Box(
            modifier = Modifier
                .background(
                    color = if (message.isSentByMe) Color(0xFF3797F0) else Color(0xFFF0F0F0),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = message.content,
                color = if (message.isSentByMe) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Status for sent messages
        if (message.isSentByMe) {
            Text(
                text = "Seen",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun ChatInputBar(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Camera icon
        Icon(
            imageVector = Icons.Default.Call, // Temporary - will replace with camera icon
            contentDescription = "Camera",
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Open camera */ },
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Message input
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .background(Color.LightGray.copy(alpha = 0.3f), MaterialTheme.shapes.medium)
                .clickable { /* Open full message input */ },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "Message...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Voice note icon
        Icon(
            imageVector = Icons.Default.Videocam, // Temporary - will replace with voice icon
            contentDescription = "Voice note",
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Record voice note */ },
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Image picker icon
        Icon(
            imageVector = Icons.Default.MoreVert, // Temporary - will replace with gallery icon
            contentDescription = "Add media",
            modifier = Modifier
                .size(24.dp)
                .clickable { /* Open gallery */ },
            tint = Color.Gray
        )
    }
}