package com.newOs.captureRise.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Alarm(
    val alarmTime: String,
    val isEnabled: Boolean,
    @PrimaryKey(autoGenerate = true) val id : Int =0
)
