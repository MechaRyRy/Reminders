package com.meowmakers.reminders.notification

sealed class AppNotification(
    val id: Int,
    val channelId: String
) {
    object ReminderNotification : AppNotification(1001, "reminder_channel")
}