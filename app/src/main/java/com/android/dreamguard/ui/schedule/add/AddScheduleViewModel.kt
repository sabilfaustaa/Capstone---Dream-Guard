package com.android.dreamguard.ui.schedule.add

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.dreamguard.data.local.database.ScheduleDatabase
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.ScheduleEntity
import com.android.dreamguard.data.remote.models.SleepScheduleRequest
import com.android.dreamguard.utils.AlarmReceiver
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddScheduleViewModel(private val context: Context) : ViewModel() {
    private val dao = ScheduleDatabase.getInstance(context).scheduleDao()

    private val _addScheduleResult = MutableLiveData<Boolean>()
    val addScheduleResult: LiveData<Boolean> = _addScheduleResult

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        createNotificationChannel()
    }

    fun addSleepSchedule(bedTime: String, wakeUpTime: String, wakeUpAlarm: Boolean, sleepReminders: Boolean) {
        val apiService = ApiConfig.getApiService(context)

        val request = SleepScheduleRequest(
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            wakeUpAlarm = wakeUpAlarm,
            sleepReminders = sleepReminders
        )

        viewModelScope.launch {
            try {
                val response = apiService.addSleepSchedule(request)
                if (response.isSuccessful) {
                    _addScheduleResult.value = true
                    val apiSchedule = response.body()
                    apiSchedule?.let {
                        val localSchedule = ScheduleEntity(
                            id = it.id,
                            bedTime = it.bedTime,
                            wakeUpTime = it.wakeUpTime,
                            wakeUpAlarm = it.wakeUpAlarm,
                            sleepReminders = it.sleepReminders,
                            plannedDuration = it.plannedDuration,
                            actualBedTime = it.actualBedTime,
                            actualWakeUpTime = it.actualWakeUpTime,
                            actualDuration = it.actualDuration,
                            difference = it.difference,
                            sleepQuality = it.sleepQuality,
                            notes = it.notes,
                            createdAt = it.createdAt
                        )
                        dao.insertSchedule(localSchedule)

                        if (wakeUpAlarm || sleepReminders) {
                            setAlarm(localSchedule)
                        } else {
                            cancelAlarm(localSchedule)
                        }
                    }
                } else {
                    _addScheduleResult.value = false
                    _errorMessage.value = "Failed to add schedule: ${response.message()}"
                }
            } catch (e: Exception) {
                _addScheduleResult.value = false
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateSleepSchedule(scheduleId: String, bedTime: String, wakeUpTime: String, wakeUpAlarm: Boolean, sleepReminders: Boolean) {
        val apiService = ApiConfig.getApiService(context)

        val request = SleepScheduleRequest(
            id = scheduleId,
            bedTime = bedTime,
            wakeUpTime = wakeUpTime,
            wakeUpAlarm = wakeUpAlarm,
            sleepReminders = sleepReminders
        )

        viewModelScope.launch {
            try {
                val response = apiService.updateSleepSchedule(scheduleId, request)
                if (response.isSuccessful) {
                    val updatedSchedule = ScheduleEntity(
                        id = scheduleId,
                        bedTime = bedTime,
                        wakeUpTime = wakeUpTime,
                        wakeUpAlarm = wakeUpAlarm,
                        sleepReminders = sleepReminders,
                        plannedDuration = null,
                        actualBedTime = null,
                        actualWakeUpTime = null,
                        actualDuration = null,
                        difference = null,
                        sleepQuality = null,
                        notes = null,
                        createdAt = null
                    )
                    dao.insertSchedule(updatedSchedule)

                    if (wakeUpAlarm || sleepReminders) {
                        setAlarm(updatedSchedule)
                    } else {
                        cancelAlarm(updatedSchedule)
                    }
                } else {
                    _errorMessage.value = "Failed to update schedule: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.postValue("Error: ${e.message}")
            }
        }
    }

    private fun setAlarm(schedule: ScheduleEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = Calendar.getInstance()

        // Set Bedtime Alarm
        schedule.bedTime?.let { bedTime ->
            val bedTimeCalendar = parseTimeToCalendar(bedTime)
            if (bedTimeCalendar.after(now)) {
                val bedTimeIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("TITLE", "Time to Sleep!")
                    putExtra("MESSAGE", "It's bedtime, start preparing for sleep.")
                }
                val bedTimePendingIntent = PendingIntent.getBroadcast(
                    context,
                    schedule.id.hashCode() * 2,
                    bedTimeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    bedTimeCalendar.timeInMillis,
                    bedTimePendingIntent
                )
                Log.d("AlarmManager", "Bedtime alarm set for ${bedTimeCalendar.time}")
            } else {
                Log.d("AlarmManager", "Bedtime is in the past. Alarm not set.")
            }
        }

        // Set Wake Up Alarm
        schedule.wakeUpTime?.let { wakeUpTime ->
            val wakeUpCalendar = parseTimeToCalendar(wakeUpTime)
            if (wakeUpCalendar.after(now)) {
                val wakeUpIntent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("TITLE", "Wake Up!")
                    putExtra("MESSAGE", "It's time to wake up and start your day.")
                }
                val wakeUpPendingIntent = PendingIntent.getBroadcast(
                    context,
                    schedule.id.hashCode() * 2 + 1,
                    wakeUpIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.RTC_WAKEUP,
                    wakeUpCalendar.timeInMillis,
                    wakeUpPendingIntent
                )
                Log.d("AlarmManager", "Wake up alarm set for ${wakeUpCalendar.time}")
            } else {
                Log.d("AlarmManager", "Wake up time is in the past. Alarm not set.")
            }
        }
    }

    private fun cancelAlarm(schedule: ScheduleEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel Bedtime Alarm
        val bedTimeIntent = Intent(context, AlarmReceiver::class.java)
        val bedTimePendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode() * 2,
            bedTimeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(bedTimePendingIntent)
        Log.d("AlarmManager", "Bedtime alarm canceled.")

        // Cancel Wake Up Alarm
        val wakeUpIntent = Intent(context, AlarmReceiver::class.java)
        val wakeUpPendingIntent = PendingIntent.getBroadcast(
            context,
            schedule.id.hashCode() * 2 + 1,
            wakeUpIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(wakeUpPendingIntent)
        Log.d("AlarmManager", "Wake up alarm canceled.")
    }

    private fun parseTimeToCalendar(time: String): Calendar {
        val format = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val parsedDate = format.parse(time) ?: throw IllegalArgumentException("Invalid time format")
        val now = Calendar.getInstance()

        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, parsedDate.hours) // Set jam dari parsedDate
            set(Calendar.MINUTE, parsedDate.minutes)  // Set menit dari parsedDate
            set(Calendar.SECOND, 0)                   // Reset detik ke 0
            set(Calendar.MILLISECOND, 0)              // Reset milidetik ke 0
            set(Calendar.YEAR, now.get(Calendar.YEAR))
            set(Calendar.MONTH, now.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH))

            if (before(now)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "ALARM_CHANNEL"
            val channelName = "Alarm Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel for alarm notifications"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
