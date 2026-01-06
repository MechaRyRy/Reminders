package com.meowmakers.reminders.notification

import android.R
import android.app.ForegroundServiceStartNotAllowedException
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.meowmakers.reminders.Logger

class NotificationService : Service() {
    private val binder = LocalBinder()

    override fun onCreate() {
        Logger.d(NotificationService::class.java, "onCreate")
        super.onCreate()
        AppNotificationChannel.ReminderChannel.register(this)
        showNotification(AppNotification.ReminderNotification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d(NotificationService::class.java, "onStartCommand")
        showNotification(AppNotification.ReminderNotification)
        return START_STICKY
    }

    private fun showNotification(notification: AppNotification) {
        try {
            val intent =
                Intent(applicationContext, NotificationDismissedReceiver::class.java).apply {
                    putExtra(AppNotification.notificationIdExtraKey, notification.id)
                }

            val deletePendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                notification.id,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notification = NotificationCompat.Builder(
                this,
                notification.channel.id
            )
                .setSmallIcon(R.drawable.ic_menu_mylocation)
                .setContentTitle("Meow Reminder Active")
                .setContentText("Scanning for nearby locations...")
                .setSubText("Location Service")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setDeleteIntent(deletePendingIntent)
                .build()

            val serviceType = when {
                Build.VERSION.SDK_INT >= 29 -> ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
                else -> 0
            }
            ServiceCompat.startForeground(
                this,
                AppNotification.ReminderNotification.id,
                notification,
                serviceType
            )
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && e is ForegroundServiceStartNotAllowedException
            ) {
                Logger.d(
                    this::class.java,
                    "Invalid state to start a foreground service"
                )
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        Logger.d(NotificationService::class.java, "onBind")
        showNotification(AppNotification.ReminderNotification)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Logger.d(NotificationService::class.java, "onUnbind")
        return super.onUnbind(intent)
    }

    inner class LocalBinder : Binder() {
        fun getService(): NotificationService = this@NotificationService
    }
}