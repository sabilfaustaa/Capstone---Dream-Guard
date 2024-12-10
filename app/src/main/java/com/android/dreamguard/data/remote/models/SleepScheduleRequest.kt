package com.android.dreamguard.data.remote.models

import com.google.gson.annotations.SerializedName

data class SleepScheduleRequest(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("bedTime")
    val bedTime: String,

    @SerializedName("wakeUpTime")
    val wakeUpTime: String,

    @SerializedName("wakeUpAlarm")
    val wakeUpAlarm: Boolean,

    @SerializedName("sleepReminders")
    val sleepReminders: Boolean
)
