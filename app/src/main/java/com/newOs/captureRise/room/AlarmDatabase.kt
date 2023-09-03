package com.newOs.captureRise.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Alarm::class], version = 2)
abstract class AlarmDatabase: RoomDatabase() {
    abstract val alarmDao: AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        fun getInstance(context: Context): AlarmDatabase = INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AlarmDatabase::class.java, "alarm_database").fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
    }

}