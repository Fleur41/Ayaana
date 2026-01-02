package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.Result
import com.sam.ayaana.data.remote.api.LiveStreamApi
import com.sam.ayaana.data.remote.model.response.LiveCommentResponse
import com.sam.ayaana.data.remote.model.response.LiveStreamResponse
import com.sam.ayaana.data.remote.model.response.LiveStreamSessionResponse
import com.sam.ayaana.domain.model.LiveComment
import com.sam.ayaana.domain.model.LiveStream
import com.sam.ayaana.domain.model.LiveStreamSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.util.Date
import javax.inject.Inject

interface ILiveStreamRepository {

    suspend fun startLiveStream(title: String?, isPrivate: Boolean): Result<LiveStreamSession>
    suspend fun endLiveStream(streamId: String): Result<Unit>
    suspend fun updateLiveStreamViewerCount(streamId: String, viewerCount: Int): Result<Unit>

    suspend fun getLiveStreams(page: Int = 1, limit: Int = 20): Result<List<LiveStream>>
    suspend fun getLiveStreamById(streamId: String): Result<LiveStream>
    suspend fun joinLiveStream(streamId: String): Result<Unit>
    suspend fun leaveLiveStream(streamId: String): Result<Unit>

    suspend fun sendLiveComment(streamId: String, comment: String): Result<Unit>
    fun getLiveComments(streamId: String): Flow<List<LiveComment>>

    suspend fun getViewerCount(streamId: String): Result<Int>
    suspend fun getViewers(streamId: String): Result<List<String>>

    suspend fun saveLiveRecording(streamId: String): Result<String>
    suspend fun deleteLiveRecording(streamId: String): Result<Unit>
}

