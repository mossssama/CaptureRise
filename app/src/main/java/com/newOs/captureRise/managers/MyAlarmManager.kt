package com.newOs.captureRise.managers

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import com.newOs.captureRise.dataStore.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

object MyAlarmManager {
    private var contextRef: WeakReference<Context>? = null
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var dataStoreManager: DataStoreManager? =null

    fun initialize(context: Context) {
        contextRef = WeakReference(context.applicationContext)
        dataStoreManager = DataStoreManager.getInstance(context)
    }

    private fun getContext(): Context? = contextRef?.get()

    fun startAlarm() {
        val context = getContext()
        if (context != null) {
            ringtone = playPhoneDefaultRingTone(context)
            vibrator = startVibration(context)
            wakeLock = acquireWakeLock(context)
        }
    }

    fun stopAlarm() {
        stopRingTone()
        stopVibration()
        cancelWakeLock()
    }

    private fun playPhoneDefaultRingTone(context: Context): Ringtone {
        val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val ringtone: Ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
        ringtone.play()
        return ringtone
    }

    private fun startVibration(context: Context): Vibrator {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        return vibrator
    }

    private fun acquireWakeLock(context: Context): PowerManager.WakeLock {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmReceiver:WakeLock")
        wakeLock.acquire()
        return wakeLock
    }

    private fun stopRingTone() {
        ringtone?.stop()
    }

    private fun stopVibration() {
        vibrator?.cancel()
    }
    
    private fun cancelWakeLock() {
        try {
            if (wakeLock?.isHeld == true) wakeLock?.release()
        } catch (e: Exception) {
            CoroutineScope(Dispatchers.IO).launch { dataStoreManager?.setIsAlarmOn(false) }
        }
    }
}
