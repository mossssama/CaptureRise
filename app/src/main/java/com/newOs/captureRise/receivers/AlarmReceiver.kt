package com.newOs.captureRise.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.newOs.captureRise.managers.MyAlarmManager
import com.newOs.captureRise.utils.AlarmUtils
import com.newOs.captureRise.utils.NotificationsUtils

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        MyAlarmManager.initialize(context)
        MyAlarmManager.startAlarm()

        /* To be deleted with its class */
        NotificationsUtils.launchNotification(context, title = "Alarm at ${ intent.extras?.getString(AlarmUtils.Extras.HOURS) } !")

    }
}
