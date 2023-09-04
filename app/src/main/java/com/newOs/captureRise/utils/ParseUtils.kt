package com.newOs.captureRise.utils

import com.newOs.captureRise.room.Alarm

class ParseUtils {

    companion object{
        fun splitAlarmTime(item: Alarm) = item.alarmTime.split(":")

        fun convertTimeStringToInt(timeStr: String): Int = timeStr.replace(":", "").toInt()
    }
}