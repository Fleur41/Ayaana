package com.sam.ayaana.presentation.components.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sam.ayaana.domain.model.FollowStatus
import com.sam.ayaana.domain.model.User

@Composable
fun ProfileHeaderSection(
    user: User,
    modifier: Modifier = Modifier,
    onEditProfileClick: () -> Unit = {},
    onShareProfileClick: () -> Unit = {},
    onFollowClick: () -> Unit = {},
    onMessageClick: () -> Unit = {},
    onEmailClick: () -> Unit = {},
    onAddProfilePhotoClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Profile Info Row
        ProfileInfoRow(
            user = user,
            onAddProfilePhotoClick = onAddProfilePhotoClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // User Details Section
        UserDetailsSection(user = user)

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Section
        ActionButtonsSection(
            user = user,
            onEditProfileClick = onEditProfileClick,
            onShareProfileClick = onShareProfileClick,
            onFollowClick = onFollowClick,
            onMessageClick = onMessageClick,
            onEmailClick = onEmailClick
        )

        // Profile Completion (only for current user)
        if (user.isCurrentUser && calculateProfileCompletion(user) < 80) {
            Spacer(modifier = Modifier.height(16.dp))
            ProfileCompletionSection(
                user = user,
                onCompleteProfileClick = onEditProfileClick
            )
        }

        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            color = Color.LightGray
        )
    }
}

@Composable
private fun ProfileInfoRow(
    user: User,
    onAddProfilePhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Profile Image with Instagram gradient border and flip animation
        ProfileImageWithBorder(
            user = user,
            onAddProfilePhotoClick = onAddProfilePhotoClick
        )

        Spacer(modifier = Modifier.width(20.dp))

        // Stats Section with dividers
        ProfileStatsSection(
            posts = user.posts,
            followers = user.followers,
            following = user.following
        )
    }
}

@Composable
private fun ProfileImageWithBorder(
    user: User,
    onAddProfilePhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states for flip/rotate effect
    var isAnimating by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }
    var scaleX by remember { mutableStateOf(1f) }

    // Animated values
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 2000),
        finishedListener = {
            // When rotation completes, reverse the animation
            if (isAnimating) {
                rotation = -rotation
            } else {
                // Reset to original state
                rotation = 0f
                scaleX = 1f
            }
        }
    )

    val animatedScaleX by animateFloatAsState(
        targetValue = scaleX,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = modifier.size(86.dp)
    ) {
        // Instagram-style gradient border
        Box(
            modifier = Modifier
                .size(86.dp)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF833AB4),
                            Color(0xFFC13584),
                            Color(0xFFFD1D1D),
                            Color(0xFFFCAF45)
                        )
                    ),
                    shape = CircleShape
                )
                .padding(3.dp)
                // Apply flip and rotation transformations
                .graphicsLayer {
                    rotationY = animatedRotation
                    scaleX = animatedScaleX
                    cameraDistance = 8 * density
                }
                .clip(CircleShape)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    // Start the flip animation
                    isAnimating = true
                    rotation = 360f // Full rotation
                    scaleX = -1f   // Flip horizontally

                    // Auto-reset after animation completes
                    // The finishedListener will handle reversing the animation
                }
        ) {
            // Check for local Uri first, then remote URL
            val imageModel = when {
                user.localProfileUri != null -> user.localProfileUri
                !user.profilePicture.isNullOrEmpty() -> user.profilePicture
                else -> null
            }

            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White, CircleShape)
                        .padding(2.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        // Add photo button (only for current user)
        if (user.isCurrentUser) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .background(Color(0xFF0095F6), CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) { onAddProfilePhotoClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add profile photo",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsSection(
    posts: Int,
    followers: Int,
    following: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProfileStatItem(count = posts, label = "Posts")
        HorizontalDivider(
            modifier = Modifier
                .height(40.dp)
                .width(1.dp),
            color = Color.LightGray
        )
        ProfileStatItem(count = followers, label = "Followers")
        HorizontalDivider(
            modifier = Modifier
                .height(40.dp)
                .width(1.dp),
            color = Color.LightGray
        )
        ProfileStatItem(count = following, label = "Following")
    }
}

