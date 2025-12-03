package com.sam.ayaana.Utils

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.sam.ayaana.R

@Composable
fun SharedBottomNav(
    navController: NavHostController,
    hasProfilePicture: Boolean,
    profileImageUrl: String? = null
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        modifier = Modifier.height(56.dp),
        containerColor = Color.Black,
        contentColor = Color.White
    ) {
        // Home
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == "home") R.drawable.ic_home_filled
                        else R.drawable.ic_home_outlined
                    ),
                    contentDescription = "Home",
                    tint = if (currentRoute == "home") Color.White else Color.Gray
                )
            },
            label = { Text(text = "", fontSize = 0.sp) },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Search
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == "search") R.drawable.ic_search_filled
                        else R.drawable.ic_search_outlined
                    ),
                    contentDescription = "Search",
                    tint = if (currentRoute == "search") Color.White else Color.Gray
                )
            },
            label = { Text(text = "", fontSize = 0.sp) },
            selected = currentRoute == "search",
            onClick = {
                navController.navigate("search") {
                    popUpTo("search") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Reels
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == "reels") R.drawable.ic_reels_filled
                        else R.drawable.ic_reels_outlined
                    ),
                    contentDescription = "Reels",
                    tint = if (currentRoute == "reels") Color.White else Color.Gray
                )
            },
            label = { Text(text = "", fontSize = 0.sp) },
            selected = currentRoute == "reels",
            onClick = {
                navController.navigate("reels") {
                    popUpTo("reels") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Chat
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (currentRoute == "chat") R.drawable.ic_chat_filled
                        else R.drawable.ic_chat_outlined
                    ),
                    contentDescription = "Chat",
                    tint = if (currentRoute == "chat") Color.White else Color.Gray
                )
            },
            label = { Text(text = "", fontSize = 0.sp) },
            selected = currentRoute == "chat",
            onClick = {
                navController.navigate("chat") {
                    popUpTo("chat") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        // Profile - SHOWS PROFILE IMAGE WHEN AVAILABLE
        NavigationBarItem(
            icon = {
                if (hasProfilePicture && profileImageUrl != null) {
                    // Show profile image
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            model = profileImageUrl,
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // Show icon when no profile picture
                    Icon(
                        painter = painterResource(
                            id = if (currentRoute == "profile") R.drawable.ic_profile_filled
                            else R.drawable.ic_profile_outlined
                        ),
                        contentDescription = "Profile",
                        tint = if (currentRoute == "profile") Color.White else Color.Gray
                    )
                }
            },
            label = { Text(text = "", fontSize = 0.sp) },
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    popUpTo("profile") { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.unit.dp
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import androidx.navigation.compose.currentBackStackEntryAsState
//import coil.compose.AsyncImage
//import com.sam.ayaana.R

//@Composable
//fun SharedBottomNav(
//    navController: NavController,
//    hasProfilePicture: Boolean = false,
//    profileImageUrl: String? = null
//) {
//    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//
//    NavigationBar(
//        modifier = Modifier.height(56.dp),
//        containerColor = Color.Black,
//        contentColor = Color.White
//    ) {
//        // Home
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(
//                        id = if (currentRoute == "home") R.drawable.ic_home_filled
//                        else R.drawable.ic_home_outlined
//                    ),
//                    contentDescription = "Home",
//                    tint = if (currentRoute == "home") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "home",
//            onClick = {
//                navController.navigate("home") {
//                    popUpTo("home") { saveState = true }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }
//        )
//
//        // Search
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(
//                        id = if (currentRoute == "search") R.drawable.ic_search_filled
//                        else R.drawable.ic_search_outlined
//                    ),
//                    contentDescription = "Search",
//                    tint = if (currentRoute == "search") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "search",
//            onClick = {
//                navController.navigate("search") {
//                    popUpTo("search") { saveState = true }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }
//        )
//
//        // Reels
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(
//                        id = if (currentRoute == "reels") R.drawable.ic_reels_filled
//                        else R.drawable.ic_reels_outlined
//                    ),
//                    contentDescription = "Reels",
//                    tint = if (currentRoute == "reels") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "reels",
//            onClick = {
//                navController.navigate("reels") {
//                    popUpTo("reels") { saveState = true }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }
//        )
//
//        // Chat
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    painter = painterResource(
//                        id = if (currentRoute == "chat") R.drawable.ic_chat_filled
//                        else R.drawable.ic_chat_outlined
//                    ),
//                    contentDescription = "Chat",
//                    tint = if (currentRoute == "chat") Color.White else Color.Gray
//                )
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "chat",
//            onClick = {
//                navController.navigate("chat") {
//                    popUpTo("chat") { saveState = true }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }
//        )
//
//        // Profile - WITH PROFILE IMAGE when available
//        NavigationBarItem(
//            icon = {
//                if (currentRoute == "profile" && hasProfilePicture && profileImageUrl != null) {
//                    // Show profile image when on profile screen and picture exists
//                    Box(modifier = Modifier.size(28.dp)) {
//                        AsyncImage(
//                            model = profileImageUrl,
//                            contentDescription = "Profile",
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .clip(androidx.compose.foundation.shape.CircleShape)
//                        )
//                    }
//                } else {
//                    // Show icon otherwise
//                    Icon(
//                        painter = painterResource(
//                            id = if (currentRoute == "profile") R.drawable.ic_profile_filled
//                            else R.drawable.ic_profile_outlined
//                        ),
//                        contentDescription = "Profile",
//                        tint = if (currentRoute == "profile") Color.White else Color.Gray
//                    )
//                }
//            },
//            label = { Text(text = "", fontSize = 0.sp) },
//            selected = currentRoute == "profile",
//            onClick = {
//                navController.navigate("profile") {
//                    popUpTo("profile") { saveState = true }
//                    launchSingleTop = true
//                    restoreState = true
//                }
//            }
//        )
//    }
//}