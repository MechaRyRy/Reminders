package com.meowmakers.reminders.notification

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.meowmakers.reminders.Logger
import com.meowmakers.reminders.R
import androidx.core.graphics.toColorInt

class NotificationService : Service() {
    private val binder = LocalBinder()

    private val tasks = mutableListOf(
        "Finish project report",
        "Buy groceries",
        "Call the doctor"
    )


    override fun onCreate() {
        super.onCreate()
        AppNotificationChannel.ReminderChannel.register(this)
        refreshNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        refreshNotification()
        return START_STICKY
    }

    fun refreshNotification() {
        val remoteViews = RemoteViews(packageName, R.layout.notification_large)

        val rowIds = listOf(R.id.slot0, R.id.slot1)
        val textIds = listOf(
            R.id.row_text_0,
            R.id.row_text_1,
        )
        val checkIds = listOf(
            R.id.row_check_0,
            R.id.row_check_1,
        )

        rowIds.forEach { remoteViews.setViewVisibility(it, View.GONE) }

        tasks.take(2).forEachIndexed { index, taskText ->
            remoteViews.setViewVisibility(rowIds[index], View.VISIBLE)
            remoteViews.setTextViewText(textIds[index], taskText)

            val checkIntent = NotificationAction.Toggle(index).asIntent(applicationContext)
            val pIntent = PendingIntent.getBroadcast(
                this, index, checkIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            remoteViews.setOnClickPendingIntent(checkIds[index], pIntent)
        }


        val swapIntent = NotificationAction.Swap.asIntent(applicationContext)
        val swapPendingIntent = PendingIntent.getBroadcast(
            this,
            100,
            swapIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setOnClickPendingIntent(R.id.btn_swap, swapPendingIntent)

        val deleteIntent = NotificationAction.Dismissed.asIntent(applicationContext)
        val deletePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            100,
            deleteIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(
            this,
            AppNotification.SummaryNotification.channel.id
        )
            .setSmallIcon(R.drawable.ic_swap)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setCustomHeadsUpContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setColorized(true)
            .setColor("#FFFFFF".toColorInt())
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDeleteIntent(deletePendingIntent)

        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        } else 0

        ServiceCompat.startForeground(
            this,
            AppNotification.SummaryNotification.id,
            builder.build(),
            serviceType
        )
    }

    override fun onBind(intent: Intent?): IBinder {
        Logger.d(NotificationService::class.java, "onBind")
        refreshNotification()
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