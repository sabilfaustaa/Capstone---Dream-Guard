package com.android.dreamguard.data.remote.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_schedule")
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val id: String, // ID dari API
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
)
