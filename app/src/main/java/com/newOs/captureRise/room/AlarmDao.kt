package com.newOs.captureRise.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {
    @Insert
    suspend fun insertAlarm(alarm: Alarm)

    @Query("DELETE FROM Alarm WHERE alarmTime = :alarmTime")
    suspend fun deleteAlarm(alarmTime:String)

    @Query("UPDATE Alarm SET isEnabled = :isEnabled WHERE alarmTime = :alarmTime")
    suspend fun updateAlarmEnable(alarmTime: String, isEnabled: Boolean)

    @Query("SELECT * FROM Alarm")
    fun getAllAlarms(): List<Alarm>

    @Query("SELECT * FROM Alarm WHERE isEnabled = true")
    fun getAllSettledAlarms(): List<Alarm>

    @Query("SELECT * FROM Alarm")
    fun getAllAlarmsLiveData(): LiveData<List<Alarm>>

    @Query("DELETE FROM Alarm")
    suspend fun clearAlarms()
}



//    @Update
//    suspend fun updateAlarm(alarm: Alarm)

//    @Query("UPDATE Alarm SET alarmTime = :alarmTime WHERE alarmTime = :alarmTime AND isEnabled = :isEnabled")
//    suspend fun updateAlarmTime(alarmTime: String,isEnabled: Boolean)

//    @Query("SELECT * FROM Alarm")
//    fun getAllAlarms(): List<Alarm>