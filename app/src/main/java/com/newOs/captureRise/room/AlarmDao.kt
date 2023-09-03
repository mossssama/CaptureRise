package com.newOs.captureRise.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {
    @Insert
    suspend fun insertAlarm(alarm: Alarm)

    @Query("DELETE FROM Alarm WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId:Int)

    @Query("UPDATE Alarm SET alarmTime = :alarmTime, isEnabled = :isEnabled WHERE id = :alarmId")
    suspend fun updateAlarm(alarmId: Int, alarmTime: String, isEnabled: Boolean)

    @Query("SELECT * FROM Alarm ORDER BY alarmTime ASC")
    fun getAllAlarmsLiveData(): LiveData<List<Alarm>>

    @Query("DELETE FROM Alarm")
    suspend fun clearAlarms()
}
