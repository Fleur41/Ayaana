package com.sam.ayaana.presentation.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.model.ProfileTab
import com.sam.ayaana.domain.model.User
import kotlin.collections.forEachIndexed
import kotlin.enums.EnumEntries

@Composable
fun ProfilePostsGrid(
    user: User,
    posts: List<Post> = emptyList(),
    taggedPosts: List<Post> = emptyList(),
    reels: List<Post> = emptyList(),
    onPostClick: (String) -> Unit = {},
    onTabSelected: (ProfileTab) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = ProfileTab.entries

    Column(modifier = modifier) {
        ProfileTabsRow(
            selectedTabIndex = selectedTabIndex,
            tabs = tabs,
            onTabSelected = { index ->
                selectedTabIndex = index
                onTabSelected(tabs[index])
            }
        )

        Divider(color = Color.LightGray)

        when (tabs[selectedTabIndex]) {
            ProfileTab.POSTS -> {
                if (user.isPrivate && !user.isFollowing && !user.isCurrentUser) {
                    PrivateProfileContent(user = user)
                } else if (posts.isEmpty()) {
                    EmptyPostsContent(user = user)
                } else {
                    PostsGrid(posts = posts, onPostClick = onPostClick)
                }
            }
            ProfileTab.REELS -> {
                if (reels.isEmpty()) {
                    EmptyReelsContent()
                } else {
                    ReelsGrid(posts = reels, onPostClick = onPostClick)
                }
            }
            ProfileTab.TAGGED -> {
                if (taggedPosts.isEmpty()) {
                    EmptyTaggedContent()
                } else {
                    PostsGrid(posts = taggedPosts, onPostClick = onPostClick)
                }
            }
        }
    }
}

