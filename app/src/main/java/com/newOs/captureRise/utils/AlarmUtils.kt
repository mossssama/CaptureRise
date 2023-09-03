package com.newOs.captureRise.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.newOs.captureRise.receivers.AlarmReceiver
import com.newOs.captureRise.utils.AlarmUtils.Extras.Companion.HOURS
import java.util.*

class AlarmUtils {

    class Extras {
        companion object {
            const val HOURS = "hour"
        }
    }

    companion object {

        fun enableAlarm(context: Context, calendar: Calendar, hour: String, alarmCode: Int) {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java).apply { putExtra(HOURS, hour) }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
            val pendingIntent = PendingIntent.getBroadcast(context, alarmCode, intent, flags)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        }

        fun disableAlarm(context: Context, alarmCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else 0
            val pendingIntent = PendingIntent.getBroadcast(context, alarmCode, intent, flags)
            alarmManager.cancel(pendingIntent)
        }

        fun setAlarmCalendar(calendar: Calendar, hours:Int, minutes:Int, seconds:Int){
            calendar.set(Calendar.HOUR_OF_DAY, hours)
            calendar.set(Calendar.MINUTE, minutes)
            calendar.set(Calendar.SECOND, seconds)
        }

    }

}