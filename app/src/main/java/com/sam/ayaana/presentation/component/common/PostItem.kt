package com.sam.ayaana.presentation.component.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.R

@Composable

fun PostItem(
    modifier: Modifier = Modifier,
    post: Post,
    onProfileClick: (String) -> Unit,
    onLikeClick: (String, Boolean) -> Unit,
    onCommentClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onRepostClick: (String, Boolean) -> Unit,
    onMoreOptionsClick: (String) -> Unit,
    onSaveClick: (String) -> Unit,
    isCommentsExpanded: Boolean = false,
) {
    Column(modifier = modifier) {
        // Post Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            AsyncImage(
                model = post.userProfileImage,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onProfileClick(post.userId) },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Username
            Text(
                text = post.username,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.clickable { onProfileClick(post.userId) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // More options
            IconButton(onClick = { onMoreOptionsClick(post.id) }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options"
                )
            }
        }

        // Post Image
        AsyncImage(
            model = post.imageUrl,
            contentDescription = "Post image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )

        // Post Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like button
            IconButton(onClick = { onLikeClick(post.id, post.isLiked) }) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (post.isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }

            // Comment button
            IconButton(onClick = { onCommentClick(post.id) }) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    //painter = painterResource(id = R.drawable.ic_message_outline),
                    contentDescription = "Comment"
                )
            }

            // Repost button
            IconButton(onClick = { onRepostClick(post.id, post.isReposted) }) {
                Icon(painter = painterResource(id = R.drawable.ic_repost),
                    //imageVector = Icons.Outlined.Repeat, // Repost icon
                    contentDescription = "Repost"
                )
            }

            // Share button (SEND icon)
            IconButton(onClick = { onShareClick(post.id) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_send),
                    //imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Share"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            IconButton(onClick = { onSaveClick(post.id) }) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Save"
                )
            }
        }

        // Likes count
        if (post.likes > 0) {
            Text(
                text = "Liked by ${post.likes} people",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Caption
        if (post.caption.isNotEmpty()) {
            Text(
                text = "${post.username} ${post.caption}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // Comments preview
        if (post.comments > 0) {
            Text(
                text = "View all ${post.comments} comments",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable { onCommentClick(post.id) }
            )
        }

        // Post time
        Text(
            text = "4 days ago", // You can format post.timestamp later
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        IconButton(onClick = onClickRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Retry",
            )
        }
    }
}