@Composable
private fun ProfileStatItem(
    count: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatCount(count),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            fontSize = 16.sp
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun UserDetailsSection(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = user.fullName,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            fontSize = 14.sp
        )

        if (!user.bio.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
        }

        user.website?.let { website ->
            if (website.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = website,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF00376B),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(
    user: User,
    onEditProfileClick: () -> Unit,
    onShareProfileClick: () -> Unit,
    onFollowClick: () -> Unit,
    onMessageClick: () -> Unit,
    onEmailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (user.isCurrentUser) {
            // My Profile Buttons
            Button(
                onClick = onEditProfileClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEFEFEF),
                    contentColor = Color.Black
                ),
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = "Edit profile",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onShareProfileClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEFEFEF),
                    contentColor = Color.Black
                ),
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = "Share profile",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { /* Handle more options */ },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEFEFEF), MaterialTheme.shapes.small)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = Color.Black
                )
            }
        } else {
            // Other User's Profile Buttons
            val followButtonColors = when (user.followStatus) {
                FollowStatus.FOLLOWING -> Color(0xFFEFEFEF) to Color.Black
                FollowStatus.REQUESTED -> Color(0xFFEFEFEF) to Color.Black
                FollowStatus.NOT_FOLLOWING -> Color(0xFF0095F6) to Color.White
            }

            Button(
                onClick = onFollowClick,
                modifier = Modifier.weight(1.5f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = followButtonColors.first,
                    contentColor = followButtonColors.second
                ),
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                FollowButtonContent(followStatus = user.followStatus)
            }

            Button(
                onClick = onMessageClick,
                modifier = Modifier.weight(1.5f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEFEFEF),
                    contentColor = Color.Black
                ),
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Text(
                    text = "Message",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = onEmailClick,
                modifier = Modifier.weight(0.8f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEFEFEF),
                    contentColor = Color.Black
                ),
                shape = MaterialTheme.shapes.small,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun FollowButtonContent(
    followStatus: FollowStatus,
    modifier: Modifier = Modifier
) {
    when (followStatus) {
        FollowStatus.FOLLOWING -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Following",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Following",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        FollowStatus.REQUESTED -> {
            Text(
                text = "Requested",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        FollowStatus.NOT_FOLLOWING -> {
            Text(
                text = "Follow",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ProfileCompletionSection(
    user: User,
    onCompleteProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFAFA), MaterialTheme.shapes.small)
            .padding(16.dp)
    ) {
        Text(
            text = "Complete your profile",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        val completionPercentage = calculateProfileCompletion(user)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFFDBDBDB), MaterialTheme.shapes.small)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(completionPercentage / 100f)
                    .height(4.dp)
                    .background(Color(0xFF0095F6), MaterialTheme.shapes.small)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Missing items
        MissingProfileItems(user = user)

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCompleteProfileClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF0095F6)
            ),
            elevation = null
        ) {
            Text(
                text = "Complete profile",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun MissingProfileItems(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val hasProfileImage = user.localProfileUri != null || !user.profilePicture.isNullOrEmpty()
        if (!hasProfileImage) {
            Text(
                text = "• Add a profile picture",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        if (user.bio.isNullOrEmpty()) {
            Text(
                text = "• Add a bio",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        if (user.fullName.isBlank() || user.fullName == user.username) {
            Text(
                text = "• Add your name",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        if (user.website.isNullOrEmpty()) {
            Text(
                text = "• Add a link",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

private fun formatCount(count: Int): String {
    return when {
        count >= 1000000 -> "${count / 1000000}M"
        count >= 1000 -> "${count / 1000}K"
        else -> count.toString()
    }
}

private fun calculateProfileCompletion(user: User): Int {
    var completion = 0
    val hasProfileImage = user.localProfileUri != null || !user.profilePicture.isNullOrEmpty()
    if (hasProfileImage) completion += 25
    if (!user.bio.isNullOrEmpty()) completion += 25
    if (!user.fullName.isBlank() && user.fullName != user.username) completion += 25
    if (!user.website.isNullOrEmpty()) completion += 25
    return completion
}

// Preview
@Preview(showBackground = true)
@Composable
private fun ProfileHeaderSectionPreview() {
    MaterialTheme {
        Column {
            // My Profile
            ProfileHeaderSection(
                user = User(
                    id = "1",
                    username = "fle_ur41",
                    email = "fleur@example.com",
                    profilePicture = null,
                    localProfileUri = null,
                    fullName = "Fleur Kings",
                    bio = null,
                    posts = 3,
                    followers = 174,
                    following = 604,
                    isPrivate = false,
                    isFollowing = false,
                    followStatus = FollowStatus.NOT_FOLLOWING,
                    isCurrentUser = true
                )
            )

            // Other User Profile
            ProfileHeaderSection(
                user = User(
                    id = "2",
                    username = "andrew",
                    email = "andrew@example.com",
                    profilePicture = "https://picsum.photos/200",
                    localProfileUri = null,
                    fullName = "Andrew Queo",
                    bio = "Artist\nDESIGNER\nIsabelle@art.design",
                    posts = 174,
                    followers = 772000,
                    following = 714,
                    isPrivate = false,
                    isFollowing = false,
                    followStatus = FollowStatus.NOT_FOLLOWING,
                    isCurrentUser = false
                )
            )
        }
    }
}

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
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import coil.compose.AsyncImage
//import com.sam.ayaana.domain.model.FollowStatus
//import com.sam.ayaana.domain.model.User
//
//@Composable
//fun ProfileHeaderSection(
//    user: User,
//    modifier: Modifier = Modifier,
//    onEditProfileClick: () -> Unit = {},
//    onShareProfileClick: () -> Unit = {},
//    onFollowClick: () -> Unit = {},
//    onMessageClick: () -> Unit = {},
//    onEmailClick: () -> Unit = {},
//    onAddProfilePhotoClick: () -> Unit = {}
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
//        // Profile Image with Instagram gradient border - UPDATED: Added localProfileUri support
//        ProfileImageWithBorder(
//            user = user, // CHANGED: Pass entire user object instead of just profilePicture
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
//    user: User, // UPDATED: Accept entire user object
//    onAddProfilePhotoClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Box(modifier = modifier.size(86.dp)) {
//        // Instagram-style gradient border
//        Box(
//            modifier = Modifier
//                .size(86.dp)
//                .background(
//                    brush = Brush.sweepGradient(
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
//            // UPDATED START: Check for local Uri first, then remote URL
//            val imageModel = when {
//                user.localProfileUri != null -> user.localProfileUri // Local image from gallery
//                !user.profilePicture.isNullOrEmpty() -> user.profilePicture // Remote image URL
//                else -> null // No image
//            }
//
//            if (imageModel != null) {
//                AsyncImage(
//                    model = imageModel,
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
//            // UPDATED END
//        }
//
//        // Add photo button (only for current user)
//        if (user.isCurrentUser) {
//            Box(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .size(28.dp)
//                    .background(Color(0xFF0095F6), CircleShape)
//                    .border(2.dp, Color.White, CircleShape)
//                    .clickable(
//                        interactionSource = null, // UPDATED: Remove ripple effect
//                        indication = null
//                    ) { onAddProfilePhotoClick() },
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
//            val followButtonColors = when (user.followStatus) {
//                FollowStatus.FOLLOWING -> Color(0xFFEFEFEF) to Color.Black
//                FollowStatus.REQUESTED -> Color(0xFFEFEFEF) to Color.Black
//                FollowStatus.NOT_FOLLOWING -> Color(0xFF0095F6) to Color.White
//            }
//
//            Button(
//                onClick = onFollowClick,
//                modifier = Modifier.weight(1.5f),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = followButtonColors.first,
//                    contentColor = followButtonColors.second
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
//        // Missing items - UPDATED: Added localProfileUri check
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
//        // UPDATED: Check both localProfileUri and profilePicture
//        val hasProfileImage = user.localProfileUri != null || !user.profilePicture.isNullOrEmpty()
//        if (!hasProfileImage) {
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
//// UPDATED: Added localProfileUri to profile completion calculation
//private fun calculateProfileCompletion(user: User): Int {
//    var completion = 0
//    // Check both local and remote profile images
//    val hasProfileImage = user.localProfileUri != null || !user.profilePicture.isNullOrEmpty()
//    if (hasProfileImage) completion += 25
//    if (!user.bio.isNullOrEmpty()) completion += 25
//    if (!user.fullName.isBlank() && user.fullName != user.username) completion += 25
//    if (!user.website.isNullOrEmpty()) completion += 25
//    return completion
//}
//
//// Preview
//@Preview(showBackground = true)
//@Composable
//private fun ProfileHeaderSectionPreview() {
//    MaterialTheme {
//        Column {
//            // My Profile
//            ProfileHeaderSection(
//                user = User(
//                    id = "1",
//                    username = "fle_ur41",
//                    email = "fleur@example.com",
//                    profilePicture = null,
//                    localProfileUri = null, // UPDATED: Added localProfileUri field
//                    fullName = "Fleur Kings",
//                    bio = null,
//                    posts = 3,
//                    followers = 174,
//                    following = 604,
//                    isPrivate = false,
//                    isFollowing = false,
//                    followStatus = FollowStatus.NOT_FOLLOWING,
//                    isCurrentUser = true
//                )
//            )
//
//            // Other User Profile
//            ProfileHeaderSection(
//                user = User(
//                    id = "2",
//                    username = "andrew",
//                    email = "andrew@example.com",
//                    profilePicture = "https://picsum.photos/200",
//                    localProfileUri = null, // UPDATED: Added localProfileUri field
//                    fullName = "Andrew Queo",
//                    bio = "Artist\nDESIGNER\nIsabelle@art.design",
//                    posts = 174,
//                    followers = 772000,
//                    following = 714,
//                    isPrivate = false,
//                    isFollowing = false,
//                    followStatus = FollowStatus.NOT_FOLLOWING,
//                    isCurrentUser = false
//                )
//            )
//        }
//    }
//}
//
