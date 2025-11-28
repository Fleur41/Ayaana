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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.sam.ayaana.R
import com.sam.ayaana.Utils.Result
import com.sam.ayaana.domain.model.Chat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatsState by viewModel.chatsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    println("🟡 DEBUG: ChatListScreen recomposed, chatsState: $chatsState")
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
                    onClick = { }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Chat",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = viewModel::searchChats,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Requests",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (chatsState) {
                is Result.Loading -> {
                    println("🟡 DEBUG: Showing Loading state")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Error -> {
                    println("🟡 DEBUG: Showing Error state")
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error loading chats")
                    }
                }
                is Result.Success -> {
                    val chats = (chatsState as Result.Success<List<Chat>>).data
//                    val chats = (chatsState as Result.Success).data
                    println("🟡 DEBUG: Showing Success state with ${chats.size} chats")
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
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        placeholder = {
            Text("Ask Meta AI or Search")
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
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




//package com.sam.ayaana.presentation.screens.chat
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import com.sam.ayaana.R
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChatScreen(
//    navController: NavHostController? = null
//) {
//    val currentRoute = if (navController != null) {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        navBackStackEntry?.destination?.route
//    } else {
//        "chat"
//    }
//
//    Scaffold(
//        topBar = {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.Black)
//                    .padding(16.dp)
//            ) {
//                Text(
//                    text = "Chat",
//                    color = Color.White,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold
//                )
//            }
//        },
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
//                    onClick = { navController?.navigate("home") }
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
//                    onClick = { navController?.navigate("reels") }
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
//                    onClick = { navController?.navigate("chat") }
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
//                    onClick = { navController?.navigate("search") }
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
//                    onClick = { navController?.navigate("profile") }
//                )
//            }
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding)
//                .background(Color.White),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "Chat Content Will Be Here",
//                    color = Color.Black,
//                    fontSize = 18.sp
//                )
//            }
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun ChatScreenPreview() {
//    ChatScreen()
//}