package com.android.dreamguard.data.remote.models

data class SleepScheduleListResponse(
    val status: String,
    val message: String,
    val data: List<SleepSchedule>
)

data class SleepScheduleResponse(
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
    val createdAt: String?,
    val id: String
)

fun SleepScheduleResponse.toSleepSchedule(): SleepSchedule {
    return SleepSchedule(
        id = id,
        bedTime = bedTime,
        wakeUpTime = wakeUpTime,
        wakeUpAlarm = wakeUpAlarm ?: false,
        sleepReminders = sleepReminders ?: false,
        plannedDuration = plannedDuration,
        actualBedTime = actualBedTime,
        actualWakeUpTime = actualWakeUpTime,
        actualDuration = actualDuration,
        difference = difference,
        sleepQuality = sleepQuality,
        notes = notes,
        createdAt = createdAt
    )
}
