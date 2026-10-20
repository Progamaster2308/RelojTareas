package com.timetask.watch.scheduling

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timetask.watch.TimeTaskApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val validActions = listOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED
        )

        if (intent.action in validActions) {
            val app = context.applicationContext as TimeTaskApplication
            val repository = app.repository
            val scheduler = AlarmScheduler(context)

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pendingTasks = repository.getAllPendingTasks()
                    for (task in pendingTasks) {
                        scheduler.schedule(task)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}