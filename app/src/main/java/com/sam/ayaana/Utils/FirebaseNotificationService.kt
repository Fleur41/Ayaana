package com.sam.ayaana.Utils

import androidx.core.content.ContextCompat.getSystemService
import com.sam.ayaana.domain.repository.INotificationsRepository
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "ayaana_notifications"
        private const val CHANNEL_NAME = "Ayaana Notifications"
        private const val NOTIFICATION_ID = 1001
    }

    @Inject
    lateinit var notificationsRepository: INotificationsRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO: Send token to your server
        CoroutineScope(Dispatchers.IO).launch {
            // saveTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle data payload
        remoteMessage.data.let { data ->
            val title = data["title"] ?: "New Notification"
            val message = data["message"] ?: ""
            val type = data["type"] ?: ""
            val userId = data["userId"] ?: ""

            // Show notification
            showNotification(title, message)

            // Update local notifications if app is in foreground
            CoroutineScope(Dispatchers.IO).launch {
                // Refresh notifications from repository
                notificationsRepository.getNotifications()
            }
        }

        // Handle notification payload (when app is in background)
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "New Notification"
            val body = notification.body ?: ""

            showNotification(title, body)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Ayaana app notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with your app icon
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    // Utility method to manually trigger a notification (for testing)
    fun triggerTestNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID + 1, notification)
    }
}