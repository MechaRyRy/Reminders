package com.meowmakers.reminders.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meowmakers.reminders.Logger


class NotificationDismissedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        // Your code here to handle the notification dismissal
        val notificationId = intent.getIntExtra(AppNotification.notificationIdExtraKey, 0)
        Logger.d(
            this::class.java,
            "Notification with ID $notificationId was dismissed."
        )
        // Tell the service that the notification has been swiped away and resurrect it.
    }
}