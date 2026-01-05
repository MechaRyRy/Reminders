package com.meowmakers.reminders.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.meowmakers.reminders.Logger

class ServiceBinding(context: Context) {

    private val applicationContext = context.applicationContext

    private lateinit var service: NotificationService
    private var isBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            val binder = iBinder as NotificationService.LocalBinder
            service = binder.getService()
            isBound = true

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    fun startAndBindService() {
        Logger.d(ServiceBinding::class.java, "startAndBindService")
        val intent = Intent(applicationContext, NotificationService::class.java)
        ContextCompat.startForegroundService(applicationContext, intent)
        applicationContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
}