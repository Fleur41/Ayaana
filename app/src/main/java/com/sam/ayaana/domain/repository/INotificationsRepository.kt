
package com.sam.ayaana.domain.repository

import com.sam.ayaana.Utils.Result
import com.sam.ayaana.data.remote.api.NotificationsApi
import com.sam.ayaana.data.remote.model.response.FollowRequestResponse
import com.sam.ayaana.data.remote.model.response.NotificationResponse
import com.sam.ayaana.data.remote.model.response.SuggestedUserResponse
import com.sam.ayaana.domain.model.FollowRequest
import com.sam.ayaana.domain.model.Notification
import com.sam.ayaana.domain.model.SuggestedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject

interface INotificationsRepository {

    // Notifications
    suspend fun getNotifications(): Result<List<Notification>>
    fun getNotificationsFlow(): Flow<List<Notification>>
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>

    // Follow Requests
    suspend fun getFollowRequests(): Result<List<FollowRequest>>
    suspend fun acceptFollowRequest(requestId: String): Result<Unit>
    suspend fun deleteFollowRequest(requestId: String): Result<Unit>

    // Suggested Users
    suspend fun getSuggestedUsers(): Result<List<SuggestedUser>>
    suspend fun followSuggestedUser(userId: String): Result<Unit>
    suspend fun dismissSuggestedUser(userId: String): Result<Unit>

}

