package com.newOs.captureRise.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.newOs.captureRise.dataStore.DataStoreManager
import com.newOs.captureRise.managers.MyAlarmManager
import com.newOs.captureRise.utils.AlarmUtils
import com.newOs.captureRise.utils.NotificationsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val dataStoreManager = DataStoreManager.getInstance(context)

        CoroutineScope(Dispatchers.IO).launch {
            dataStoreManager.setIsAlarmOn(true)
            MyAlarmManager.initialize(context)
            MyAlarmManager.startAlarm()
        }

        val hours = addPreZeroIfNeeded(intent.extras?.getString(AlarmUtils.Extras.HOURS).toString())
        val minutes = addPreZeroIfNeeded(intent.extras?.getString(AlarmUtils.Extras.MINUTES).toString())
        NotificationsUtils.launchNotification(context, title = "Alarm at $hours:$minutes !")

    }

    private fun addPreZeroIfNeeded(input: String): String = if (input.length == 1 && input.toInt() >= 0 && input.toInt() <= 9) "0$input" else input

}
