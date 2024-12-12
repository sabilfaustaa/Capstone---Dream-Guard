package com.android.dreamguard.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.capstone.dreamguard.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val title = intent.getStringExtra("TITLE") ?: "Alarm"
            val message = intent.getStringExtra("MESSAGE") ?: "It's time!"

            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val notificationBuilder = NotificationCompat.Builder(context, "ALARM_CHANNEL")
                .setSmallIcon(R.drawable.grey_clock)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())

        } catch (e: SecurityException) {
            Log.e("AlarmReceiver", "Permission issue: ${e.message}")
        }
    }
}
