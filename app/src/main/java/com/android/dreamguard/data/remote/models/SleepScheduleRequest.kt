package com.android.dreamguard.data.remote.models

import com.google.gson.annotations.SerializedName

data class SleepScheduleRequest(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("bedTime")
    val bedTime: String? = null,

    @SerializedName("wakeUpTime")
    val wakeUpTime: String? = null,

    @SerializedName("wakeUpAlarm")
    val wakeUpAlarm: Boolean? = null,

    @SerializedName("sleepReminders")
    val sleepReminders: Boolean? = null,

    @SerializedName("actualBedTime")
    val actualBedTime: String? = null,

    @SerializedName("actualWakeUpTime")
    val actualWakeUpTime: String? = null,

    @SerializedName("sleepQuality")
    val sleepQuality: String? = null,

    @SerializedName("notes")
    val notes: String? = null,
)
