package com.android.dreamguard.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.dreamguard.data.remote.models.ScheduleEntity

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity)

    @Query("SELECT * FROM sleep_schedule")
    fun getAllSchedules(): LiveData<List<ScheduleEntity>>

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity)
}
