package com.newOs.captureRise.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationsUtils {

    companion object {
        private const val CHANNEL_ID = "alarmId"
        private const val CHANNEL_NAME= "alarmNotification"
        private const val CHANNEL_DESCRIPTION = "alarm"

        fun launchNotification(context: Context, title: String) {
            createNotificationChannel(context)

            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(title)
                .setSound(soundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(222, builder.build())
        }

        // A notification channel must be created for this version of Android and later
        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val descriptionText = CHANNEL_DESCRIPTION
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply { description = descriptionText }

                val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

    }

}