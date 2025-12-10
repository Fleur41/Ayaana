package com.sam.ayaana.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sam.ayaana.domain.repository.INotificationsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "ayaana_notifications"
        private const val CHANNEL_NAME = "Ayaana Notifications"
        private const val NOTIFICATION_ID = 1001

        // For testing notifications - make this a companion object function
        fun triggerTestNotification(context: Context, title: String, message: String) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Create notification channel for Android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Ayaana app notifications"
                    enableVibration(true)
                    vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Build notification
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with your app icon
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(longArrayOf(100, 200, 300, 400, 500))
                .build()

            // Show notification
            notificationManager.notify(NOTIFICATION_ID + 1, notification)

            Timber.d("Test notification sent: $title - $message")
        }
    }

    @Inject
    lateinit var notificationsRepository: INotificationsRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("FCM Token: $token")

        // Save token to SharedPreferences or send to your server
        CoroutineScope(Dispatchers.IO).launch {
            saveTokenToPreferences(token)
            // sendTokenToServer(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Timber.d("FCM Message received: ${remoteMessage.data}")


        // Handle data payload (when app is in foreground)
        remoteMessage.data.let { data ->
            val title = data["title"] ?: "New Notification"
            val message = data["message"] ?: ""
            val type = data["type"] ?: ""
            val userId = data["userId"] ?: ""

            // Show notification
            showNotification(title, message, type)

            // Update local notifications if app is in foreground
            CoroutineScope(Dispatchers.IO).launch {
                // You could update your repository here
                // notificationsRepository.getNotifications()
            }
        }

        // Handle notification payload (when app is in background)
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "New Notification"
            val body = notification.body ?: ""

            showNotification(title, body, "general")
        }
    }

    private fun showNotification(title: String, message: String, type: String = "general") {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Ayaana app notifications"
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
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
            .setVibrate(longArrayOf(100, 200, 300, 400, 500))
            .build()

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, notification)

        Timber.d("FCM notification shown: $title - $message")
    }

    private fun saveTokenToPreferences(token: String) {
        val sharedPref = getSharedPreferences("ayaana_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("fcm_token", token)
            apply()
        }
        Timber.d("FCM Token saved to preferences: $token")
    }
}

//package com.sam.ayaana.Utils
//
//import androidx.core.content.ContextCompat.getSystemService
//import com.sam.ayaana.domain.repository.INotificationsRepository
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class FirebaseNotificationService : FirebaseMessagingService() {
//
//    companion object {
//        private const val CHANNEL_ID = "ayaana_notifications"
//        private const val CHANNEL_NAME = "Ayaana Notifications"
//        private const val NOTIFICATION_ID = 1001
//    }
//
//    @Inject
//    lateinit var notificationsRepository: INotificationsRepository
//
//    override fun onNewToken(token: String) {
//        super.onNewToken(token)
//        // TODO: Send token to your server
//        CoroutineScope(Dispatchers.IO).launch {
//            // saveTokenToServer(token)
//        }
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        super.onMessageReceived(remoteMessage)
//
//        // Handle data payload
//        remoteMessage.data.let { data ->
//            val title = data["title"] ?: "New Notification"
//            val message = data["message"] ?: ""
//            val type = data["type"] ?: ""
//            val userId = data["userId"] ?: ""
//
//            // Show notification
//            showNotification(title, message)
//
//            // Update local notifications if app is in foreground
//            CoroutineScope(Dispatchers.IO).launch {
//                // Refresh notifications from repository
//                notificationsRepository.getNotifications()
//            }
//        }
//
//        // Handle notification payload (when app is in background)
//        remoteMessage.notification?.let { notification ->
//            val title = notification.title ?: "New Notification"
//            val body = notification.body ?: ""
//
//            showNotification(title, body)
//        }
//    }
//
//    private fun showNotification(title: String, message: String) {
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Create notification channel for Android O and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply {
//                description = "Ayaana app notifications"
//            }
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        // Build notification
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with your app icon
//            .setAutoCancel(true)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        // Show notification
//        notificationManager.notify(NOTIFICATION_ID, notification)
//    }
//
//    // Utility method to manually trigger a notification (for testing)
//    fun triggerTestNotification(context: Context, title: String, message: String) {
//        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setAutoCancel(true)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        notificationManager.notify(NOTIFICATION_ID + 1, notification)
//    }
//}