class NotificationsRepositoryImpl @Inject constructor(
    private val notificationsApi: NotificationsApi
) : INotificationsRepository {

    private val _notificationsFlow = MutableStateFlow<List<Notification>>(emptyList())

    override suspend fun getNotifications(): Result<List<Notification>> {
        return try {
            println("DEBUG REPO: Creating mock notifications...")
            // MOCK DATA FOR NOW
            val mockNotifications = createMockNotifications()
            println("DEBUG REPO: Created ${mockNotifications.size} notifications")
            _notificationsFlow.value = mockNotifications
            Result.Success(mockNotifications)
        } catch (e: Exception) {
            println("DEBUG REPO: Error creating mock notifications: ${e.message}")
            Result.Error(e.message ?: "Unknown error occurred")
        }
    }

    override fun getNotificationsFlow(): Flow<List<Notification>> {
        return _notificationsFlow.asStateFlow()
    }

    override suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        return try {
            // Update local state
            val updated = _notificationsFlow.value.map { notification ->
                if (notification.id == notificationId) notification.copy(isRead = true)
                else notification
            }
            _notificationsFlow.value = updated

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark notification as read")
        }
    }

    override suspend fun getFollowRequests(): Result<List<FollowRequest>> {
        return try {
            println("DEBUG REPO: Creating mock follow requests...")
            // MOCK DATA
            val mockFollowRequests = createMockFollowRequests()
            println("DEBUG REPO: Created ${mockFollowRequests.size} follow requests")
            Result.Success(mockFollowRequests)
        } catch (e: Exception) {
            println("DEBUG REPO: Error creating mock follow requests: ${e.message}")
            Result.Error(e.message ?: "Failed to fetch follow requests")
        }
    }

    override suspend fun acceptFollowRequest(requestId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Accepting follow request: $requestId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to accept follow request")
        }
    }

    override suspend fun deleteFollowRequest(requestId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Deleting follow request: $requestId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete follow request")
        }
    }

    override suspend fun getSuggestedUsers(): Result<List<SuggestedUser>> {
        return try {
            println("DEBUG REPO: Creating mock suggested users...")
            // MOCK DATA
            val mockSuggestedUsers = createMockSuggestedUsers()
            println("DEBUG REPO: Created ${mockSuggestedUsers.size} suggested users")
            Result.Success(mockSuggestedUsers)
        } catch (e: Exception) {
            println("DEBUG REPO: Error creating mock suggested users: ${e.message}")
            Result.Error(e.message ?: "Failed to fetch suggested users")
        }
    }

    override suspend fun followSuggestedUser(userId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Following suggested user: $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to follow user")
        }
    }

    override suspend fun dismissSuggestedUser(userId: String): Result<Unit> {
        return try {
            println("DEBUG REPO: Dismissing suggested user: $userId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to dismiss suggested user")
        }
    }

    // Mock data creation methods - FIXED VERSION
    private fun createMockNotifications(): List<Notification> {
        return listOf(
            Notification(
                id = "1",
                type = Notification.NotificationType.FOLLOW_REQUEST,
                title = "Follow requests",
                message = "ryan_mon123 + 5 others want to follow you",
                userProfileImage = null,
                userName = null,
                postImage = null,
                targetUserId = null,
                postId = null,
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2), // 2 hours ago
                actionRequired = true
            ),
            Notification(
                id = "2",
                type = Notification.NotificationType.SUGGESTED_FRIEND,
                title = "tim_mantis01",
                message = "Followed by jane_smith + 2 others",
                userProfileImage = "https://picsum.photos/id/237/200/200",
                userName = "tim_mantis01",
                postImage = null,
                targetUserId = null,
                postId = null,
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60), // 1 hour ago
                actionRequired = false
            ),
            Notification(
                id = "3",
                type = Notification.NotificationType.LIKE,
                title = "john_doe",
                message = "liked your photo",
                userProfileImage = "https://picsum.photos/id/1005/200/200",
                userName = "john_doe",
                postImage = "https://picsum.photos/id/1006/400/400",
                targetUserId = null,
                postId = "post_123",
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 30), // 30 mins ago
                actionRequired = false
            ),
            Notification(
                id = "4",
                type = Notification.NotificationType.COMMENT,
                title = "jane_smith",
                message = "commented: 'This is amazing! 😍'",
                userProfileImage = "https://picsum.photos/id/1011/200/200",
                userName = "jane_smith",
                postImage = "https://picsum.photos/id/1012/400/400",
                targetUserId = null,
                postId = "post_456",
                isRead = true,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 3), // 3 hours ago
                actionRequired = false
            ),
            Notification(
                id = "5",
                type = Notification.NotificationType.NEW_POST,
                title = "amanda_jones",
                message = "just shared their first post",
                userProfileImage = "https://picsum.photos/id/1015/200/200",
                userName = "amanda_jones",
                postImage = "https://picsum.photos/id/1016/400/400",
                targetUserId = null,
                postId = "post_789",
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 5), // 5 hours ago
                actionRequired = false
            ),
            Notification(
                id = "6",
                type = Notification.NotificationType.FOLLOW_ACCEPTED,
                title = "alex_walker",
                message = "started following you",
                userProfileImage = "https://picsum.photos/id/1020/200/200",
                userName = "alex_walker",
                postImage = null,
                targetUserId = null,
                postId = null,
                isRead = false,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24), // 1 day ago
                actionRequired = false
            ),
            Notification(
                id = "7",
                type = Notification.NotificationType.MENTION,
                title = "michael_brown",
                message = "mentioned you in a story",
                userProfileImage = "https://picsum.photos/id/1025/200/200",
                userName = "michael_brown",
                postImage = "https://picsum.photos/id/1027/400/400",
                targetUserId = null,
                postId = "post_999",
                isRead = true,
                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 48), // 2 days ago
                actionRequired = false
            )
        )
    }
