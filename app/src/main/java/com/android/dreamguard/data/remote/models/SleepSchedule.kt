package com.android.dreamguard.data.remote.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SleepSchedule(
    val id: String,
    val bedTime: String?,
    val wakeUpTime: String?,
    val wakeUpAlarm: Boolean?,
    val sleepReminders: Boolean?,
    val plannedDuration: String?,
    val actualBedTime: String?,
    val actualWakeUpTime: String?,
    val actualDuration: String?,
    val difference: String?,
    val sleepQuality: String?,
    val notes: String?,
    val createdAt: String?
) : Parcelable
