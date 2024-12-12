package com.android.dreamguard.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.dreamguard.data.local.dao.ScheduleDao
import com.android.dreamguard.data.remote.models.ScheduleEntity

@Database(entities = [ScheduleEntity::class], version = 1, exportSchema = false)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile private var instance: ScheduleDatabase? = null

        fun getInstance(context: Context): ScheduleDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ScheduleDatabase::class.java,
                    "sleep_schedule_db"
                ).build().also { instance = it }
            }
    }
}
