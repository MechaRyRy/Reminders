package com.meowmakers.reminders.notification

sealed class AppNotification(
    val id: Int,
    val channel: AppNotificationChannel
) {
    object ReminderNotification : AppNotification(1001, AppNotificationChannel.ReminderChannel)

    companion object {
        const val notificationIdExtraKey = "notificationId"
    }
}
