

package com.sam.ayaana.presentation.screens.chat

import com.sam.ayaana.R
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.VideoCall
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.Message
import com.sam.ayaana.Utils.EmojiData
import com.sam.ayaana.domain.model.MessageType
import com.sam.ayaana.presentation.components.chat.VoiceRecorder
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
    val isSendingVoice by viewModel.isSendingVoice.collectAsState()
    val lazyListState = rememberLazyListState()
    val context = LocalContext.current

    var messageText by remember { mutableStateOf("") }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showVoiceRecorder by remember { mutableStateOf(false) }

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
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${selectedChat?.userId ?: ""}")
                        }
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Rounded.Call, contentDescription = "Call")
                    }

                    IconButton(onClick = {
                        val intent = Intent("android.media.action.VIDEO_CAMERA")
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.Rounded.VideoCall, contentDescription = "Video call")
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

                    // showVoiceRecorder
                    if (showVoiceRecorder){
                        VoiceRecorder(
                            onRecordingComplete = { audioFile ->
                                viewModel.sendVoiceMessage(audioFile)
                                showVoiceRecorder = false
                            },
                            onCancel = {
                                showVoiceRecorder = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }

                    // Show loading when sending voice message
                    if (isSendingVoice) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .background(Color.LightGray.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Sending voice message...")
                        }
                    }

                    // showEmojiPicker
                    if (showEmojiPicker) {
                        SimpleEmojiPicker(
                            onEmojiSelected = { emoji ->
                                messageText += emoji
                            },
                            onClose = { showEmojiPicker = false }
                        )
                    }

                    ChatInputBar(
                        messageText = messageText,
                        onMessageTextChange = { messageText = it },
                        onSendMessage = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                        onCameraClick = {
                            val intent = Intent("android.media.action.IMAGE_CAPTURE")
                            context.startActivity(intent)
                        },
                        onImageClick = {
                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "image/*"
                                addCategory(Intent.CATEGORY_OPENABLE)
                            }
                            context.startActivity(intent)
                        },
                        onVoiceClick = {
                            // Start voice recording
                            showVoiceRecorder = true
                        },
                        onEmojiClick = {
                            showEmojiPicker = !showEmojiPicker
                        },
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
        Text(
            text = "Jul 9, ${if (message.isSentByMe) "12:09 PM" else "12:09 AM"}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        when (message.messageType) {
            MessageType.VOICE -> {
                // Voice message UI
                VoiceMessageBubble(
                    message = message,
                    isSentByMe = message.isSentByMe
                )
            }
            else -> {
                // Text message UI
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
            }
        }

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
fun VoiceMessageBubble(
    message: Message,
    isSentByMe: Boolean
) {
    Row(
        modifier = Modifier
            .background(
                color = if (isSentByMe) Color(0xFF3797F0) else Color(0xFFF0F0F0),
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Voice message",
            tint = if (isSentByMe) Color.White else Color.Black,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Voice message",
            color = if (isSentByMe) Color.White else Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "0:15", // You can calculate actual duration
            color = if (isSentByMe) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ChatInputBar(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onCameraClick: () -> Unit,
    onImageClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onEmojiClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTyping = messageText.isNotBlank()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(if (isTyping) 0.85f else 1f)
                .height(45.dp)
                .background(
                    color = Color(0xFFF0F0F0),
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera icon - always visible
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF833AB4), CircleShape)
                        .noRippleClickable(onClick = onCameraClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Message input field
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    BasicTextField(
                        value = messageText,
                        onValueChange = onMessageTextChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(vertical = 12.dp),
                        decorationBox = { innerTextField ->
                            if (messageText.isEmpty()) {
                                Text(
                                    text = "Message...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // FIXED: Emoji icon is ALWAYS visible, voice/image hide when typing
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))

                    // Voice note icon - only visible when NOT typing
                    if (!isTyping) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF25D366), CircleShape)
                                .noRippleClickable(onClick = onVoiceClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice note",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Image picker icon - only visible when NOT typing
                    if (!isTyping) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
                                .noRippleClickable(onClick = onImageClick),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Add image",
                                modifier = Modifier.size(18.dp),
                                tint = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Emoji icon - ALWAYS visible (even when typing)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color.Transparent, CircleShape)
                            .border(
                                width = 1.dp,
                                color = Color.Gray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .noRippleClickable(onClick = onEmojiClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_emoji),
                            contentDescription = "Emoji",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }

        // Send button appears when typing
        if (isTyping) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(Color(0xFF3797F0), CircleShape)
                    .noRippleClickable(onClick = onSendMessage),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }
    }
}

//@Composable
//fun ChatInputBar(
//    messageText: String,
//    onMessageTextChange: (String) -> Unit,
//    onSendMessage: () -> Unit,
//    onCameraClick: () -> Unit,
//    onImageClick: () -> Unit,
//    onVoiceClick: () -> Unit,
//    onEmojiClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    // Instagram-style rounded container for all input elements
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(50.dp)
//            .background(
//                color = Color.LightGray.copy(alpha = 0.2f),
//                shape = MaterialTheme.shapes.large
//            )
//            .padding(horizontal = 8.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Camera icon with purple circle background (Instagram style)
//            Box(
//                modifier = Modifier
//                    .size(36.dp)
//                    .background(Color(0xFF833AB4), CircleShape)
//                    .noRippleClickable(onClick = onCameraClick),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.CameraAlt,
//                    contentDescription = "Camera",
//                    modifier = Modifier.size(18.dp),
//                    tint = Color.White
//                )
//            }
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            // Message input field - inside the rounded container
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxHeight(),
//                contentAlignment = Alignment.CenterStart
//            ) {
//                BasicTextField(
//                    value = messageText,
//                    onValueChange = onMessageTextChange,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
//                        .padding(vertical = 12.dp),
//
//                    decorationBox = { innerTextField ->
//                        if (messageText.isEmpty()) {
//                            Text(
//                                text = "Message...",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = Color.Gray
//                            )
//                        }
//                        innerTextField()
//                    }
//                )
//            }
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            // Voice note icon with green circle background
//            Box(
//                modifier = Modifier
//                    .size(36.dp)
//                    .background(Color(0xFF25D366), CircleShape)
//                    .noRippleClickable(onClick = onVoiceClick),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Mic,
//                    contentDescription = "Voice note",
//                    modifier = Modifier.size(18.dp),
//                    tint = Color.White
//                )
//            }
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            // Image picker icon with light gray circle
//            Box(
//                modifier = Modifier
//                    .size(36.dp)
//                    .background(Color.Gray.copy(alpha = 0.3f), CircleShape)
//                    .noRippleClickable(onClick = onImageClick),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Image,
//                    contentDescription = "Add image",
//                    modifier = Modifier.size(18.dp),
//                    tint = Color.Black
//                )
//            }
//
//            Spacer(modifier = Modifier.width(8.dp))
//
//            // Emoji icon with rounded square (Instagram style)
//            Box(
//                modifier = Modifier
//                    .size(36.dp)
//                    .background(Color.Transparent, CircleShape)
//                    .border(
//                        width = 1.dp,
//                        color = Color.Gray.copy(alpha = 0.5f),
//                        shape = RoundedCornerShape(8.dp)
//                    )
//                    .noRippleClickable(onClick = onEmojiClick),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_emoji),
//                    contentDescription = "Emoji",
//                    modifier = Modifier.size(20.dp),
//                    tint = Color.Unspecified
//                )
////
//            }
//
//            // Send button appears when text is entered (Instagram puts it outside the container)
//            if (messageText.isNotBlank()) {
//                Spacer(modifier = Modifier.width(12.dp))
//                Box(
//                    modifier = Modifier
//                        .size(36.dp)
//                        .background(Color(0xFF3797F0), CircleShape)
//                        .noRippleClickable(onClick = onSendMessage),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Send,
//                        contentDescription = "Send",
//                        modifier = Modifier.size(18.dp),
//                        tint = Color.White
//                    )
//                }
//            }
//        }
//    }
//}


@Composable
fun SimpleEmojiPicker(
    onEmojiSelected: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(EmojiData.categories.first()) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Emoji",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            EmojiData.categories.forEach { category ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (category == selectedCategory) Color(0xFF3797F0)
                            else Color.LightGray.copy(alpha = 0.3f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .noRippleClickable { selectedCategory = category },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = category.name,
                        tint = if (category == selectedCategory) Color.White else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Text(
            text = selectedCategory.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Fixed LazyVerticalGrid with proper imports
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(selectedCategory.emojis) { emoji ->
                Text(
                    text = emoji,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .size(40.dp)
                        .noRippleClickable { onEmojiSelected(emoji) },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Fixed clickable modifier
@Composable
private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null
) {
    onClick()
}


//package com.sam.ayaana.presentation.screens.chat
//
//import com.sam.ayaana.R
//import android.content.Intent
//import android.net.Uri
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.automirrored.filled.Send
//import androidx.compose.material.icons.filled.CameraAlt
//import androidx.compose.material.icons.filled.Mic
//import androidx.compose.material.icons.filled.Image
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.rounded.Call
//import androidx.compose.material.icons.rounded.VideoCall
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
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
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import coil.compose.AsyncImage
//import com.sam.ayaana.Utils.Result
//import com.sam.ayaana.domain.model.Message
//import com.sam.ayaana.Utils.EmojiData
//import com.sam.ayaana.domain.model.MessageType
//import com.sam.ayaana.presentation.components.chat.VoiceRecorder
//import kotlinx.coroutines.delay
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatDetailScreen(
//    navController: NavController,
//    chatId: String,
//    viewModel: ChatViewModel = hiltViewModel()
//) {
//    val selectedChat by viewModel.selectedChat.collectAsState()
//    val messagesState by viewModel.messagesState.collectAsState()
//    val isSendingVoice by viewModel.isSendingVoice.collectAsState()
//    val lazyListState = rememberLazyListState()
//    val context = LocalContext.current
//
//    var messageText by remember { mutableStateOf("") }
//    var showEmojiPicker by remember { mutableStateOf(false) }
//    var showVoiceRecorder by remember { mutableStateOf(false) }
//
//    LaunchedEffect(chatId) {
//        if (selectedChat?.id != chatId) {
//            viewModel.loadMessages(chatId)
//        }
//    }
//
//    LaunchedEffect(messagesState) {
//        if (messagesState is Result.Success) {
//            delay(100)
//            if (messagesState is Result.Success<List<Message>>) {
//                val messages = (messagesState as Result.Success<List<Message>>).data
//                if (messages.isNotEmpty()) {
//                    lazyListState.animateScrollToItem(0)
//                }
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        AsyncImage(
//                            model = selectedChat?.profileImage ?: "https://picsum.photos/id/103/200/300",
//                            contentDescription = "Profile",
//                            modifier = Modifier
//                                .size(36.dp)
//                                .clip(CircleShape)
//                        )
//                        Spacer(modifier = Modifier.width(12.dp))
//                        Column {
//                            Text(
//                                text = selectedChat?.username ?: "Silvester...",
//                                style = MaterialTheme.typography.bodyLarge,
//                                fontWeight = FontWeight.Bold,
//                                color = MaterialTheme.colorScheme.onBackground
//                            )
//                            Text(
//                                text = selectedChat?.username?.lowercase() ?: "silvester_mu...",
//                                style = MaterialTheme.typography.bodySmall,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        viewModel.clearSelectedChat()
//                        navController.popBackStack()
//                    }) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        val intent = Intent(Intent.ACTION_DIAL).apply {
//                            data = Uri.parse("tel:${selectedChat?.userId ?: ""}")
//                        }
//                        context.startActivity(intent)
//                    }) {
//                        Icon(Icons.Rounded.Call, contentDescription = "Call")
//                    }
//
//                    IconButton(onClick = {
//                        val intent = Intent("android.media.action.VIDEO_CAMERA")
//                        context.startActivity(intent)
//                    }) {
//                        Icon(Icons.Rounded.VideoCall, contentDescription = "Video call")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//            when (messagesState) {
//                is Result.Loading -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator(
//                            color = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//                is Result.Error -> {
//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            "Error loading messages",
//                            color = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//                is Result.Success -> {
//                    val messages = (messagesState as Result.Success<List<Message>>).data
//
//                    LazyColumn(
//                        state = lazyListState,
//                        reverseLayout = true,
//                        modifier = Modifier.weight(1f),
//                        verticalArrangement = Arrangement.Top
//                    ) {
//                        items(messages.reversed()) { message ->
//                            MessageBubble(message = message)
//                        }
//                    }
//
//                    // showVoiceRecorder
//                    if (showVoiceRecorder){
//                        VoiceRecorder(
//                            onRecordingComplete = { audioFile ->
//                                viewModel.sendVoiceMessage(audioFile)
//                                showVoiceRecorder = false
//                            },
//                            onCancel = {
//                                showVoiceRecorder = false
//                            },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(200.dp)
//                        )
//                    }
//
//                    // Show loading when sending voice message
//                    if (isSendingVoice) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(60.dp)
//                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                "Sending voice message...",
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                    }
//
//                    // showEmojiPicker
//                    if (showEmojiPicker) {
//                        SimpleEmojiPicker(
//                            onEmojiSelected = { emoji ->
//                                messageText += emoji
//                            },
//                            onClose = { showEmojiPicker = false }
//                        )
//                    }
//
//                    ChatInputBar(
//                        messageText = messageText,
//                        onMessageTextChange = { messageText = it },
//                        onSendMessage = {
//                            if (messageText.isNotBlank()) {
//                                viewModel.sendMessage(messageText)
//                                messageText = ""
//                            }
//                        },
//                        onCameraClick = {
//                            val intent = Intent("android.media.action.IMAGE_CAPTURE")
//                            context.startActivity(intent)
//                        },
//                        onImageClick = {
//                            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
//                                type = "image/*"
//                                addCategory(Intent.CATEGORY_OPENABLE)
//                            }
//                            context.startActivity(intent)
//                        },
//                        onVoiceClick = {
//                            // Start voice recording
//                            showVoiceRecorder = true
//                        },
//                        onEmojiClick = {
//                            showEmojiPicker = !showEmojiPicker
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun MessageBubble(
//    message: Message,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 4.dp),
//        horizontalAlignment = if (message.isSentByMe) Alignment.End else Alignment.Start
//    ) {
//        Text(
//            text = "Jul 9, ${if (message.isSentByMe) "12:09 PM" else "12:09 AM"}",
//            style = MaterialTheme.typography.bodySmall,
//            color = MaterialTheme.colorScheme.onSurfaceVariant,
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//
//        when (message.messageType) {
//            MessageType.VOICE -> {
//                // Voice message UI
//                VoiceMessageBubble(
//                    message = message,
//                    isSentByMe = message.isSentByMe
//                )
//            }
//            else -> {
//                // Text message UI
//                Box(
//                    modifier = Modifier
//                        .background(
//                            color = if (message.isSentByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
//                            shape = MaterialTheme.shapes.medium
//                        )
//                        .padding(horizontal = 16.dp, vertical = 12.dp)
//                ) {
//                    Text(
//                        text = message.content,
//                        color = if (message.isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//        }
//
//        if (message.isSentByMe) {
//            Text(
//                text = "Seen",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.onSurfaceVariant,
//                modifier = Modifier.padding(top = 2.dp)
//            )
//        }
//    }
//}
//
//@Composable
//fun VoiceMessageBubble(
//    message: Message,
//    isSentByMe: Boolean
//) {
//    Row(
//        modifier = Modifier
//            .background(
//                color = if (isSentByMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
//                shape = MaterialTheme.shapes.medium
//            )
//            .padding(horizontal = 16.dp, vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            imageVector = Icons.Default.Mic,
//            contentDescription = "Voice message",
//            tint = if (isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
//            modifier = Modifier.size(20.dp)
//        )
//
//        Spacer(modifier = Modifier.width(8.dp))
//
//        Text(
//            text = "Voice message",
//            color = if (isSentByMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
//            style = MaterialTheme.typography.bodyMedium
//        )
//
//        Spacer(modifier = Modifier.width(8.dp))
//
//        Text(
//            text = "0:15", // You can calculate actual duration
//            color = if (isSentByMe) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//            style = MaterialTheme.typography.bodySmall
//        )
//    }
//}
//
//@Composable
//fun ChatInputBar(
//    messageText: String,
//    onMessageTextChange: (String) -> Unit,
//    onSendMessage: () -> Unit,
//    onCameraClick: () -> Unit,
//    onImageClick: () -> Unit,
//    onVoiceClick: () -> Unit,
//    onEmojiClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val isTyping = messageText.isNotBlank()
//
//    Row(
//        modifier = modifier,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(
//            modifier = Modifier
//                .weight(if (isTyping) 0.85f else 1f)
//                .height(45.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.surfaceVariant,
//                    shape = RoundedCornerShape(20.dp)
//                ),
//            contentAlignment = Alignment.CenterStart
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Camera icon - always visible
//                Box(
//                    modifier = Modifier
//                        .size(36.dp)
//                        .background(MaterialTheme.colorScheme.primary, CircleShape)
//                        .noRippleClickable(onClick = onCameraClick),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.CameraAlt,
//                        contentDescription = "Camera",
//                        modifier = Modifier.size(18.dp),
//                        tint = MaterialTheme.colorScheme.onPrimary
//                    )
//                }
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                // Message input field
//                Box(
//                    modifier = Modifier
//                        .weight(1f)
//                        .fillMaxHeight(),
//                    contentAlignment = Alignment.CenterStart
//                ) {
//                    BasicTextField(
//                        value = messageText,
//                        onValueChange = onMessageTextChange,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                            .padding(vertical = 12.dp),
//                        decorationBox = { innerTextField ->
//                            if (messageText.isEmpty()) {
//                                Text(
//                                    text = "Message...",
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                                )
//                            }
//                            innerTextField()
//                        }
//                    )
//                }
//
//                // FIXED: Emoji icon is ALWAYS visible, voice/image hide when typing
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    // Voice note icon - only visible when NOT typing
//                    if (!isTyping) {
//                        Box(
//                            modifier = Modifier
//                                .size(36.dp)
//                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
//                                .noRippleClickable(onClick = onVoiceClick),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Mic,
//                                contentDescription = "Voice note",
//                                modifier = Modifier.size(18.dp),
//                                tint = MaterialTheme.colorScheme.onSecondary
//                            )
//                        }
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//
//                    // Image picker icon - only visible when NOT typing
//                    if (!isTyping) {
//                        Box(
//                            modifier = Modifier
//                                .size(36.dp)
//                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
//                                .noRippleClickable(onClick = onImageClick),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Image,
//                                contentDescription = "Add image",
//                                modifier = Modifier.size(18.dp),
//                                tint = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                        }
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//
//                    // Emoji icon - ALWAYS visible (even when typing)
//                    Box(
//                        modifier = Modifier
//                            .size(36.dp)
//                            .background(Color.Transparent, CircleShape)
//                            .border(
//                                width = 1.dp,
//                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
//                                shape = RoundedCornerShape(8.dp)
//                            )
//                            .noRippleClickable(onClick = onEmojiClick),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_emoji),
//                            contentDescription = "Emoji",
//                            modifier = Modifier.size(20.dp),
//                            tint = MaterialTheme.colorScheme.onSurfaceVariant
//                        )
//                    }
//                }
//            }
//        }
//
//        // Send button appears when typing
//        if (isTyping) {
//            Spacer(modifier = Modifier.width(12.dp))
//            Box(
//                modifier = Modifier
//                    .size(45.dp)
//                    .background(MaterialTheme.colorScheme.primary, CircleShape)
//                    .noRippleClickable(onClick = onSendMessage),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.Send,
//                    contentDescription = "Send",
//                    modifier = Modifier.size(20.dp),
//                    tint = MaterialTheme.colorScheme.onPrimary
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun SimpleEmojiPicker(
//    onEmojiSelected: (String) -> Unit,
//    onClose: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var selectedCategory by remember { mutableStateOf(EmojiData.categories.first()) }
//
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(320.dp)
//            .background(MaterialTheme.colorScheme.surface)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Emoji",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//            IconButton(onClick = onClose) {
//                Icon(
//                    Icons.Default.Close,
//                    contentDescription = "Close",
//                    tint = MaterialTheme.colorScheme.onBackground
//                )
//            }
//        }
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            EmojiData.categories.forEach { category ->
//                Box(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .background(
//                            color = if (category == selectedCategory) MaterialTheme.colorScheme.primary
//                            else MaterialTheme.colorScheme.surfaceVariant,
//                            shape = MaterialTheme.shapes.medium
//                        )
//                        .noRippleClickable { selectedCategory = category },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = category.icon,
//                        contentDescription = category.name,
//                        tint = if (category == selectedCategory) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
//        }
//
//        Text(
//            text = selectedCategory.name,
//            style = MaterialTheme.typography.bodyMedium,
//            fontWeight = FontWeight.Medium,
//            color = MaterialTheme.colorScheme.onBackground,
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//        )
//
//        // Fixed LazyVerticalGrid with proper imports
//        LazyVerticalGrid(
//            columns = GridCells.Fixed(8),
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp)
//        ) {
//            items(selectedCategory.emojis) { emoji ->
//                Text(
//                    text = emoji,
//                    fontSize = 24.sp,
//                    color = MaterialTheme.colorScheme.onBackground,
//                    modifier = Modifier
//                        .size(40.dp)
//                        .noRippleClickable { onEmojiSelected(emoji) },
//                    textAlign = TextAlign.Center
//                )
//            }
//        }
//    }
//}
//
//// Fixed clickable modifier
//@Composable
//private fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = this.clickable(
//    interactionSource = remember { MutableInteractionSource() },
//    indication = null
//) {
//    onClick()
//}
