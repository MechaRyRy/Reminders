package com.meowmakers.reminders.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meowmakers.reminders.Logger


class NotificationDismissedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        val notificationId = intent.getIntExtra(AppNotification.notificationIdExtraKey, 0)
        Logger.d(
            this::class.java,
            "Notification with ID $notificationId was dismissed."
        )


        val serviceIntent = Intent(context, NotificationService::class.java)
        val binder = peekService(context, serviceIntent)

        if (binder != null) {
            Logger.d(
                this::class.java,
                "Service exists, resurrecting notification"
            )
            val myService = (binder as NotificationService.LocalBinder).getService()
            myService.showNotification(AppNotification.ReminderNotification)
        }
    }
}