@Composable
private fun ProfileTabsRow(
    selectedTabIndex: Int,
    tabs: EnumEntries<ProfileTab>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        divider = { HorizontalDivider(color = Color.LightGray) },
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 1.dp,
                color = Color.Black
            )
        },
        contentColor = Color.Black
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(24.dp),
                        tint = if (selectedTabIndex == index) Color.Black else Color.Gray
                    )
                },
                text = {
                    Text(
                        text = tab.title,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTabIndex == index) Color.Black else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
private fun PostsGrid(
    posts: List<Post>,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(posts) { post ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable { onPostClick(post.id) }
            ) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Post image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun ReelsGrid(
    posts: List<Post>,
    onPostClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(posts) { post ->
            Box(
                modifier = Modifier
                    .aspectRatio(0.75f)
                    .clickable { onPostClick(post.id) }
            ) {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Reel",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun PrivateProfileContent(
    user: User,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = "Private",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "This account is private",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Follow this account to see their photos and videos.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun EmptyPostsContent(
    user: User,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "No posts",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = if (user.isCurrentUser) "Share your first photo" else "No posts yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            if (user.isCurrentUser) {
                Text(
                    text = "When you share photos, they will appear on your profile.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyReelsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = "No reels",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "No reels yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyTaggedContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = "No tags",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = "No tags yet",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

//// presentation/components/profile/ProfileHeader.kt
//package com.sam.ayaana.presentation.components.profile
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
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
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Email
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Divider
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import com.sam.ayaana.domain.model.FollowStatus
//import com.sam.ayaana.domain.model.User
//
//@Composable
//fun ProfileHeader(
//    user: User,
//    modifier: Modifier = Modifier,
//    onEditProfileClick: () -> Unit = {},
//    onShareProfileClick: () -> Unit = {},
//    onFollowClick: () -> Unit = {},
//    onMessageClick: () -> Unit = {},
//    onEmailClick: () -> Unit = {},
//    onAddProfilePhotoClick: () -> Unit = {}
//
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 12.dp)
//    ) {
//        // Profile Info Row
//        ProfileInfoRow(
//            user = user,
//            onAddProfilePhotoClick = onAddProfilePhotoClick
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // User Details Section
//        UserDetailsSection(user = user)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Action Buttons Section
//        ActionButtonsSection(
//            user = user,
//            onEditProfileClick = onEditProfileClick,
//            onShareProfileClick = onShareProfileClick,
//            onFollowClick = onFollowClick,
//            onMessageClick = onMessageClick,
//            onEmailClick = onEmailClick
//        )
//
//        // Profile Completion (only for current user)
//        if (user.isCurrentUser && calculateProfileCompletion(user) < 80) {
//            Spacer(modifier = Modifier.height(16.dp))
//            ProfileCompletionSection(
//                user = user,
//                onCompleteProfileClick = onEditProfileClick
//            )
//        }
//
//        HorizontalDivider(
//            modifier = Modifier
//                .padding(vertical = 16.dp)
//                .fillMaxWidth(),
//            color = Color.LightGray
//        )
//    }
//}
//
//@Composable
//private fun ProfileInfoRow(
//    user: User,
//    onAddProfilePhotoClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//        // Profile Image with Instagram gradient border
//        ProfileImageWithBorder(
//            profilePicture = user.profilePicture,
//            isCurrentUser = user.isCurrentUser,
//            onAddProfilePhotoClick = onAddProfilePhotoClick
//        )
//
//        Spacer(modifier = Modifier.width(20.dp))
//
//        // Stats Section with dividers
//        ProfileStatsSection(
//            posts = user.posts,
//            followers = user.followers,
//            following = user.following
//        )
//    }
//}
//
//@Composable
//private fun ProfileImageWithBorder(
//    profilePicture: String?,
//    isCurrentUser: Boolean,
//    onAddProfilePhotoClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(modifier = modifier.size(86.dp)) {
//        // Instagram-style gradient border
//        Box(
//            modifier = Modifier
//                .size(86.dp)
//                .background(
//                    brush = androidx.compose.ui.graphics.Brush.sweepGradient(
//                        colors = listOf(
//                            Color(0xFF833AB4),
//                            Color(0xFFC13584),
//                            Color(0xFFFD1D1D),
//                            Color(0xFFFCAF45)
//                        )
//                    ),
//                    shape = CircleShape
//                )
//                .padding(3.dp)
//        ) {
//            if (!profilePicture.isNullOrEmpty()) {
//                AsyncImage(
//                    model = profilePicture,
//                    contentDescription = "Profile picture",
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clip(CircleShape)
//                        .background(Color.White, CircleShape)
//                        .padding(2.dp),
//                    contentScale = ContentScale.Crop
//                )
//            } else {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .clip(CircleShape)
//                        .background(Color.LightGray),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Person,
//                        contentDescription = "Profile",
//                        tint = Color.White,
//                        modifier = Modifier.size(40.dp)
//                    )
//                }
//            }
//        }
//
//        // Add photo button (only for current user)
//        if (isCurrentUser) {
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .size(28.dp)
//                    .background(Color(0xFF0095F6), CircleShape)
//                    .border(2.dp, Color.White, CircleShape)
//                    .clickable { onAddProfilePhotoClick() },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add profile photo",
//                    tint = Color.White,
//                    modifier = Modifier.size(16.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun ProfileStatsSection(
//    posts: Int,
//    followers: Int,
//    following: Int,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.SpaceEvenly
//    ) {
//        ProfileStatItem(count = posts, label = "Posts")
//        HorizontalDivider(
//            modifier = Modifier
//                .height(40.dp)
//                .width(1.dp),
//            color = Color.LightGray
//        )
//        ProfileStatItem(count = followers, label = "Followers")
//        HorizontalDivider(
//            modifier = Modifier
//                .height(40.dp)
//                .width(1.dp),
//            color = Color.LightGray
//        )
//        ProfileStatItem(count = following, label = "Following")
//    }
//}
//
//@Composable
//private fun ProfileStatItem(
//    count: Int,
//    label: String,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = formatCount(count),
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontWeight = FontWeight.Bold
//            ),
//            fontSize = 16.sp
//        )
//        Text(
//            text = label,
//            style = MaterialTheme.typography.bodySmall,
//            color = Color.Gray,
//            fontSize = 12.sp
//        )
//    }
//}
//
//@Composable
//private fun UserDetailsSection(
//    user: User,
//    modifier: Modifier = Modifier
//) {
//    Column(modifier = modifier) {
//        Text(
//            text = user.fullName,
//            style = MaterialTheme.typography.titleMedium.copy(
//                fontWeight = FontWeight.Bold
//            ),
//            fontSize = 14.sp
//        )
//
//        if (!user.bio.isNullOrEmpty()) {
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = user.bio,
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.Black,
//                fontSize = 14.sp,
//                lineHeight = 18.sp
//            )
//        }
//
//        user.website?.let { website ->
//            if (website.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = website,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color(0xFF00376B),
//                    fontSize = 14.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun ActionButtonsSection(
//    user: User,
//    onEditProfileClick: () -> Unit,
//    onShareProfileClick: () -> Unit,
//    onFollowClick: () -> Unit,
//    onMessageClick: () -> Unit,
//    onEmailClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        if (user.isCurrentUser) {
//            // My Profile Buttons
//            Button(
//                onClick = onEditProfileClick,
//                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFEFEFEF),
//                    contentColor = Color.Black
//                ),
//                shape = MaterialTheme.shapes.small,
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 0.dp,
//                    pressedElevation = 0.dp
//                )
//            ) {
//                Text(
//                    text = "Edit profile",
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//
//            Button(
//                onClick = onShareProfileClick,
//                modifier = Modifier.weight(1f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFEFEFEF),
//                    contentColor = Color.Black
//                ),
//                shape = MaterialTheme.shapes.small,
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 0.dp,
//                    pressedElevation = 0.dp
//                )
//            ) {
//                Text(
//                    text = "Share profile",
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//
//            IconButton(
//                onClick = { /* Handle more options */ },
//                modifier = Modifier
//                    .size(48.dp)
//                    .background(Color(0xFFEFEFEF), MaterialTheme.shapes.small)
//            ) {
//                Icon(
//                    imageVector = Icons.Default.MoreVert,
//                    contentDescription = "More options",
//                    tint = Color.Black
//                )
//            }
//        } else {
//            // Other User's Profile Buttons
//            val (containerColor, contentColor) = when (user.followStatus) {
//                FollowStatus.FOLLOWING -> Color(0xFFEFEFEF) to Color.Black
//                FollowStatus.REQUESTED -> Color(0xFFEFEFEF) to Color.Black
//                FollowStatus.NOT_FOLLOWING -> Color(0xFF0095F6) to Color.White
//            }
//
//            Button(
//                onClick = onFollowClick,
//                modifier = Modifier.weight(1.5f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = containerColor,
//                    contentColor = contentColor
//                ),
//                shape = MaterialTheme.shapes.small,
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 0.dp,
//                    pressedElevation = 0.dp
//                )
//            ) {
//                FollowButtonContent(followStatus = user.followStatus)
//            }
//
//            Button(
//                onClick = onMessageClick,
//                modifier = Modifier.weight(1.5f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFEFEFEF),
//                    contentColor = Color.Black
//                ),
//                shape = MaterialTheme.shapes.small,
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 0.dp,
//                    pressedElevation = 0.dp
//                )
//            ) {
//                Text(
//                    text = "Message",
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//
//            Button(
//                onClick = onEmailClick,
//                modifier = Modifier.weight(0.8f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFFEFEFEF),
//                    contentColor = Color.Black
//                ),
//                shape = MaterialTheme.shapes.small,
//                elevation = ButtonDefaults.buttonElevation(
//                    defaultElevation = 0.dp,
//                    pressedElevation = 0.dp
//                )
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Email,
//                    contentDescription = "Email",
//                    modifier = Modifier.size(18.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun FollowButtonContent(
//    followStatus: FollowStatus,
//    modifier: Modifier = Modifier
//) {
//    when (followStatus) {
//        FollowStatus.FOLLOWING -> {
//            Row(
//                modifier = modifier,
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "Following",
//                    modifier = Modifier.size(18.dp)
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "Following",
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
//        }
//        FollowStatus.REQUESTED -> {
//            Text(
//                text = "Requested",
//                fontSize = 13.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//        }
//        FollowStatus.NOT_FOLLOWING -> {
//            Text(
//                text = "Follow",
//                fontSize = 13.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//        }
//    }
//}
//
//@Composable
//private fun ProfileCompletionSection(
//    user: User,
//    onCompleteProfileClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .background(Color(0xFFFAFAFA), MaterialTheme.shapes.small)
//            .padding(16.dp)
//    ) {
//        Text(
//            text = "Complete your profile",
//            style = MaterialTheme.typography.bodyMedium.copy(
//                fontWeight = FontWeight.SemiBold
//            ),
//            fontSize = 14.sp
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Progress bar
//        val completionPercentage = calculateProfileCompletion(user)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(4.dp)
//                .background(Color(0xFFDBDBDB), MaterialTheme.shapes.small)
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth(completionPercentage / 100f)
//                    .height(4.dp)
//                    .background(Color(0xFF0095F6), MaterialTheme.shapes.small)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // Missing items
//        MissingProfileItems(user = user)
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        Button(
//            onClick = onCompleteProfileClick,
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = Color.Transparent,
//                contentColor = Color(0xFF0095F6)
//            ),
//            elevation = null
//        ) {
//            Text(
//                text = "Complete profile",
//                fontSize = 13.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//        }
//    }
//}
//
//@Composable
//private fun MissingProfileItems(
//    user: User,
//    modifier: Modifier = Modifier
//) {
//    Column(modifier = modifier) {
//        if (user.profilePicture.isNullOrEmpty()) {
//            Text(
//                text = "• Add a profile picture",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray,
//                fontSize = 12.sp
//            )
//        }
//        if (user.bio.isNullOrEmpty()) {
//            Text(
//                text = "• Add a bio",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray,
//                fontSize = 12.sp
//            )
//        }
//        if (user.fullName.isBlank() || user.fullName == user.username) {
//            Text(
//                text = "• Add your name",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray,
//                fontSize = 12.sp
//            )
//        }
//        if (user.website.isNullOrEmpty()) {
//            Text(
//                text = "• Add a link",
//                style = MaterialTheme.typography.bodySmall,
//                color = Color.Gray,
//                fontSize = 12.sp
//            )
//        }
//    }
//}
//
//private fun formatCount(count: Int): String {
//    return when {
//        count >= 1000000 -> "${count / 1000000}M"
//        count >= 1000 -> "${count / 1000}K"
//        else -> count.toString()
//    }
//}
//
//private fun calculateProfileCompletion(user: User): Int {
//    var completion = 0
//    if (!user.profilePicture.isNullOrEmpty()) completion += 25
//    if (!user.bio.isNullOrEmpty()) completion += 25
//    if (!user.fullName.isBlank() && user.fullName != user.username) completion += 25
//    if (!user.website.isNullOrEmpty()) completion += 25
//    return completion
//}