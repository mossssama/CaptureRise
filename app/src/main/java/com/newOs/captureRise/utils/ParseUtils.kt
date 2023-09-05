package com.newOs.captureRise.utils

import com.newOs.captureRise.room.Alarm

class ParseUtils {

    companion object{
        fun splitAlarmTime(item: Alarm) = item.alarmTime.split(":")

        fun splitAlarmTime(alarmTime: String) = alarmTime.split(":")


        fun convertTimeStringToInt(timeStr: String): Int = timeStr.replace(":", "").toInt()

        fun convertAlarmToFullTimeFormat(inputTime: String): String {
            val parts = inputTime.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull()?.toString() ?: ""
            val minute = parts.getOrNull(1)?.toIntOrNull()?.toString() ?: ""

            val formattedHour = if (hour.isNotEmpty()) {
                if (hour.length == 1) "0$hour" else hour
            } else {
                ""
            }

            val formattedMinute = if (minute.isNotEmpty()) {
                if (minute.length == 1) "0$minute" else minute
            } else {
                ""
            }

            return if (formattedHour.isNotEmpty() && formattedMinute.isNotEmpty()) {
                "$formattedHour:$formattedMinute"
            } else {
                inputTime // Return the input string as is if it couldn't be parsed
            }
        }

        fun convertAlarmToSimplifiedFormat(inputTime: String): String {
            val parts = inputTime.split(":")
            val hour = parts[0].toIntOrNull()?.toString() ?: ""
            val minute = parts.getOrNull(1)?.toIntOrNull()?.toString() ?: ""

            return if (hour.isNotEmpty() && minute.isNotEmpty()) {
                "$hour:$minute"
            } else {
                inputTime // Return the input string as is if it couldn't be parsed
            }
        }

    }
}