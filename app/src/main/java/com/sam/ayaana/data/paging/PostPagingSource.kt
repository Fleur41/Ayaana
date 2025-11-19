package com.sam.ayaana.data.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sam.ayaana.domain.model.Post
import com.sam.ayaana.domain.repository.IPostRepository
import kotlinx.coroutines.flow.first
import com.sam.ayaana.Utils.Result

// data/paging/PostPagingSource.kt - Enhanced mock data
class FeedPostPagingSource(
    private val postRepository: IPostRepository
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val page = params.key ?: 1

            // TEMPORARY: Mock data for testing - COMMENT OUT WHEN API IS READY
            val mockPosts = createMockPosts(page)
            LoadResult.Page(
                data = mockPosts,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (mockPosts.isNotEmpty()) page + 1 else null
            )

            // TODO: UNCOMMENT WHEN API IS READY
            /*
            val result = postRepository.getFeedPosts().first()
            when (result) {
                is Result.Success -> {
                    val posts = result.data
                    LoadResult.Page(
                        data = posts,
                        prevKey = if (page > 1) page - 1 else null,
                        nextKey = if (posts.isNotEmpty()) page + 1 else null
                    )
                }
                is Result.Error -> {
                    LoadResult.Error(Exception(result.message))
                }
                else -> LoadResult.Error(Exception("Failed to load posts"))
            }
            */
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // Enhanced mock data generator with videos and more users
    private fun createMockPosts(page: Int): List<Post> {
        val users = listOf(
            UserInfo("andrew", "Andrew Queo", "Artist • DESIGNER"),
            UserInfo("silver", "Silver Johnson", "Photographer 📸"),
            UserInfo("matso", "Matso Rodriguez", "Travel Enthusiast 🌍"),
            UserInfo("samuel", "Samuel Wilson", "Tech Innovator 💻"),
            UserInfo("grace", "Grace Lee", "Fashion Blogger 👗"),
            UserInfo("leo", "Leonard Otie", "Fitness Coach 💪"),
            UserInfo("emily", "Emily Davis", "Food Lover 🍕"),
            UserInfo("olivia", "Olivia Wilson", "Music Producer 🎵"),
            UserInfo("william", "William Brown", "Adventure Seeker 🏔️"),
            UserInfo("sophia", "Sophia Martinez", "Art Director 🎨")
        )

        return (1..10).map { index ->
            val postIndex = (page - 1) * 10 + index
            val user = users[postIndex % users.size]
            val isVideo = postIndex % 4 == 0 // Every 4th post is a video

            Post(
                id = "post_$postIndex",
                userId = "user_${postIndex % users.size}",
                username = user.username,
                userProfileImage = "https://picsum.photos/id/${postIndex + 100}/200/300",
                imageUrl = if (isVideo) {
                    // Video thumbnail (you can use a video thumbnail service later)
                    "https://picsum.photos/id/${postIndex + 500}/600/600"
                } else {
                    "https://picsum.photos/id/$postIndex/600/600"
                },
                caption = when (postIndex % 6) {
                    0 -> "Beautiful day! 🌞 Just finished my morning workout. #fitness #motivation"
                    1 -> "Exploring new places in ${user.location} 🗺️ The architecture here is amazing!"
                    2 -> "Good times with friends 👥 Coffee and conversations that last for hours ☕"
                    3 -> "New project alert! 🚀 Working on something exciting in the studio. #creative"
                    4 -> "Sunset views that take your breath away 🌅 Nature never fails to amaze me"
                    else -> "Daily inspiration: ${user.bio} Keep pushing forward! 💫"
                },
                likes = (50..800).random(),
                comments = (5..120).random(),
                reposts = (0..45).random(),
                isLiked = postIndex % 3 == 0,
                isReposted = postIndex % 5 == 0,
                timestamp = System.currentTimeMillis() - (postIndex * 3600000L),
                location = user.location,
                // Add video URL for video posts
                videoUrl = if (isVideo) "https://example.com/video_$postIndex.mp4" else null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    // Helper data class for user information
    private data class UserInfo(
        val username: String,
        val fullName: String,
        val bio: String,
        val location: String = when ((0..4).random()) {
            0 -> "New York, NY"
            1 -> "Los Angeles, CA"
            2 -> "London, UK"
            3 -> "Tokyo, Japan"
            else -> "Paris, France"
        }
    )
}

class ExplorePostPagingSource(
    private val postRepository: IPostRepository
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val page = params.key ?: 1

            // TEMPORARY: Mock data for testing - COMMENT OUT WHEN API IS READY
            val mockPosts = createMockExplorePosts(page)
            LoadResult.Page(
                data = mockPosts,
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (mockPosts.isNotEmpty()) page + 1 else null
            )

            // TODO: UNCOMMENT WHEN API IS READY
            /*
            val result = postRepository.getExplorePosts().first()
            when (result) {
                is Result.Success -> {
                    val posts = result.data
                    LoadResult.Page(
                        data = posts,
                        prevKey = if (page > 1) page - 1 else null,
                        nextKey = if (posts.isNotEmpty()) page + 1 else null
                    )
                }
                is Result.Error -> {
                    LoadResult.Error(Exception(result.message))
                }
                else -> LoadResult.Error(Exception("Failed to load explore posts"))
            }
            */
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    // Enhanced mock data generator for explore page
    private fun createMockExplorePosts(page: Int): List<Post> {
        val exploreUsers = listOf(
            UserInfo("travel_lover", "Travel Lover", "Exploring the world one country at a time ✈️", "Bali, Indonesia"),
            UserInfo("foodie_adventures", "Foodie Adventures", "Professional eater & food photographer 📸", "Rome, Italy"),
            UserInfo("art_gallery", "Art Gallery", "Contemporary art & digital creations 🎨", "Berlin, Germany"),
            UserInfo("fitness_motivation", "Fitness Motivation", "Helping you achieve your fitness goals 💪", "Miami, FL"),
            UserInfo("tech_news", "Tech News", "Latest in technology & innovation 📱", "San Francisco, CA"),
            UserInfo("fashion_diary", "Fashion Diary", "Street style & fashion trends 👗", "Milan, Italy"),
            UserInfo("music_world", "Music World", "Live concerts & music reviews 🎵", "Nashville, TN"),
            UserInfo("nature_photography", "Nature Photography", "Capturing Earth's beauty 🌿", "Reykjavik, Iceland"),
            UserInfo("gaming_community", "Gaming Community", "Esports & gaming content 🎮", "Seoul, South Korea"),
            UserInfo("book_lovers", "Book Lovers", "Reviews & literary discussions 📚", "Oxford, UK")
        )

        return (1..10).map { index ->
            val postIndex = (page - 1) * 10 + index
            val user = exploreUsers[postIndex % exploreUsers.size]
            val isVideo = postIndex % 3 == 0 // More videos in explore page

            Post(
                id = "explore_post_$postIndex",
                userId = "explore_user_${postIndex % exploreUsers.size}",
                username = user.username,
                userProfileImage = "https://picsum.photos/id/${postIndex + 200}/200/300",
                imageUrl = if (isVideo) {
                    "https://picsum.photos/id/${postIndex + 600}/600/600"
                } else {
                    "https://picsum.photos/id/${postIndex + 1000}/600/600"
                },
                caption = when (postIndex % 5) {
                    0 -> "Amazing sunset views in ${user.location}! 🌅 #travel #wanderlust"
                    1 -> "Delicious ${if (postIndex % 2 == 0) "pizza" else "sushi"} from ${user.location} 🍕"
                    2 -> "New ${if (postIndex % 2 == 0) "digital art" else "photography"} collection just dropped! 🎨"
                    3 -> "Daily motivation: ${user.bio} Never give up! 💫"
                    else -> "Exploring the hidden gems of ${user.location} 🗺️"
                },
                likes = (100..2000).random(),
                comments = (20..300).random(),
                reposts = (10..150).random(),
                isLiked = postIndex % 4 == 0,
                isReposted = postIndex % 6 == 0,
                timestamp = System.currentTimeMillis() - (postIndex * 1800000L),
                location = user.location,
                videoUrl = if (isVideo) "https://example.com/explore_video_$postIndex.mp4" else null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    private data class UserInfo(
        val username: String,
        val fullName: String,
        val bio: String,
        val location: String
    )
}

//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.sam.ayaana.domain.model.Post
//import com.sam.ayaana.domain.repository.IPostRepository
//import kotlinx.coroutines.flow.first
//import com.sam.ayaana.Utils.Result

//class FeedPostPagingSource(
//    private val postRepository: IPostRepository
//) : PagingSource<Int, Post>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
//        return try {
//            val page = params.key ?: 1
//            val result = postRepository.getFeedPosts().first()
//
//            when (result) {
//                is Result.Success -> {
//                    val posts = result.data
//                    LoadResult.Page(
//                        data = posts,
//                        prevKey = if (page > 1) page - 1 else null,
//                        nextKey = if (posts.isNotEmpty()) page + 1 else null
//                    )
//                }
//                is Result.Error -> {
//                    LoadResult.Error(Exception(result.message))
//                }
//                else -> LoadResult.Error(Exception("Failed to load posts"))
//            }
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
//}
//
//class ExplorePostPagingSource(
//    private val postRepository: IPostRepository
//) : PagingSource<Int, Post>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
//        return try {
//            val page = params.key ?: 1
//            val result = postRepository.getExplorePosts().first()
//
//            when (result) {
//                is Result.Success -> {
//                    val posts = result.data
//                    LoadResult.Page(
//                        data = posts,
//                        prevKey = if (page > 1) page - 1 else null,
//                        nextKey = if (posts.isNotEmpty()) page + 1 else null
//                    )
//                }
//                is Result.Error -> {
//                    LoadResult.Error(Exception(result.message))
//                }
//                else -> LoadResult.Error(Exception("Failed to load explore posts"))
//            }
//        } catch (e: Exception) {
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
//        return state.anchorPosition?.let { anchorPosition ->
//            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
//                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
//        }
//    }
//}