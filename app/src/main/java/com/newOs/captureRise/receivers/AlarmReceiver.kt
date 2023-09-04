package com.newOs.captureRise.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.newOs.captureRise.dataStore.DataStoreManager
import com.newOs.captureRise.managers.MyAlarmManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dataStoreManager = DataStoreManager.getInstance(context)

        // Launch a coroutine to set the alarm state
        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.setIsAlarmOn(true)
            MyAlarmManager.initialize(context)
            MyAlarmManager.startAlarm()
        }

//        MyAlarmManager.initialize(context)
//        MyAlarmManager.startAlarm()
//        NotificationsUtils.launchNotification(context, title = "Alarm at ${ intent.extras?.getString(AlarmUtils.Extras.HOURS) }:${ intent.extras?.getString(AlarmUtils.Extras.MINUTES) } !")

    }
}