//    private fun createMockNotifications(): List<Notification> {
//        return listOf(
//            Notification(
//                id = "1",
//                type = Notification.NotificationType.FOLLOW_REQUEST,
//                title = "Follow requests",
//                message = "ryan_mon123 + 5 others",
//                userProfileImage = null,
//                userName = null,
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 2), // 2 hours ago
//                actionRequired = true
//            ),
//            Notification(
//                id = "2",
//                type = Notification.NotificationType.SUGGESTED_FRIEND,
//                title = "tim_mantis01",
//                message = "who you might know, is on Instagram",
//                userProfileImage = "https://picsum.photos/id/101/200/200",
//                userName = "tim_mantis01",
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24), // 1 day ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "3",
//                type = Notification.NotificationType.LIKE,
//                title = "john_doe",
//                message = "liked your post",
//                userProfileImage = "https://picsum.photos/id/102/200/200",
//                userName = "john_doe",
//                postImage = "https://picsum.photos/id/103/400/400",
//                targetUserId = null,
//                postId = "post_123",
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 3), // 3 hours ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "4",
//                type = Notification.NotificationType.COMMENT,
//                title = "jane_smith",
//                message = "commented: 'Great shot! 📸'",
//                userProfileImage = "https://picsum.photos/id/104/200/200",
//                userName = "jane_smith",
//                postImage = "https://picsum.photos/id/105/400/400",
//                targetUserId = null,
//                postId = "post_456",
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 48), // 2 days ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "5",
//                type = Notification.NotificationType.NEW_POST,
//                title = "amanda_jones",
//                message = "posted for the first time in a while",
//                userProfileImage = "https://picsum.photos/id/106/200/200",
//                userName = "amanda_jones",
//                postImage = null,
//                targetUserId = null,
//                postId = "post_789",
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 72), // 3 days ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "6",
//                type = Notification.NotificationType.FOLLOW_ACCEPTED,
//                title = "alex_walker",
//                message = "accepted your follow request",
//                userProfileImage = "https://picsum.photos/id/107/200/200",
//                userName = "alex_walker",
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 5), // 5 hours ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "7",
//                type = Notification.NotificationType.MENTION,
//                title = "michael_brown",
//                message = "mentioned you in a comment",
//                userProfileImage = "https://picsum.photos/id/108/200/200",
//                userName = "michael_brown",
//                postImage = "https://picsum.photos/id/109/400/400",
//                targetUserId = null,
//                postId = "post_999",
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 12), // 12 hours ago
//                actionRequired = false
//            )
//        )
//    }

    private fun createMockFollowRequests(): List<FollowRequest> {
        return listOf(
            FollowRequest(
                id = "1",
                userId = "ndi.gi",
                userName = "ndi.gi",
                fullName = "Ndiqirigi G",
                profileImage = "https://picsum.photos/id/110/200/200",
                mutualFollowers = listOf("user1", "user2"),
                timestamp = Date()
            ),
            FollowRequest(
                id = "2",
                userId = "njenga_ndiran",
                userName = "njenga_ndiran...",
                fullName = "Njenga Ndira",
                profileImage = "https://picsum.photos/id/111/200/200",
                mutualFollowers = listOf("user3"),
                timestamp = Date()
            ),
            FollowRequest(
                id = "3",
                userId = "antonymbugu",
                userName = "antonymbugu...",
                fullName = "antonio 27",
                profileImage = "https://picsum.photos/id/112/200/200",
                mutualFollowers = listOf("user4", "user5", "user6"),
                timestamp = Date()
            ),
            FollowRequest(
                id = "4",
                userId = "taji_luxe",
                userName = "taji_luxe",
                fullName = "FASHION STORE",
                profileImage = "https://picsum.photos/id/113/200/200",
                mutualFollowers = emptyList(),
                timestamp = Date()
            ),
            FollowRequest(
                id = "5",
                userId = "ryan_mon123",
                userName = "ryan_mon123",
                fullName = "Ryan",
                profileImage = "https://picsum.photos/id/114/200/200",
                mutualFollowers = listOf("user7"),
                timestamp = Date()
            ),
            FollowRequest(
                id = "6",
                userId = "shivan_wamuyu",
                userName = "shivan_wamuyu",
                fullName = "Shivan Wamuyu",
                profileImage = "https://picsum.photos/id/115/200/200",
                mutualFollowers = listOf("user8", "user9"),
                timestamp = Date()
            ),
            FollowRequest(
                id = "7",
                userId = "juma_allan",
                userName = "juma_allan",
                fullName = "Juma Allan 💧",
                profileImage = "https://picsum.photos/id/116/200/200",
                mutualFollowers = listOf("user10"),
                timestamp = Date()
            )
        )
    }

    private fun createMockSuggestedUsers(): List<SuggestedUser> {
        return listOf(
            SuggestedUser(
                id = "1",
                userName = "Rakesh Rd Diwan",
                fullName = "Rakesh Rd Diwan",
                profileImage = "https://picsum.photos/id/117/200/200",
                mutualConnections = emptyList(),
                reason = "Suggested for you"
            ),
            SuggestedUser(
                id = "2",
                userName = "Amber Ray",
                fullName = "Amber Ray",
                profileImage = "https://picsum.photos/id/118/200/200",
                mutualConnections = listOf("mckaruri", "user5", "user6", "user7", "user8"),
                reason = "Followed by mckaruri + 18 more"
            ),
            SuggestedUser(
                id = "3",
                userName = "John Ngatia",
                fullName = "John Ngatia",
                profileImage = "https://picsum.photos/id/119/200/200",
                mutualConnections = listOf("ditchen"),
                reason = "Following ditchen + 1 more"
            ),
            SuggestedUser(
                id = "4",
                userName = "Kezia.Maringa",
                fullName = "Kezia Maringa",
                profileImage = "https://picsum.photos/id/120/200/200",
                mutualConnections = emptyList(),
                reason = "Popular in your area"
            ),
            SuggestedUser(
                id = "5",
                userName = "Sarah Johnson",
                fullName = "Sarah Johnson",
                profileImage = "https://picsum.photos/id/121/200/200",
                mutualConnections = listOf("friend1", "friend2", "friend3"),
                reason = "Followed by 3 of your friends"
            ),
            SuggestedUser(
                id = "6",
                userName = "David Kim",
                fullName = "David Kim",
                profileImage = "https://picsum.photos/id/122/200/200",
                mutualConnections = emptyList(),
                reason = "Trending in photography"
            ),
            SuggestedUser(
                id = "7",
                userName = "Lisa Park",
                fullName = "Lisa Park",
                profileImage = "https://picsum.photos/id/123/200/200",
                mutualConnections = listOf("colleague1", "colleague2"),
                reason = "Works at TechCorp"
            )
        )
    }
}

