package com.meowmakers.reminders.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.meowmakers.reminders.Logger
import com.meowmakers.reminders.notification.NotificationAction.Swap.asNotificationAction


class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        intent.asNotificationAction()?.let {
            Logger.d(
                this::class.java,
                "NotificationActionReceiver got $it"
            )

            val serviceIntent = Intent(context, NotificationService::class.java)
            val binder = peekService(context, serviceIntent)

            if (binder != null) {
                Logger.d(
                    this::class.java,
                    "Service exists, resurrecting notification"
                )
                val myService = (binder as NotificationService.LocalBinder).getService()
                myService.refreshNotification()
            }
        }
    }
}