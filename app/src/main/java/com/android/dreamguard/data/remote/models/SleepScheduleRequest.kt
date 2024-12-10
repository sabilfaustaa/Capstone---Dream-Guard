package com.android.dreamguard.data.remote.models

data class SleepScheduleRequest(
    val bedTime: String,
    val wakeUpTime: String,
    val wakeUpAlarm: Boolean,
    val sleepReminders: Boolean
)
