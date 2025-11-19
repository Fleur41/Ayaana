package com.sam.ayaana.presentation.component.homesection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.presentation.component.common.ErrorMessage
import com.sam.ayaana.presentation.component.common.PostItem
import com.sam.ayaana.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun PostsSection(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onProfileClick: (String) -> Unit,
    onLikeClick: (String, Boolean) -> Unit,
    onCommentClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onRepostClick: (String, Boolean) -> Unit,
    onMoreOptionsClick: (String) -> Unit,
    onSaveClick: (String) -> Unit
) {
    //val currentPosts: Flow<PagingData<Post>> = viewModel.getCurrentPosts()
    val homePosts: LazyPagingItems<Post> = viewModel.homePosts.collectAsLazyPagingItems()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Timeline selector (optional - you can add this later)
        // Row(
        //     modifier = Modifier.fillMaxWidth(),
        //     horizontalArrangement = Arrangement.Center
        // ) {
        //     TabRow(selectedTabIndex = selectedTab) {
        //         // Home Tab
        //         Tab(selected = selectedTab == 0, onClick = { /* switch to home */ }) {
        //             Text("Home")
        //         }
        //         // Explore Tab
        //         Tab(selected = selectedTab == 1, onClick = { /* switch to explore */ }) {
        //             Text("Explore")
        //         }
        //     }
        // }
        items(
            count = homePosts.itemCount,
            key = homePosts.itemKey{post -> post.id}
        ){ index ->
            val post = homePosts[index]
            if (post != null){
                PostItem(
                    post = post,
                    onProfileClick = onProfileClick,
                    onLikeClick = onLikeClick,
                    onCommentClick = onCommentClick,
                    onShareClick = onShareClick,
                    onRepostClick = onRepostClick,
                    onMoreOptionsClick = onMoreOptionsClick,
                    onSaveClick = onSaveClick,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // Handle loading states
        homePosts.loadState.apply {
            when {
                // Loading more items
                append is LoadState.Loading -> {
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

                // Error loading more
                append is LoadState.Error -> {
                    item {
                        //Show error message
                        ErrorMessage(
                            message = "Couldn't load more posts.",
                            onClickRetry = { homePosts.retry() }
                        )
                    }
                }

                // Handle the initial loading error
                refresh is LoadState.Error -> {
                    item {
                        ErrorMessage(
                            message = "Failed to load posts. Check your connection.",
                            modifier = Modifier.fillParentMaxSize(),
                            onClickRetry = { homePosts.retry() }
                        )
                    }
                }
            }
        }
    }
}