class LiveStreamRepositoryImpl @Inject constructor(
    private val liveStreamApi: LiveStreamApi
) : ILiveStreamRepository {

    private val _liveStreamsFlow = MutableStateFlow<List<LiveStream>>(emptyList())

    override suspend fun startLiveStream(title: String?, isPrivate: Boolean): Result<LiveStreamSession> {
        return try {
            println("DEBUG REPO: Starting live stream...")
            // MOCK DATA FOR NOW
            val mockSession = createMockLiveStreamSession()
            println("DEBUG REPO: Created mock live stream session")
            Result.Success(mockSession)
        } catch (e: Exception) {
            println("DEBUG REPO: Error creating live stream: ${e.message}")
            Result.Error(e.message ?: "Failed to start live stream")
        }
    }

    override suspend fun endLiveStream(streamId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Ending live stream: $streamId")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("DEBUG REPO: Error ending live stream: ${e.message}")
            Result.Error(e.message ?: "Failed to end live stream")
        }
    }

    override suspend fun updateLiveStreamViewerCount(streamId: String, viewerCount: Int): Result<Unit> {
        return try {
            println("DEBUG REPO: Updating viewer count for $streamId to $viewerCount")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("DEBUG REPO: Error updating viewer count: ${e.message}")
            Result.Error(e.message ?: "Failed to update viewer count")
        }
    }

    override suspend fun getLiveStreams(page: Int, limit: Int): Result<List<LiveStream>> {
        return try {
            println("DEBUG REPO: Creating mock live streams...")
            // MOCK DATA FOR NOW
            val mockLiveStreams = createMockLiveStreams()
            println("DEBUG REPO: Created ${mockLiveStreams.size} live streams")
            _liveStreamsFlow.value = mockLiveStreams
            Result.Success(mockLiveStreams)
        } catch (e: Exception) {
            println("DEBUG REPO: Error creating live streams: ${e.message}")
            Result.Error(e.message ?: "Failed to fetch live streams")
        }
    }

    override suspend fun getLiveStreamById(streamId: String): Result<LiveStream> {
        return try {
            println("DEBUG REPO: Getting live stream by ID: $streamId")
            // Find in mock data
            val liveStream = createMockLiveStreams().find { it.id == streamId }
            if (liveStream != null) {
                Result.Success(liveStream)
            } else {
                Result.Error("Live stream not found")
            }
        } catch (e: Exception) {
            println("DEBUG REPO: Error getting live stream: ${e.message}")
            Result.Error(e.message ?: "Failed to fetch live stream")
        }
    }

    override suspend fun joinLiveStream(streamId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Joining live stream: $streamId")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("DEBUG REPO: Error joining live stream: ${e.message}")
            Result.Error(e.message ?: "Failed to join live stream")
        }
    }

    override suspend fun leaveLiveStream(streamId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Leaving live stream: $streamId")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("DEBUG REPO: Error leaving live stream: ${e.message}")
            Result.Error(e.message ?: "Failed to leave live stream")
        }
    }

    override suspend fun sendLiveComment(streamId: String, comment: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Sending live comment to $streamId: $comment")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("DEBUG REPO: Error sending comment: ${e.message}")
            Result.Error(e.message ?: "Failed to send comment")
        }
    }

    override fun getLiveComments(streamId: String): Flow<List<LiveComment>> {
        return try {
            println("DEBUG REPO: Getting live comments for $streamId")
            // MOCK DATA FLOW
            flow {
                emit(createMockLiveComments())
            }
        } catch (e: Exception) {
            println("DEBUG REPO: Error getting comments: ${e.message}")
            flow { emit(emptyList()) }
        }
    }

    override suspend fun getViewerCount(streamId: String): Result<Int> {
        return try {
            println("DEBUG REPO: Getting viewer count for $streamId")
            // MOCK DATA
            val viewerCount = (100..5000).random()
            Result.Success(viewerCount)
        } catch (e: Exception) {
            println("DEBUG REPO: Error getting viewer count: ${e.message}")
            Result.Error(e.message ?: "Failed to get viewer count")
        }
    }

    override suspend fun getViewers(streamId: String): Result<List<String>> {
        return try {
            println("DEBUG REPO: Getting viewers for $streamId")
            // MOCK DATA
            val viewers = listOf("user1", "user2", "user3", "user4", "user5")
            Result.Success(viewers)
        } catch (e: Exception) {
            println("DEBUG REPO: Error getting viewers: ${e.message}")
            Result.Error(e.message ?: "Failed to get viewers")
        }
    }

    override suspend fun saveLiveRecording(streamId: String): Result<String> {
        return try {
            println("DEBUG REPO: Saving live recording for $streamId")
            // MOCK DATA
            val videoUrl = "https://picsum.photos/id/${streamId.hashCode() % 100}/1920/1080"
            Result.Success(videoUrl)
        } catch (e: Exception) {
            println("DEBUG REPO: Error saving recording: ${e.message}")
            Result.Error(e.message ?: "Failed to save recording")
        }
    }

    override suspend fun deleteLiveRecording(streamId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Deleting live recording for $streamId")
            Result.Success(Unit)
        } catch (e: Exception) {
            println("DEBUG REPO: Error deleting recording: ${e.message}")
            Result.Error(e.message ?: "Failed to delete recording")
        }
    }

    // Mock data creation methods
    private fun createMockLiveStreamSession(): LiveStreamSession {
        return LiveStreamSession(
            streamId = "live_${System.currentTimeMillis()}",
            rtmpUrl = "rtmp://live.example.com/app",
            streamKey = "stream_key_${System.currentTimeMillis()}",
            viewerUrl = "https://stream.example.com/watch/live_${System.currentTimeMillis()}",
            expiresAt = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000) // 24 hours from now
        )
    }

    private fun createMockLiveStreams(): List<LiveStream> {
        return listOf(
            LiveStream(
                id = "live_1",
                hostId = "user_123",
                hostUsername = "jane_doe",
                hostProfileImage = "https://picsum.photos/id/100/200/200",
                title = "Morning Coffee Chat ☕",
                thumbnailUrl = "https://picsum.photos/id/101/400/300",
                streamUrl = "https://stream.example.com/watch/live_1",
                viewerCount = 1245,
                isLive = true,
                startedAt = Date(System.currentTimeMillis() - 30 * 60 * 1000), // 30 mins ago
                endedAt = null,
                tags = listOf("coffee", "chat", "morning"),
                isPrivate = false
            ),
            LiveStream(
                id = "live_2",
                hostId = "user_456",
                hostUsername = "john_smith",
                hostProfileImage = "https://picsum.photos/id/102/200/200",
                title = "Gaming Session 🎮",
                thumbnailUrl = "https://picsum.photos/id/103/400/300",
                streamUrl = "https://stream.example.com/watch/live_2",
                viewerCount = 3567,
                isLive = true,
                startedAt = Date(System.currentTimeMillis() - 60 * 60 * 1000), // 1 hour ago
                endedAt = null,
                tags = listOf("gaming", "fortnite", "stream"),
                isPrivate = false
            ),
            LiveStream(
                id = "live_3",
                hostId = "user_789",
                hostUsername = "amanda_jones",
                hostProfileImage = "https://picsum.photos/id/104/200/200",
                title = "Music Practice 🎵",
                thumbnailUrl = "https://picsum.photos/id/105/400/300",
                streamUrl = "https://stream.example.com/watch/live_3",
                viewerCount = 890,
                isLive = true,
                startedAt = Date(System.currentTimeMillis() - 45 * 60 * 1000), // 45 mins ago
                endedAt = null,
                tags = listOf("music", "guitar", "practice"),
                isPrivate = false
            ),
            LiveStream(
                id = "live_4",
                hostId = "user_101",
                hostUsername = "david_wilson",
                hostProfileImage = "https://picsum.photos/id/106/200/200",
                title = "Cooking Show 🍳",
                thumbnailUrl = "https://picsum.photos/id/107/400/300",
                streamUrl = "https://stream.example.com/watch/live_4",
                viewerCount = 2310,
                isLive = false,
                startedAt = Date(System.currentTimeMillis() - 3 * 60 * 60 * 1000), // 3 hours ago
                endedAt = Date(System.currentTimeMillis() - 60 * 60 * 1000), // Ended 1 hour ago
                tags = listOf("cooking", "recipe", "food"),
                isPrivate = false
            )
        )
    }

    private fun createMockLiveComments(): List<LiveComment> {
        return listOf(
            LiveComment(
                id = "comment_1",
                username = "viewer1",
                text = "Great stream!",
                timestamp = System.currentTimeMillis() - 10 * 60 * 1000
            ),
            LiveComment(
                id = "comment_2",
                username = "viewer2",
                text = "Love the content! 🔥",
                timestamp = System.currentTimeMillis() - 8 * 60 * 1000
            ),
            LiveComment(
                id = "comment_3",
                username = "viewer3",
                text = "Can you show that again?",
                timestamp = System.currentTimeMillis() - 5 * 60 * 1000
            ),
            LiveComment(
                id = "comment_4",
                username = "viewer4",
                text = "Thanks for streaming!",
                timestamp = System.currentTimeMillis() - 3 * 60 * 1000
            ),
            LiveComment(
                id = "comment_5",
                username = "viewer5",
                text = "Amazing! 👏",
                timestamp = System.currentTimeMillis() - 1 * 60 * 1000
            )
        )
    }
}

// Extension functions to map from remote to domain models
private fun LiveStreamResponse.toDomain(): LiveStream {
    return LiveStream(
        id = id,
        hostId = hostId,
        hostUsername = hostUsername,
        hostProfileImage = hostProfileImage,
        title = title,
        thumbnailUrl = thumbnailUrl,
        streamUrl = streamUrl,
        viewerCount = viewerCount,
        isLive = isLive,
        startedAt = startedAt,
        endedAt = endedAt,
        tags = tags,
        isPrivate = isPrivate
    )
}

private fun LiveStreamSessionResponse.toDomain(): LiveStreamSession {
    return LiveStreamSession(
        streamId = streamId,
        rtmpUrl = rtmpUrl,
        streamKey = streamKey,
        viewerUrl = viewerUrl,
        expiresAt = expiresAt
    )
}

private fun LiveCommentResponse.toDomain(): LiveComment {
    return LiveComment(
        id = id,
        username = username,
        text = text,
        timestamp = timestamp
    )
}