package com.meowmakers.reminders.notification

sealed class AppNotification(
    val id: Int,
    val channel: AppNotificationChannel
) {
    object SummaryNotification : AppNotification(1001, AppNotificationChannel.ReminderChannel)
}
