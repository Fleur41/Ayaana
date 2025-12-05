package com.sam.ayaana.presentation.screens.chat

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.sam.ayaana.R
import com.sam.ayaana.Utils.AIAssistantDetector
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.AiMessage
import com.sam.ayaana.domain.model.Chat
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel(),
    isAiMode: Boolean = false
) {
    val chatsState by viewModel.chatsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredChats by viewModel.filteredChats.collectAsState()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
                            contentDescription = "Home",
                            tint = if (currentRoute == "home") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
                            contentDescription = "Reels",
                            tint = if (currentRoute == "reels") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "reels",
                    onClick = { navController.navigate("reels") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
                            contentDescription = "Chat",
                            tint = if (currentRoute == "chat") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "chat",
                    onClick = { navController.navigate("chat") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
                            contentDescription = "Search",
                            tint = if (currentRoute == "search") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "search",
                    onClick = { navController.navigate("search") }
                )

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
                            contentDescription = "Profile",
                            tint = if (currentRoute == "profile") Color.White else Color.Gray
                        )
                    },
                    label = { Text(text = "", fontSize = 0.sp) },
                    selected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Show AI header when in AI mode
            if (isAiMode) {
                AIChatHeader(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // FIXED: Custom SearchBar (not Material3 SearchBar)
            CustomSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onClearClick = {
                    if (isAiMode) {
                        viewModel.clearAiConversation()
                    }
                    viewModel.clearSearch()
                },
                onSearch = { query ->
                    if (AIAssistantDetector.shouldNavigateToAi(query)) {
                        // Navigate to AiAssistantScreen for AI queries
                        navController.navigate("ai_assistant?initialQuery=${Uri.encode(query)}")
                        viewModel.updateSearchQuery("")
                    } else {
                        // Handle normal search for users
                        viewModel.triggerSearch()
                    }
                },
                isAiMode = isAiMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            if (isAiMode) {
                AIConversation(viewModel = viewModel)
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Messages",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ChatList handles ALL chat display logic
                    ChatListView(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

// FIXED: Renamed from SearchBar to CustomSearchBar to avoid conflict with Material3 SearchBar
@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearClick: () -> Unit,
    isAiMode: Boolean = false,
) {
    val focusRequester = remember { FocusRequester() }

    // Auto-focus search bar when in AI mode
    LaunchedEffect(isAiMode) {
        if (isAiMode) {
            delay(300)
            focusRequester.requestFocus()
        }
    }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            AyaanaAIIcon(
                isRotating = true,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    modifier = Modifier.clickable(onClick = onClearClick)
                )
            } else if (isAiMode) {
                // AI mode indicator
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Mode",
                    tint = Color(0xFF833AB4),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        placeholder = {
            Text(
                text = if (isAiMode) {
                    "Ask Ayaana AI anything..."
                } else {
                    "Ask Ayaana AI or Search users..."
                }
            )
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Color.Black,
            fontSize = 16.sp
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color(0xFF833AB4),
            focusedLeadingIconColor = Color.Gray,
            unfocusedLeadingIconColor = Color.Gray,
            focusedTrailingIconColor = Color.Gray,
            unfocusedTrailingIconColor = Color.Gray
        ),
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Composable
fun ChatListItem(
    chat: Chat,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chat.profileImage,
            contentDescription = "Profile image",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = chat.username,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = chat.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Text(
            text = "2d",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

// FIXED: Renamed from AiAssistantHeader to AIChatHeader
@Composable
fun AIChatHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Ayaana AI Assistant",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        // AI status indicator
        Icon(
            imageVector = Icons.Default.Circle,
            contentDescription = "AI Online",
            tint = Color.Green,
            modifier = Modifier.size(8.dp)
        )
    }
}

@Composable
fun AyaanaAIIcon(
    modifier: Modifier = Modifier,
    isRotating: Boolean = true
) {
    // Animation 1: Circular rotation
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    // Animation 2: Pulsing effect for the center
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Ayaana gradient colors
    val ayaanaGradient = listOf(
        Color(0xFF833AB4),
        Color(0xFFC13584),
        Color(0xFFE1306C),
        Color(0xFFFD1D1D),
        Color(0xFFF56040),
        Color(0xFFF77737),
        Color(0xFFFCAF45),
        Color(0xFFFFDC80),
        Color(0xFF833AB4)
    )

    Box(
        modifier = modifier
            .size(24.dp)
            .graphicsLayer {
                rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        // Outer rotating gradient ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sweepGradient = Brush.sweepGradient(
                colors = ayaanaGradient,
                center = Offset(size.width / 2, size.height / 2)
            )

            // Draw the main gradient ring
            drawCircle(
                brush = sweepGradient,
                radius = size.minDimension / 2 - 1.dp.toPx(),
                style = Stroke(width = 2.5.dp.toPx())
            )

            // Optional: Add a subtle glow effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF833AB4).copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2, size.height / 2),
                    radius = size.minDimension / 2
                ),
                radius = size.minDimension / 2
            )
        }

        // Center with "Ay" text with pulsing animation
        Box(
            modifier = Modifier
                .size((14 * pulse).dp)
                .background(Color.White, CircleShape)
                .border(0.5.dp, Color.LightGray.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ay",
                color = Color(0xFF833AB4),
                fontSize = 6.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp
            )
        }
    }
}

// FIXED: Renamed from AiConversation to AIConversation
@Composable
fun AIConversation(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isAiResponding by viewModel.isAiResponding.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 16.dp)
    ) {
        items(aiMessages) { message ->
            AIMessageBubble(message = message)
        }

        if (isAiResponding) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ayaana AI is thinking...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

// FIXED: Renamed from AiMessageBubble to AIMessageBubble
@Composable
fun AIMessageBubble(
    message: AiMessage
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                    )
                )
                .background(
                    color = if (message.isFromUser) Color(0xFF0095F6) else Color.LightGray.copy(
                        alpha = 0.2f
                    )
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (message.isFromUser) Color.White else Color.Black
            )
        }

        // Timestamp
        Text(
            text = if (message.isFromUser) "You" else "Ayaana AI",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

// FIXED: Renamed from ChatList to ChatListView
@Composable
fun ChatListView(
    viewModel: ChatViewModel = hiltViewModel(),
    navController: NavController? = null
) {
    val chatsState by viewModel.chatsState.collectAsState()
    val filteredChats by viewModel.filteredChats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            searchQuery.isNotEmpty() && filteredChats.isEmpty() -> {
                EmptySearchView(query = searchQuery)
            }

            searchQuery.isNotEmpty() -> {
                navController?.let { nav ->
                    FilteredChatsView(
                        chats = filteredChats,
                        viewModel = viewModel,
                        navController = nav
                    )
                } ?: run {
                    FilteredChatsViewWithoutNav(
                        chats = filteredChats,
                        viewModel = viewModel
                    )
                }
            }

            else -> {
                when (chatsState) {
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
                            Text("Error loading chats")
                        }
                    }

                    is Result.Success -> {
                        val allChats = (chatsState as Result.Success<List<Chat>>).data
                        navController?.let { nav ->
                            AllChatsView(
                                chats = allChats,
                                viewModel = viewModel,
                                navController = nav
                            )
                        } ?: run {
                            AllChatsViewWithoutNav(
                                chats = allChats,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilteredChatsViewWithoutNav(
    chats: List<Chat>,
    viewModel: ChatViewModel
) {
    LazyColumn {
        if (chats.isEmpty()) {
            item {
                EmptySearchView(query = "")
            }
        } else {
            items(chats) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = {
                        viewModel.selectChat(chat)
                        println("Chat selected: ${chat.username}, but no navController available")
                    }
                )
            }
        }
    }
}

@Composable
fun AllChatsViewWithoutNav(
    chats: List<Chat>,
    viewModel: ChatViewModel
) {
    LazyColumn {
        items(chats) { chat ->
            ChatListItem(
                chat = chat,
                onClick = {
                    viewModel.selectChat(chat)
                    println("Chat selected: ${chat.username}, but no navController available")
                }
            )
        }
    }
}

@Composable
fun AllChatsView(
    chats: List<Chat>,
    viewModel: ChatViewModel,
    navController: NavController
) {
    LazyColumn {
        items(chats) { chat ->
            ChatListItem(
                chat = chat,
                onClick = {
                    viewModel.selectChat(chat)
                    navController.navigate("chat_detail/${chat.id}")
                }
            )
        }
    }
}

@Composable
fun FilteredChatsView(
    chats: List<Chat>,
    viewModel: ChatViewModel,
    navController: NavController
) {
    LazyColumn {
        if (chats.isEmpty()) {
            item {
                EmptySearchView(query = "")
            }
        } else {
            items(chats) { chat ->
                ChatListItem(
                    chat = chat,
                    onClick = {
                        viewModel.selectChat(chat)
                        navController.navigate("chat_detail/${chat.id}")
                    }
                )
            }
        }
    }
}

// FIXED: Renamed from EmptySearchResults to EmptySearchView
@Composable
fun EmptySearchView(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = "No results",
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (query.isNotEmpty()) {
                    "No results found for \"$query\""
                } else {
                    "No chats found"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
//package com.sam.ayaana.presentation.screens.chat

//
//import androidx.compose.animation.core.LinearEasing
//import androidx.compose.animation.core.RepeatMode
//import androidx.compose.animation.core.animateFloat
//import androidx.compose.animation.core.infiniteRepeatable
//import androidx.compose.animation.core.rememberInfiniteTransition
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
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
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.AutoAwesome
//import androidx.compose.material.icons.filled.Circle
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.SearchOff
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import coil.compose.AsyncImage
//import com.sam.ayaana.R
//import com.sam.ayaana.Utils.Result
//import com.sam.ayaana.domain.model.AiMessage
//import com.sam.ayaana.domain.model.Chat
//import kotlinx.coroutines.delay
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatListScreen(
//    navController: NavController,
//    viewModel: ChatViewModel = hiltViewModel(),
//    isAiMode: Boolean = false
//) {
//    val chatsState by viewModel.chatsState.collectAsState()
//    val searchQuery by viewModel.searchQuery.collectAsState()
//    val filteredChats by viewModel.filteredChats.collectAsState()
//
//    val hasSearchResults = searchQuery.isNotEmpty() && filteredChats.isEmpty()
//    val searchResultMessage = if (searchQuery.isNotEmpty() && filteredChats.isEmpty()) {
//        "No results found for \"$searchQuery\""
//    } else {
//        ""
//    }
//
//
//    println("🟡 DEBUG: ChatListScreen recomposed, chatsState: $chatsState")
//    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar(
//                containerColor = Color.Black,
//                contentColor = Color.White
//            ) {
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "home") R.drawable.ic_home_filled else R.drawable.ic_home_outlined),
//                            contentDescription = "Home",
//                            tint = if (currentRoute == "home") Color.White else Color.Gray
//                        )
//                    },
//                    label = { Text(text = "", fontSize = 0.sp) },
//                    selected = currentRoute == "home",
//                    onClick = { navController.navigate("home") }
//                )
//
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "reels") R.drawable.ic_reels_filled else R.drawable.ic_reels_outlined),
//                            contentDescription = "Reels",
//                            tint = if (currentRoute == "reels") Color.White else Color.Gray
//                        )
//                    },
//                    label = { Text(text = "", fontSize = 0.sp) },
//                    selected = currentRoute == "reels",
//                    onClick = { navController.navigate("reels") }
//                )
//
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "chat") R.drawable.ic_chat_filled else R.drawable.ic_chat_outlined),
//                            contentDescription = "Chat",
//                            tint = if (currentRoute == "chat") Color.White else Color.Gray
//                        )
//                    },
//                    label = { Text(text = "", fontSize = 0.sp) },
//                    selected = currentRoute == "chat",
//                    onClick = { navController.navigate("chat") }
//                )
//
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "search") R.drawable.ic_search_filled else R.drawable.ic_search_outlined),
//                            contentDescription = "Search",
//                            tint = if (currentRoute == "search") Color.White else Color.Gray
//                        )
//                    },
//                    label = { Text(text = "", fontSize = 0.sp) },
//                    selected = currentRoute == "search",
//                    onClick = { navController.navigate("search") }
//                )
//
//                NavigationBarItem(
//                    icon = {
//                        Icon(
//                            painter = painterResource(id = if (currentRoute == "profile") R.drawable.ic_profile_filled else R.drawable.ic_profile_outlined),
//                            contentDescription = "Profile",
//                            tint = if (currentRoute == "profile") Color.White else Color.Gray
//                        )
//                    },
//                    label = { Text(text = "", fontSize = 0.sp) },
//                    selected = currentRoute == "profile",
//                    onClick = { navController.navigate("profile") }
//                )
//            }
//        }
//    )
//    { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(Color.White)
//        ) {
//            // Show AI header when in AI mode
//            if (isAiMode) {
//                AiAssistantHeader(
//                    onBackClick = { navController.popBackStack() }
//                )
//            }
//
//            SearchBar(
//                query = viewModel.searchQuery.collectAsState().value,
//                onQueryChange = viewModel::updateSearchQuery,
//                onClearClick = {
//                    if (isAiMode) {
//                        viewModel.clearAiConversation()
//                    }
//                    viewModel.clearSearch()
//                },
//                onSearch = {
//                    if (isAiMode) {
//                        if (searchQuery.isNotEmpty()) {
//                            viewModel.sendAiQuery(searchQuery)
//                            viewModel.updateSearchQuery("")
//                        }
//                    } else {
//                        viewModel.triggerSearch()
//                    }
//                },
//                isAiMode = isAiMode,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp)
//            )
//
//            if (isAiMode) {
//                AiConversation(viewModel = viewModel)
//                Spacer(modifier = Modifier.height(16.dp))
//            } else {
//                Column {
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    Text(
//                        text = "Messages",
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.padding(horizontal = 16.dp)
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // ChatList handles ALL chat display logic
//                    ChatList(
//                        navController = navController,
//                        viewModel = viewModel
//                    )
//                }
//            }
//        }
//    }
//}
//
////    { paddingValues ->
////        Column(
////            modifier = Modifier
////                .fillMaxSize()
////                .padding(paddingValues)
////                .background(Color.White)
////        ) {
////            // Show AI header when in AI mode
////            if (isAiMode) {
////                AiAssistantHeader(
////                    onBackClick = { navController.popBackStack() }
////                )
////            }
////
////            SearchBar(
////                query = searchQuery,
////                onQueryChange = viewModel::updateSearchQuery,
////                onClearClick = {
////                    if (isAiMode){
////                        viewModel.clearAiConversation()
////                    }
////                    viewModel.clearSearch()
////                },
//////                onClearClick = viewModel::clearSearch,
////                onSearch = {
////                    if (isAiMode){
////                        if(searchQuery.isNotEmpty()) {
////                            viewModel.sendAiQuery(searchQuery)
////                            viewModel.updateSearchQuery("")
////                        }
////                    }else{
////                        viewModel.triggerSearch()
////                    }
////                },
////                // onSearch = viewModel::triggerSearch,
////                isAiMode = isAiMode,
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .padding(16.dp)
////            )
////            if (isAiMode){
////                AiConversation(viewModel = viewModel)
////                Spacer(modifier = Modifier.height(8.dp))
////            } else{
////                ChatList(
////                    navController = navController,
////                    viewModel = viewModel
////                )
////            }
////
////            Spacer(modifier = Modifier.height(8.dp))
////
////            Text(
////                text = "Messages",
////                style = MaterialTheme.typography.titleMedium,
////                modifier = Modifier.padding(horizontal = 16.dp)
////            )
////
////            Spacer(modifier = Modifier.height(8.dp))
////
////            // SIMPLIFIED LOGIC: Always use filteredChats when there's a search query
////            when {
////                // Show search results or "no results" message
////                searchQuery.isNotEmpty() -> {
////                    if (filteredChats.isNotEmpty()) {
////                        // Show filtered results
////                        println("✅ DEBUG: Showing ${filteredChats.size} filtered chats for '$searchQuery'")
////                        LazyColumn {
////                            items(filteredChats) { chat ->
////                                println("✅ DEBUG: Showing chat: ${chat.username}")
////                                ChatListItem(
////                                    chat = chat,
////                                    onClick = {
////                                        viewModel.selectChat(chat)
////                                        navController.navigate("chat_detail/${chat.id}")
////                                    }
////                                )
////                            }
////                        }
////                    } else {
////                        Box(
////                            modifier = Modifier
////                                .fillMaxSize()
////                                .weight(1f),
////                            contentAlignment = Alignment.Center
////                        ) {
////                            Column(
////                                horizontalAlignment = Alignment.CenterHorizontally,
////                                verticalArrangement = Arrangement.Center
////                            ) {
////                                Icon(
////                                    imageVector = Icons.Default.SearchOff,
////                                    contentDescription = "No results",
////                                    tint = Color.Gray,
////                                    modifier = Modifier.size(48.dp)
////                                )
////                                Spacer(modifier = Modifier.height(8.dp))
////                                Text(
////                                    text = "No results found for \"$searchQuery\"",
////                                    style = MaterialTheme.typography.bodyMedium,
////                                    color = Color.Gray
////                                )
////                            }
////                        }
////                    }
////                }
////
////                // Show initial chat list (no search)
////                else -> {
////                    when (chatsState) {
////                        is Result.Loading -> {
////                            Box(
////                                modifier = Modifier.fillMaxSize(),
////                                contentAlignment = Alignment.Center
////                            ) {
////                                CircularProgressIndicator()
////                            }
////                        }
////                        is Result.Error -> {
////                            Box(
////                                modifier = Modifier.fillMaxSize(),
////                                contentAlignment = Alignment.Center
////                            ) {
////                                Text("Error loading chats")
////                            }
////                        }
////                        is Result.Success -> {
////                            val allChats = (chatsState as Result.Success<List<Chat>>).data
////                            LazyColumn {
////                                items(allChats) { chat ->
////                                    ChatListItem(
////                                        chat = chat,
////                                        onClick = {
////                                            viewModel.selectChat(chat)
////                                            navController.navigate("chat_detail/${chat.id}")
////                                        }
////                                    )
////                                }
////                            }
////                        }
////                    }
////                }
////            }
////        }
////    }
////}
//
//@Composable
//fun SearchBar(
//    modifier: Modifier = Modifier,
//    query: String,
//    onQueryChange: (String) -> Unit,
//    onSearch: () -> Unit,
//    onClearClick: () -> Unit,
//    isAiMode: Boolean = false,
//) {
//
//    val focusRequester = remember { FocusRequester() }
//
//
//    // Auto-focus search bar when in AI mode
//    LaunchedEffect(isAiMode) {
//        if (isAiMode) {
//            delay(300)
//            focusRequester.requestFocus()
//        }
//    }
//    TextField(
//        value = query,
//        onValueChange = onQueryChange,
//        leadingIcon = {
//            AyaanaAIIcon(
//                isRotating = isAiMode,
//                modifier = Modifier.size(24.dp)
//            )
//        },
//        trailingIcon = {
//            if (query.isNotEmpty()){
//                Icon(
//                    imageVector = Icons.Default.Close,
//                    contentDescription = "Clear",
//                    modifier = Modifier
//                        .clickable(onClick = onClearClick)
//                )
//            } else if (isAiMode) {
//                // AI mode indicator
//                Icon(
//                    imageVector = Icons.Default.AutoAwesome,
//                    contentDescription = "AI Mode",
//                    tint = Color(0xFF833AB4),
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//        },
//        placeholder = {
//            Text(
//                text = if (isAiMode) {
//                    "Ask Ayaana AI anything..."
//                } else {
//                    "Ask Ayaana AI or Search"
//                }
//            )
//
//        },
//        textStyle = MaterialTheme.typography.bodyMedium.copy(
//            color = Color.Black,
//            fontSize = 16.sp
//        ),
//        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
//        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
//        colors = TextFieldDefaults.colors(
//            focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
//            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
//            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
//            focusedIndicatorColor = Color.Transparent,
//            unfocusedIndicatorColor = Color.Transparent,
//            focusedTextColor = Color.Black,
//            unfocusedTextColor = Color.Black,
//            cursorColor = Color(0xFF833AB4),
//            focusedLeadingIconColor = Color.Gray,
//            unfocusedLeadingIconColor = Color.Gray,
//            focusedTrailingIconColor = Color.Gray,
//            unfocusedTrailingIconColor = Color.Gray
//        ),
//        shape = MaterialTheme.shapes.medium,
//        singleLine = true,
//        modifier = modifier
//            .fillMaxWidth()
//            .height(56.dp)
//    )
//}
//
//@Composable
//fun ChatListItem(
//    chat: Chat,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .clickable(onClick = onClick)
//            .padding(horizontal = 16.dp, vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        AsyncImage(
//            model = chat.profileImage,
//            contentDescription = "Profile image",
//            modifier = Modifier
//                .size(56.dp)
//                .clip(CircleShape)
//        )
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Column(
//            modifier = Modifier.weight(1f)
//        ) {
//            Text(
//                text = chat.username,
//                style = MaterialTheme.typography.bodyLarge
//            )
//            Text(
//                text = chat.lastMessage,
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.Gray
//            )
//        }
//
//        Text(
//            text = "2d",
//            style = MaterialTheme.typography.bodySmall,
//            color = Color.Gray
//        )
//    }
//}
//
//
//@Composable
//fun AiAssistantHeader(
//    onBackClick: () -> Unit
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconButton(onClick = onBackClick) {
//            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
//        }
//
//        Spacer(modifier = Modifier.width(12.dp))
//
//        Text(
//            text = "Ayaana AI Assistant",
//            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // AI status indicator
//        Icon(
//            imageVector = Icons.Default.Circle,
//            contentDescription = "AI Online",
//            tint = Color.Green,
//            modifier = Modifier.size(8.dp)
//        )
//    }
//}
////@Composable
////fun AyaanaAIIcon(
////    modifier: Modifier = Modifier,
////    isRotating: Boolean = true
////) {
////    val infiniteTransition = rememberInfiniteTransition()
////
////    // Animation 1: Circular rotation (existing)
////    val rotation by infiniteTransition.animateFloat(
////        initialValue = 0f,
////        targetValue = 360f,
////        animationSpec = infiniteRepeatable(
////            animation = tween(3000, easing = LinearEasing),
////            repeatMode = RepeatMode.Restart
////        )
////    )
////
////    // Animation 2: Vertical sweep through text (new)
////    val verticalSweep by infiniteTransition.animateFloat(
////        initialValue = 0f,
////        targetValue = 1f,
////        animationSpec = infiniteRepeatable(
////            animation = tween(1500, easing = LinearEasing), // Half the time of rotation
////            repeatMode = RepeatMode.Reverse // Goes 0→1 then 1→0
////        )
////    )
////
////    // Ayaana gradient colors
////    val ayaanaGradient = listOf(
////        Color(0xFF833AB4), // Purple
////        Color(0xFFC13584), // Pink
////        Color(0xFFE1306C), // Red-pink
////        Color(0xFFFD1D1D), // Red
////        Color(0xFFF56040), // Orange
////        Color(0xFFF77737), // Light orange
////        Color(0xFFFCAF45), // Yellow
////        Color(0xFFFFDC80), // Light yellow
////        Color(0xFF833AB4)  // Back to purple
////    )
////
////    Box(
////        modifier = modifier
////            .size(24.dp)
////            .graphicsLayer {
////                if (isRotating) {
////                    rotationZ = rotation
////                }
////            },
////        contentAlignment = Alignment.Center
////    ) {
////        // Outer rotating gradient ring
////        Canvas(modifier = Modifier.fillMaxSize()) {
////            val sweepGradient = Brush.sweepGradient(
////                colors = ayaanaGradient,
////                center = Offset(size.width / 2, size.height / 2)
////            )
////
////            // Draw the main gradient ring
////            drawCircle(
////                brush = sweepGradient,
////                radius = size.minDimension / 2 - 1.dp.toPx(),
////                style = Stroke(width = 2.5.dp.toPx())
////            )
////
////            // Optional: Add a subtle glow effect
////            drawCircle(
////                brush = Brush.radialGradient(
////                    colors = listOf(
////                        Color(0xFF833AB4).copy(alpha = 0.2f),
////                        Color.Transparent
////                    ),
////                    center = Offset(size.width / 2, size.height / 2),
////                    radius = size.minDimension / 2
////                ),
////                radius = size.minDimension / 2
////            )
////        }
////
////        // Center with "Ay" text that has vertical sweep animation
////        Box(
////            modifier = Modifier
////                .size(14.dp),
////            contentAlignment = Alignment.Center
////        ) {
////            // Background for the text (static white)
////            Box(
////                modifier = Modifier
////                    .fillMaxSize()
////                    .background(Color.White, CircleShape)
////                    .border(0.5.dp, Color.LightGray.copy(alpha = 0.2f), CircleShape)
////            )
////
////            // Text with vertical gradient sweep effect
////            Canvas(modifier = Modifier.fillMaxSize()) {
////                // Create a vertical gradient that moves up and down
////                val verticalGradient = Brush.verticalGradient(
////                    colors = listOf(
////                        Color.Transparent,
////                        Color(0xFF833AB4).copy(alpha = 0.8f), // Purple highlight
////                        Color(0xFFFCAF45).copy(alpha = 0.8f), // Yellow highlight
////                        Color.Transparent
////                    ),
////                    startY = size.height * verticalSweep - size.height * 0.5f,
////                    endY = size.height * verticalSweep + size.height * 0.5f
////                )
////
////                // Create a mask for the text shape
////                drawContext.canvas.nativeCanvas.apply {
////                    saveLayer(
////                        android.graphics.RectF(0f, 0f, size.width, size.height),
////                        null
////                    )
////
////                    // Draw text in black (this will be masked)
////                    drawIntoCanvas { canvas ->
////                        val paint = android.graphics.Paint().apply {
////                            color = android.graphics.Color.BLACK
////                            textSize = 10.sp.toPx() // Slightly larger for mask
////                            textAlign = android.graphics.Paint.Align.CENTER
////                            isFakeBoldText = true
////                        }
////
////                        val x = size.width / 2
////                        val y = size.height / 2 - (paint.ascent() + paint.descent()) / 2
////
////                        canvas.nativeCanvas.drawText("Ay", x, y, paint)
////                    }
////
////                    // Apply the vertical gradient as source (DST_IN compositing)
////                    drawRect(
////                        brush = verticalGradient,
////                        blendMode = BlendMode.SrcIn
////                    )
////
////                    restore()
////                }
////            }
////        }
////    }
////}
//
//
//@Composable
//fun AyaanaAIIcon(
//    modifier: Modifier = Modifier,
//    isRotating: Boolean = true
//) {
//    // Animation 1: Circular rotation
//    val infiniteTransition = rememberInfiniteTransition()
//    val rotation by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 360f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(3000, easing = LinearEasing), // 3 seconds for full rotation
//            repeatMode = RepeatMode.Restart
//        )
//    )
//
//    // Animation 2: Vertical sweep through text
//    val verticalSweep by infiniteTransition.animateFloat(
//        initialValue = 0f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(1500, easing = LinearEasing), // Half the time of rotation
//            repeatMode = RepeatMode.Reverse // Goes 0→1 then 1→0
//        )
//    )
//
//
//    // Ayaana gradient colors - you can customize these
//    val ayaanaGradient = listOf(
//        Color(0xFF833AB4), // Purple (Instagram style)
//        Color(0xFFC13584), // Pink
//        Color(0xFFE1306C), // Red-pink
//        Color(0xFFFD1D1D), // Red
//        Color(0xFFF56040), // Orange
//        Color(0xFFF77737), // Light orange
//        Color(0xFFFCAF45), // Yellow
//        Color(0xFFFFDC80), // Light yellow
//        Color(0xFF833AB4)  // Back to purple
//    )
//
//    Box(
//        modifier = modifier
//            .size(24.dp)
//            .graphicsLayer {
//                if (isRotating) {
//                    rotationZ = rotation
//                }
//            },
//        contentAlignment = Alignment.Center
//    ) {
//        // Outer rotating gradient ring
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            val sweepGradient = Brush.sweepGradient(
//                colors = ayaanaGradient,
//                center = Offset(size.width / 2, size.height / 2)
//            )
//
//            // Draw the main gradient ring
//            drawCircle(
//                brush = sweepGradient,
//                radius = size.minDimension / 2 - 1.dp.toPx(),
//                style = Stroke(width = 2.5.dp.toPx())
//            )
//
//            // Optional: Add a subtle glow effect
//            drawCircle(
//                brush = Brush.radialGradient(
//                    colors = listOf(
//                        Color(0xFF833AB4).copy(alpha = 0.2f),
//                        Color.Transparent
//                    ),
//                    center = Offset(size.width / 2, size.height / 2),
//                    radius = size.minDimension / 2
//                ),
//                radius = size.minDimension / 2
//            )
//        }
//
//        // Center with "Ay" text (Ayaana)
//        Box(
//            modifier = Modifier
//                .size(14.dp)
//                .background(Color.White, CircleShape)
//                .border(0.5.dp, Color.LightGray.copy(alpha = 0.2f), CircleShape),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = "Ay",
//                color = Color(0xFF833AB4),
//                fontSize = 6.sp,
//                fontWeight = FontWeight.ExtraBold,
//                letterSpacing = (-0.3).sp
//            )
//        }
//    }
//}
//
//
//// AiConversation
//@Composable
//fun AiConversation(
//    viewModel: ChatViewModel = hiltViewModel()
//) {
//    val aiMessages by viewModel.aiMessages.collectAsState()
//    val isAiResponding by viewModel.isAiResponding.collectAsState()
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp) // Or use weight for dynamic height
//            .padding(horizontal = 16.dp)
//    ) {
//        items(aiMessages) { message ->
//            AiMessageBubble(message = message)
//        }
//
//        if (isAiResponding) {
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 8.dp),
//                    contentAlignment = Alignment.CenterStart
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(16.dp),
//                            strokeWidth = 2.dp
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text(
//                            text = "Ayaana AI is thinking...",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = Color.Gray
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AiMessageBubble(
//    message: AiMessage
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
//    ) {
//        Box(
//            modifier = Modifier
//                .clip(
//                    RoundedCornerShape(
//                        topStart = 16.dp,
//                        topEnd = 16.dp,
//                        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
//                        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
//                    )
//                )
//                .background(
//                    color = if (message.isFromUser) Color(0xFF0095F6) else Color.LightGray.copy(
//                        alpha = 0.2f
//                    )
//                )
//                .padding(horizontal = 12.dp, vertical = 8.dp)
//        ) {
//            Text(
//                text = message.content,
//                style = MaterialTheme.typography.bodyMedium,
//                color = if (message.isFromUser) Color.White else Color.Black
//            )
//        }
//
//        // Timestamp
//        Text(
//            text = if (message.isFromUser) "You" else "Ayaana AI",
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.Gray,
//            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
//        )
//    }
//}
//
//@Composable
//fun ChatList(
//    viewModel: ChatViewModel = hiltViewModel(),
//    navController: NavController? = null // ADD optional navController
//) {
//    val chatsState by viewModel.chatsState.collectAsState()
//    val filteredChats by viewModel.filteredChats.collectAsState()
//    val searchQuery by viewModel.searchQuery.collectAsState()
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        when {
//            searchQuery.isNotEmpty() && filteredChats.isEmpty() -> {
//                EmptySearchResults(query = searchQuery)
//            }
//
//            searchQuery.isNotEmpty() -> {
//                // Check if navController is available
//                navController?.let { nav ->
//                    FilteredChatList(
//                        chats = filteredChats,
//                        viewModel = viewModel,
//                        navController = nav // PASS navController
//                    )
//                } ?: run {
//                    // Fallback if navController is null
//                    FilteredChatListWithoutNav(
//                        chats = filteredChats,
//                        viewModel = viewModel
//                    )
//                }
//            }
//
//            else -> {
//                when (chatsState) {
//                    is Result.Loading -> {
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator()
//                        }
//                    }
//
//                    is Result.Error -> {
//                        Box(
//                            modifier = Modifier.fillMaxSize(),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("Error loading chats")
//                        }
//                    }
//
//                    is Result.Success -> {
//                        val allChats = (chatsState as Result.Success<List<Chat>>).data
//                        // Check if navController is available
//                        navController?.let { nav ->
//                            ChatListContent(
//                                chats = allChats,
//                                viewModel = viewModel,
//                                navController = nav // PASS navController
//                            )
//                        } ?: run {
//                            // Fallback if navController is null
//                            ChatListContentWithoutNav(
//                                chats = allChats,
//                                viewModel = viewModel
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun FilteredChatListWithoutNav(
//    chats: List<Chat>,
//    viewModel: ChatViewModel
//) {
//    LazyColumn {
//        if (chats.isEmpty()) {
//            item {
//                EmptySearchResults(query = "")
//            }
//        } else {
//            items(chats) { chat ->
//                ChatListItem(
//                    chat = chat,
//                    onClick = {
//                        viewModel.selectChat(chat)
//                        // Just select chat, no navigation
//                        println("Chat selected: ${chat.username}, but no navController available")
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun ChatListContentWithoutNav(
//    chats: List<Chat>,
//    viewModel: ChatViewModel
//) {
//    LazyColumn {
//        items(chats) { chat ->
//            ChatListItem(
//                chat = chat,
//                onClick = {
//                    viewModel.selectChat(chat)
//                    // Just select chat, no navigation
//                    println("Chat selected: ${chat.username}, but no navController available")
//                }
//            )
//        }
//    }
//}
////@Composable
////fun ChatList(
////    viewModel: ChatViewModel = hiltViewModel(),
////) {
////    val chatsState by viewModel.chatsState.collectAsState()
////    val filteredChats by viewModel.filteredChats.collectAsState()
////    val searchQuery by viewModel.searchQuery.collectAsState()
////
////    Box(modifier = Modifier.fillMaxSize()) {
////        when {
////            searchQuery.isNotEmpty() && filteredChats.isEmpty() -> {
////                EmptySearchResults(query = searchQuery)
////            }
////
////            searchQuery.isNotEmpty() -> {
////                FilteredChatList(chats = filteredChats, viewModel = viewModel)
////            }
////
////            else -> {
////                when (chatsState) {
////                    is Result.Loading -> {
////                        Box(
////                            modifier = Modifier.fillMaxSize(),
////                            contentAlignment = Alignment.Center
////                        ) {
////                            CircularProgressIndicator()
////                        }
////                    }
////
////                    is Result.Error -> {
////                        Box(
////                            modifier = Modifier.fillMaxSize(),
////                            contentAlignment = Alignment.Center
////                        ) {
////                            Text("Error loading chats")
////                        }
////                    }
////
////                    is Result.Success -> {
////                        val allChats = (chatsState as Result.Success<List<Chat>>).data
////                        ChatListContent(chats = allChats, viewModel = viewModel)
////                    }
////                }
////            }
////        }
////    }
////}
//
//@Composable
//fun ChatListContent(
//    chats: List<Chat>,
//    viewModel: ChatViewModel,
//    navController: NavController
////    navController: NavController? = null
//) {
//    LazyColumn {
//        items(chats) { chat ->
//            ChatListItem(
//                chat = chat,
//                onClick = {
//                    viewModel.selectChat(chat)
//                    navController.navigate("chat_detail/${chat.id}")
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun FilteredChatList(
//    chats: List<Chat>,
//    viewModel: ChatViewModel,
//    navController: NavController
//) {
//    LazyColumn {
//        if (chats.isEmpty()) {
//            item {
//                EmptySearchResults(query = "")
//            }
//        } else {
//            items(chats) { chat ->
//                ChatListItem(
//                    chat = chat,
//                    onClick = {
//                        viewModel.selectChat(chat)
//                        // You'll need to pass navController here
//                        navController.navigate("chat_detail/${chat.id}")
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun EmptySearchResults(query: String) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Icon(
//                imageVector = Icons.Default.SearchOff,
//                contentDescription = "No results",
//                tint = Color.Gray,
//                modifier = Modifier.size(48.dp)
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = if (query.isNotEmpty()) {
//                    "No results found for \"$query\""
//                } else {
//                    "No chats found"
//                },
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.Gray
//            )
//        }
//    }
//}
