package com.meowmakers.reminders

import android.util.Log

object Logger {

    fun d(clazz: Class<*>, message: String) {
        Log.d("$clazz", message)
    }
}