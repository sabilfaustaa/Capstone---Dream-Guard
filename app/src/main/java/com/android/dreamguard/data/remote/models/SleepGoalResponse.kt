package com.android.dreamguard.data.remote.models

import com.google.gson.annotations.SerializedName

data class SleepGoalResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SleepGoalData?
)

data class SleepGoalData(
    @SerializedName("sleepGoal") val sleepGoal: String
)
