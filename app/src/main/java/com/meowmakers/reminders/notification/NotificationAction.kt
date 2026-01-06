package com.meowmakers.reminders.notification

import android.content.Context
import android.content.Intent

sealed class NotificationAction(val value: String) {
    abstract fun asIntent(context: Context): Intent

    data object Swap : NotificationAction(ActionKeys.NOTIFICATION_SWAP_ACTION) {
        override fun asIntent(context: Context): Intent =
            Intent(context.applicationContext, NotificationActionReceiver::class.java)
                .apply {
                    action = value
                }
    }

    data object Dismissed : NotificationAction(ActionKeys.NOTIFICATION_DISMISS_ACTION) {
        override fun asIntent(context: Context): Intent =
            Intent(context.applicationContext, NotificationActionReceiver::class.java)
                .apply {
                    action = value
                }
    }

    data class Toggle(private val notificationIndex: Int) : NotificationAction(
        ActionKeys.NOTIFICATION_TOGGLE_ACTION
    ) {
        override fun asIntent(context: Context): Intent =
            Intent(context.applicationContext, NotificationActionReceiver::class.java)
                .apply {
                    action = value
                    putExtra(ExtraKeys.NOTIFICATION_INDEX_EXTRA, notificationIndex)
                }
    }

    fun Intent.asNotificationAction(): NotificationAction? {
        return when (action) {
            ActionKeys.NOTIFICATION_DISMISS_ACTION -> Dismissed
            ActionKeys.NOTIFICATION_TOGGLE_ACTION -> Toggle(
                extras!!.getInt(
                    ExtraKeys.NOTIFICATION_INDEX_EXTRA
                )
            )

            ActionKeys.NOTIFICATION_SWAP_ACTION -> Swap
            else -> null
        }
    }
}

object ActionKeys {
    const val NOTIFICATION_SWAP_ACTION: String = "notification_swap_action"
    const val NOTIFICATION_DISMISS_ACTION: String = "notification_dismiss_action"
    const val NOTIFICATION_TOGGLE_ACTION: String = "notification_toggle_action"
}

object ExtraKeys {
    const val NOTIFICATION_INDEX_EXTRA: String = "notification_index_extra"
}
