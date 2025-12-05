package com.sam.ayaana.presentation.screens.aiassistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.sam.ayaana.domain.model.AiMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    navController: NavHostController,
    viewModel: AiAssistantViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Get initial query from navigation arguments
    val initialQuery = navController.currentBackStackEntry?.arguments?.getString("initialQuery")

    // Use initial query if provided
    LaunchedEffect(initialQuery) {
        initialQuery?.let { query ->
            if (query.isNotBlank()) {
                viewModel.sendMessage(query)
            }
        }
    }
    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch {
                scrollState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // FIXED: TopAppBar stays fixed
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ayaana",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF833AB4)
                    )
                    Text(
                        text = " AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF833AB4)
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.clearConversation() },
                    enabled = messages.size > 1
                ) {
                    Icon(Icons.Default.Delete, "Clear chat")
                }
            }
        )

        // Chat messages - FIXED: Use weight to take remaining space
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = scrollState,
            // FIXED: Add contentPadding to prevent top bar diminishing
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // FIXED: Add top padding for first item
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(messages) { index, message ->
                Box(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = if (index == messages.lastIndex && !isLoading) 0.dp else 0.dp
                    )
                ) {
                    AiMessageBubble(message)
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = if (messages.isEmpty()) 16.dp else 0.dp
                        )
                    ) {
                        AiTypingIndicator()
                    }
                }
            }
        }

        // Input field - FIXED: Send button color back to blue
        AiInputField(
            value = userInput,
            onValueChange = { userInput = it },
            onSend = {
                if (userInput.isNotBlank()) {
                    viewModel.sendMessage(userInput)
                    userInput = ""
                }
            },
            isLoading = isLoading
        )
    }
}

@Composable
fun AiMessageBubble(message: AiMessage) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF0095F6) else Color(0xFFF5F5F5)
            )
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                color = if (isUser) Color.White else Color.Black,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun AiTypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ayaana AI is typing", color = Color.Gray)
                Spacer(modifier = Modifier.width(8.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
fun AiInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask Ayaana AI...") },
                enabled = !isLoading,
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isLoading
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    // FIXED: Send button color back to blue when enabled
                    tint = if (value.isNotBlank() && !isLoading) Color(0xFF0095F6) else Color.Gray
                )
            }
        }
    }
}

//package com.sam.ayaana.presentation.screens.aiassistant
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Send
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavHostController
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AiAssistantScreen(
//    navController: NavHostController,
//    viewModel: AiAssistantViewModel = hiltViewModel()
//) {
//    val messages by viewModel.messages.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    var userInput by remember { mutableStateOf("") }
//    val scrollState = rememberLazyListState()
//    val scope = rememberCoroutineScope()
//
//    // Auto-scroll to bottom when new messages arrive
//    LaunchedEffect(messages.size) {
//        if (messages.isNotEmpty()) {
//            scope.launch {
//                scrollState.animateScrollToItem(messages.size - 1)
//            }
//        }
//    }
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Header
//        TopAppBar(
//            title = {
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Ayaana",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp,
//                        color = Color(0xFF833AB4)
//                    )
//                    Text(
//                        text = " AI",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp,
//                        color = Color.Black
//                    )
//                }
//            },
//            navigationIcon = {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(Icons.Default.ArrowBack, "Back")
//                }
//            },
//            actions = {
//                IconButton(
//                    onClick = { viewModel.clearConversation() },
//                    enabled = messages.size > 1
//                ) {
//                    Icon(Icons.Default.Delete, "Clear chat")
//                }
//            }
//        )
//
//        // Chat messages
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth(),
//            state = scrollState,
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            items(messages) { message ->
//                AiMessageBubble(message)
//            }
//
//            if (isLoading) {
//                item {
//                    AiTypingIndicator()
//                }
//            }
//        }
//
//        // Input field
//        AiInputField(
//            value = userInput,
//            onValueChange = { userInput = it },
//            onSend = {
//                if (userInput.isNotBlank()) {
//                    viewModel.sendMessage(userInput)
//                    userInput = ""
//                }
//            },
//            isLoading = isLoading
//        )
//    }
//}
//
//@Composable
//fun AiMessageBubble(message: com.sam.ayaana.domain.model.AiMessage) {
//    val isUser = message.isFromUser
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
//    ) {
//        Card(
//            modifier = Modifier
//                .widthIn(max = 280.dp),
//            shape = RoundedCornerShape(
//                topStart = 16.dp,
//                topEnd = 16.dp,
//                bottomStart = if (isUser) 16.dp else 4.dp,
//                bottomEnd = if (isUser) 4.dp else 16.dp
//            ),
//            colors = CardDefaults.cardColors(
//                containerColor = if (isUser) Color(0xFF0095F6) else Color(0xFFF5F5F5)
//            )
//        ) {
//            Text(
//                text = message.content,
//                modifier = Modifier.padding(12.dp),
//                color = if (isUser) Color.White else Color.Black,
//                fontSize = 15.sp
//            )
//        }
//    }
//}
//
//@Composable
//fun AiTypingIndicator() {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Start
//    ) {
//        Card(
//            colors = CardDefaults.cardColors(
//                containerColor = Color(0xFFF5F5F5)
//            ),
//            shape = RoundedCornerShape(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text("Ayaana AI is typing", color = Color.Gray)
//                Spacer(modifier = Modifier.width(8.dp))
//                CircularProgressIndicator(
//                    modifier = Modifier.size(16.dp),
//                    strokeWidth = 2.dp
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun AiInputField(
//    value: String,
//    onValueChange: (String) -> Unit,
//    onSend: () -> Unit,
//    isLoading: Boolean
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        OutlinedTextField(
//            value = value,
//            onValueChange = onValueChange,
//            modifier = Modifier.weight(1f),
//            placeholder = { Text("Ask Ayaana AI...") },
//            enabled = !isLoading,
//            singleLine = false,
//            maxLines = 3,
//            shape = RoundedCornerShape(24.dp)
//        )
//
//        Spacer(modifier = Modifier.width(8.dp))
//
//        IconButton(
//            onClick = onSend,
//            enabled = value.isNotBlank() && !isLoading
//        ) {
//            Icon(
//                Icons.Default.Send,
//                contentDescription = "Send",
//                tint = if (value.isNotBlank() && !isLoading) Color(0xFF0095F6) else Color.Gray
//            )
//        }
//    }
//}