// Extension functions to map from remote to domain models
private fun NotificationResponse.toDomain(): Notification {
    return Notification(
        id = id,
        type = Notification.NotificationType.valueOf(type),
        title = title,
        message = message,
        userProfileImage = userProfileImage,
        userName = userName,
        postImage = postImage,
        targetUserId = targetUserId,
        postId = postId,
        isRead = isRead,
        timestamp = timestamp,
        actionRequired = actionRequired
    )
}

private fun FollowRequestResponse.toDomain(): FollowRequest {
    return FollowRequest(
        id = id,
        userId = userId,
        userName = userName,
        fullName = fullName,
        profileImage = profileImage,
        mutualFollowers = mutualFollowers,
        timestamp = timestamp
    )
}

private fun SuggestedUserResponse.toDomain(): SuggestedUser {
    return SuggestedUser(
        id = id,
        userName = userName,
        fullName = fullName,
        profileImage = profileImage,
        mutualConnections = mutualConnections,
        reason = reason
    )
}


//
//package com.sam.ayaana.domain.repository
//
//import com.sam.ayaana.Utils.Result
//import com.sam.ayaana.data.remote.api.NotificationsApi
//import com.sam.ayaana.data.remote.model.response.FollowRequestResponse
//import com.sam.ayaana.data.remote.model.response.NotificationResponse
//import com.sam.ayaana.data.remote.model.response.SuggestedUserResponse
//import com.sam.ayaana.domain.model.FollowRequest
//import com.sam.ayaana.domain.model.Notification
//import com.sam.ayaana.domain.model.SuggestedUser
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import java.util.Date
//import javax.inject.Inject
//
//interface INotificationsRepository {
//
//    // Notifications
//    suspend fun getNotifications(): Result<List<Notification>>
//    fun getNotificationsFlow(): Flow<List<Notification>>
//    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>
//
//    // Follow Requests
//    suspend fun getFollowRequests(): Result<List<FollowRequest>>
//    suspend fun acceptFollowRequest(requestId: String): Result<Unit>
//    suspend fun deleteFollowRequest(requestId: String): Result<Unit>
//
//    // Suggested Users
//    suspend fun getSuggestedUsers(): Result<List<SuggestedUser>>
//    suspend fun followSuggestedUser(userId: String): Result<Unit>
//    suspend fun dismissSuggestedUser(userId: String): Result<Unit>
//
//}
//
//class NotificationsRepositoryImpl @Inject constructor(
//    private val notificationsApi: NotificationsApi
//) : INotificationsRepository {
//
//    private val _notificationsFlow = MutableStateFlow<List<Notification>>(emptyList())
//
//    override suspend fun getNotifications(): Result<List<Notification>> {
//        return try {
//            println("DEBUG REPO: Creating mock notifications...")
//            // MOCK DATA FOR NOW
//            val mockNotifications = createMockNotifications()
//            println("DEBUG REPO: Created ${mockNotifications.size} notifications")
//            _notificationsFlow.value = mockNotifications
//            Result.Success(mockNotifications)
//        } catch (e: Exception) {
//            println("DEBUG REPO: Error creating mock notifications: ${e.message}")
//            Result.Error(e.message ?: "Unknown error occurred")
//        }
//    }
//
//    override fun getNotificationsFlow(): Flow<List<Notification>> {
//        return _notificationsFlow.asStateFlow()
//    }
//
//    override suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
//        return try {
//            // Update local state
//            val updated = _notificationsFlow.value.map { notification ->
//                if (notification.id == notificationId) notification.copy(isRead = true)
//                else notification
//            }
//            _notificationsFlow.value = updated
//
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Failed to mark notification as read")
//        }
//    }
//
//    override suspend fun getFollowRequests(): Result<List<FollowRequest>> {
//        return try {
//            println("DEBUG REPO: Creating mock follow requests...")
//            // MOCK DATA
//            val mockFollowRequests = createMockFollowRequests()
//            println("DEBUG REPO: Created ${mockFollowRequests.size} follow requests")
//            Result.Success(mockFollowRequests)
//        } catch (e: Exception) {
//            println("DEBUG REPO: Error creating mock follow requests: ${e.message}")
//            Result.Error(e.message ?: "Failed to fetch follow requests")
//        }
//    }
//
//    override suspend fun acceptFollowRequest(requestId: String): Result<Unit> {
//        return try {
//            println("DEBUG REPO: Accepting follow request: $requestId")
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Failed to accept follow request")
//        }
//    }
//
//    override suspend fun deleteFollowRequest(requestId: String): Result<Unit> {
//        return try {
//            println("DEBUG REPO: Deleting follow request: $requestId")
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Failed to delete follow request")
//        }
//    }
//
//    override suspend fun getSuggestedUsers(): Result<List<SuggestedUser>> {
//        return try {
//            println("DEBUG REPO: Creating mock suggested users...")
//            // MOCK DATA
//            val mockSuggestedUsers = createMockSuggestedUsers()
//            println("DEBUG REPO: Created ${mockSuggestedUsers.size} suggested users")
//            Result.Success(mockSuggestedUsers)
//        } catch (e: Exception) {
//            println("DEBUG REPO: Error creating mock suggested users: ${e.message}")
//            Result.Error(e.message ?: "Failed to fetch suggested users")
//        }
//    }
//
//    override suspend fun followSuggestedUser(userId: String): Result<Unit> {
//        return try {
//            println("DEBUG REPO: Following suggested user: $userId")
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Failed to follow user")
//        }
//    }
//
//    override suspend fun dismissSuggestedUser(userId: String): Result<Unit> {
//        return try {
//            println("DEBUG REPO: Dismissing suggested user: $userId")
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            Result.Error(e.message ?: "Failed to dismiss suggested user")
//        }
//    }
//
//    // Mock data creation methods - UPDATED FOR INSTAGRAM STYLE
//    private fun createMockNotifications(): List<Notification> {
//        return listOf(
//            // TODAY'S NOTIFICATIONS
//            Notification(
//                id = "1",
//                type = Notification.NotificationType.FOLLOW_REQUEST,
//                title = "ndi.gi",
//                message = "Requested to follow you",
//                userProfileImage = "https://picsum.photos/id/237/200/200",
//                userName = "ndi.gi",
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 30), // 30 mins ago
//                actionRequired = true
//            ),
//            Notification(
//                id = "2",
//                type = Notification.NotificationType.FOLLOW_REQUEST,
//                title = "njenga_ndiran",
//                message = "Requested to follow you",
//                userProfileImage = "https://picsum.photos/id/1005/200/200",
//                userName = "njenga_ndiran",
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 45), // 45 mins ago
//                actionRequired = true
//            ),
//            Notification(
//                id = "3",
//                type = Notification.NotificationType.FOLLOW_REQUEST,
//                title = "ryan_mon123",
//                message = "Requested to follow you",
//                userProfileImage = "https://picsum.photos/id/1011/200/200",
//                userName = "ryan_mon123",
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60), // 1 hour ago
//                actionRequired = true
//            ),
//            Notification(
//                id = "4",
//                type = Notification.NotificationType.LIKE,
//                title = "john_doe",
//                message = "liked your photo",
//                userProfileImage = "https://picsum.photos/id/1015/200/200",
//                userName = "john_doe",
//                postImage = "https://picsum.photos/id/1016/400/400",
//                targetUserId = null,
//                postId = "post_123",
//                isRead = false,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 90), // 1.5 hours ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "5",
//                type = Notification.NotificationType.COMMENT,
//                title = "jane_smith",
//                message = "commented: 'Amazing view! 😍'",
//                userProfileImage = "https://picsum.photos/id/1020/200/200",
//                userName = "jane_smith",
//                postImage = "https://picsum.photos/id/1021/400/400",
//                targetUserId = null,
//                postId = "post_456",
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 120), // 2 hours ago
//                actionRequired = false
//            ),
//
//            // YESTERDAY'S NOTIFICATIONS
//            Notification(
//                id = "6",
//                type = Notification.NotificationType.SUGGESTED_FRIEND,
//                title = "tim_mantis01",
//                message = "Followed by mckaruri + 2 others",
//                userProfileImage = "https://picsum.photos/id/1025/200/200",
//                userName = "tim_mantis01",
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24), // 1 day ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "7",
//                type = Notification.NotificationType.FOLLOW_ACCEPTED,
//                title = "alex_walker",
//                message = "started following you",
//                userProfileImage = "https://picsum.photos/id/1030/200/200",
//                userName = "alex_walker",
//                postImage = null,
//                targetUserId = null,
//                postId = null,
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 26), // 1 day 2 hours ago
//                actionRequired = false
//            ),
//
//            // LAST 7 DAYS NOTIFICATIONS
//            Notification(
//                id = "8",
//                type = Notification.NotificationType.MENTION,
//                title = "michael_brown",
//                message = "mentioned you in their story",
//                userProfileImage = "https://picsum.photos/id/1035/200/200",
//                userName = "michael_brown",
//                postImage = "https://picsum.photos/id/1036/400/400",
//                targetUserId = null,
//                postId = "post_789",
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 72), // 3 days ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "9",
//                type = Notification.NotificationType.NEW_POST,
//                title = "amanda_jones",
//                message = "just shared a new post",
//                userProfileImage = "https://picsum.photos/id/1040/200/200",
//                userName = "amanda_jones",
//                postImage = "https://picsum.photos/id/1041/400/400",
//                targetUserId = null,
//                postId = "post_999",
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 96), // 4 days ago
//                actionRequired = false
//            ),
//            Notification(
//                id = "10",
//                type = Notification.NotificationType.LIKE,
//                title = "sarah_johnson",
//                message = "liked your story",
//                userProfileImage = "https://picsum.photos/id/1045/200/200",
//                userName = "sarah_johnson",
//                postImage = "https://picsum.photos/id/1046/400/400",
//                targetUserId = null,
//                postId = "story_123",
//                isRead = true,
//                timestamp = Date(System.currentTimeMillis() - 1000 * 60 * 60 * 120), // 5 days ago
//                actionRequired = false
//            )
//        )
//    }
//
//    private fun createMockFollowRequests(): List<FollowRequest> {
//        return listOf(
//            FollowRequest(
//                id = "1",
//                userId = "ndi.gi",
//                userName = "ndi.gi",
//                fullName = "Ndiqirigi G",
//                profileImage = "https://picsum.photos/id/237/200/200",
//                mutualFollowers = listOf("user1", "user2"),
//                timestamp = Date()
//            ),
//            FollowRequest(
//                id = "2",
//                userId = "njenga_ndiran",
//                userName = "njenga_ndiran...",
//                fullName = "Njenga Ndira",
//                profileImage = "https://picsum.photos/id/1005/200/200",
//                mutualFollowers = listOf("user3"),
//                timestamp = Date()
//            ),
//            FollowRequest(
//                id = "3",
//                userId = "antonymbugu",
//                userName = "antonymbugu...",
//                fullName = "antonio 27",
//                profileImage = "https://picsum.photos/id/1011/200/200",
//                mutualFollowers = listOf("user4", "user5", "user6"),
//                timestamp = Date()
//            ),
//            FollowRequest(
//                id = "4",
//                userId = "taji_luxe",
//                userName = "taji_luxe",
//                fullName = "FASHION STORE",
//                profileImage = "https://picsum.photos/id/1015/200/200",
//                mutualFollowers = emptyList(),
//                timestamp = Date()
//            ),
//            FollowRequest(
//                id = "5",
//                userId = "ryan_mon123",
//                userName = "ryan_mon123",
//                fullName = "Ryan",
//                profileImage = "https://picsum.photos/id/1020/200/200",
//                mutualFollowers = listOf("user7"),
//                timestamp = Date()
//            )
//        )
//    }
//
//    private fun createMockSuggestedUsers(): List<SuggestedUser> {
//        return listOf(
//            SuggestedUser(
//                id = "1",
//                userName = "Rakesh Rd Diwan",
//                fullName = "Rakesh Rd Diwan",
//                profileImage = "https://picsum.photos/id/1025/200/200",
//                mutualConnections = emptyList(),
//                reason = "Suggested for you"
//            ),
//            SuggestedUser(
//                id = "2",
//                userName = "Amber Ray",
//                fullName = "Amber Ray",
//                profileImage = "https://picsum.photos/id/1030/200/200",
//                mutualConnections = listOf("mckaruri", "user5", "user6", "user7", "user8"),
//                reason = "Followed by mckaruri + 18 more"
//            ),
//            SuggestedUser(
//                id = "3",
//                userName = "John Ngatia",
//                fullName = "John Ngatia",
//                profileImage = "https://picsum.photos/id/1035/200/200",
//                mutualConnections = listOf("ditchen"),
//                reason = "Following ditchen + 1 more"
//            ),
//            SuggestedUser(
//                id = "4",
//                userName = "Kezia.Maringa",
//                fullName = "Kezia Maringa",
//                profileImage = "https://picsum.photos/id/1040/200/200",
//                mutualConnections = emptyList(),
//                reason = "Popular in your area"
//            )
//        )
//    }
//}
//
//// Extension functions to map from remote to domain models
//private fun NotificationResponse.toDomain(): Notification {
//    return Notification(
//        id = id,
//        type = Notification.NotificationType.valueOf(type),
//        title = title,
//        message = message,
//        userProfileImage = userProfileImage,
//        userName = userName,
//        postImage = postImage,
//        targetUserId = targetUserId,
//        postId = postId,
//        isRead = isRead,
//        timestamp = timestamp,
//        actionRequired = actionRequired
//    )
//}
//
//private fun FollowRequestResponse.toDomain(): FollowRequest {
//    return FollowRequest(
//        id = id,
//        userId = userId,
//        userName = userName,
//        fullName = fullName,
//        profileImage = profileImage,
//        mutualFollowers = mutualFollowers,
//        timestamp = timestamp
//    )
//}
//
//private fun SuggestedUserResponse.toDomain(): SuggestedUser {
//    return SuggestedUser(
//        id = id,
//        userName = userName,
//        fullName = fullName,
//        profileImage = profileImage,
//        mutualConnections = mutualConnections,
//        reason = reason
//    )
//}
