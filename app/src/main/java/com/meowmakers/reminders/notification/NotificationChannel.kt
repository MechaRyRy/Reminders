package com.meowmakers.reminders.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.meowmakers.reminders.R

sealed class AppNotificationChannel(
    val id: String,
    val nameRes: Int,
    val descriptionRes: Int,
    val importance: Int
) {
    object ReminderChannel : AppNotificationChannel(
        "reminder_channel",
        R.string.app_name,
        R.string.app_name,
        NotificationManager.IMPORTANCE_MAX
    )
}

fun AppNotificationChannel.register(
    context: Context,
) {
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            val name = context.getString(nameRes)
            val descriptionText = context.getString(descriptionRes)

            val channel = NotificationChannel(
                id,
                name,
                importance
            ).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        else -> {}
    }
}