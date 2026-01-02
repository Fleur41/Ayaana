package com.sam.ayaana.domain.model


import java.util.Date

data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val userProfileImage: String? = null,
    val userName: String? = null,
    val postImage: String? = null,
    val targetUserId: String? = null,
    val postId: String? = null,
    val isRead: Boolean,
    val timestamp: Date,
    val actionRequired: Boolean = false,
    val timeGroup: TimeGroup = TimeGroup.getGroup(timestamp)
) {
    enum class NotificationType {
        FOLLOW_REQUEST,
        FOLLOW_ACCEPTED,
        NEW_POST,
        LIKE,
        COMMENT,
        MENTION,
        SUGGESTED_FRIEND,
        LIVE_STREAM
    }

    enum class TimeGroup {
        TODAY,
        YESTERDAY,
        LAST_7_DAYS,
        LAST_30_DAYS,
        OLDER;

        companion object {
            fun getGroup(timestamp: Date): TimeGroup {
                val now = Date()
                val diff = now.time - timestamp.time
                val days = diff / (1000 * 60 * 60 * 24)

                return when {
                    days < 1 -> TODAY
                    days < 2 -> YESTERDAY
                    days < 8 -> LAST_7_DAYS
                    days < 31 -> LAST_30_DAYS
                    else -> OLDER
                }
            }
        }
    }
}