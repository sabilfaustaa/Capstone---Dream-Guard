package com.android.dreamguard.data.repository

import android.content.Context
import com.android.dreamguard.data.remote.api.ApiConfig

class SleepScheduleRepository(private val context: Context) {
    private val apiService = ApiConfig.getApiService(context)

//    suspend fun addSleepSchedule(schedule: Map<String, Any>) =
//        apiService.addSleepSchedule(schedule)

    suspend fun getSleepSchedules() =
        apiService.getSleepSchedules()

//    suspend fun updateSleepSchedule(scheduleId: String, schedule: Map<String, Any>) =
//        apiService.updateSleepSchedule(scheduleId, schedule)

    suspend fun updateSleepGoals(goals: Map<String, Int>) =
        apiService.updateSleepGoals(goals)

    suspend fun getSleepGoals() =
        apiService.getSleepGoals()
}
