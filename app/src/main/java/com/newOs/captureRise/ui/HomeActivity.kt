package com.newOs.captureRise.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.newOs.captureRise.adapters.RecyclerViewAdapter
import com.newOs.captureRise.room.Alarm
import com.newOs.captureRise.room.AlarmDao
import com.newOs.captureRise.room.AlarmDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.newOs.captureRise.dataStore.DataStoreManager
import com.newOs.captureRise.R
import com.newOs.captureRise.databinding.ActivityHomeBinding
import com.newOs.captureRise.managers.MyAlarmManager
import com.newOs.captureRise.utils.AlarmUtils
import com.newOs.captureRise.utils.ParseUtils.Companion.convertAlarmToFullTimeFormat
import com.newOs.captureRise.utils.ParseUtils.Companion.convertAlarmToSimplifiedFormat
import com.newOs.captureRise.utils.ParseUtils.Companion.convertTimeStringToInt
import com.newOs.captureRise.utils.ParseUtils.Companion.splitAlarmTime
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var dao: AlarmDao
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        val dataStoreManager = DataStoreManager.getInstance(this)
        MyAlarmManager.initialize(this)
        dao = AlarmDatabase.getInstance(this).alarmDao

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.addAlarm.setOnClickListener { openTimePickerForAdd(12,0) }
        binding.closeAlarm.setOnClickListener { startActivity(Intent(this, CameraActivity::class.java)) }

        determineCloseButtonState(dataStoreManager, binding)

        adapter = getRecyclerViewAdapter(ArrayList())
        binding.recyclerView.adapter = adapter

        observeAlarms()
    }

    private fun getRecyclerViewAdapter(newArrayList: ArrayList<Alarm>) =
        RecyclerViewAdapter(
            newArrayList,
            onItemLongClickListener = { item ->
                deleteAlarm(item)
                if (item.isEnabled) disableAlarm(item)
            },
            onItemClickListener = { item ->
                openTimePickerForUpdate(
                    item.id,
                    splitAlarmTime(item)[0].toInt(),
                    splitAlarmTime(item)[1].toInt(),
                    item.isEnabled
                )
            },
            onSwitchToggleListener = { item, isChecked ->
                Log.i("OsOs", "${item.id}    -     ${item.alarmTime}    -    ${item.isEnabled}")
                updateAlarmStatus(item.id, convertAlarmToSimplifiedFormat(item.alarmTime), isChecked)
                if (isChecked) enableAlarm(item)
                else disableAlarm(item)
            }
        )

    private fun determineCloseButtonState(
        dataStoreManager: DataStoreManager,
        binding: ActivityHomeBinding
    ) {
        lifecycleScope.launch {
            dataStoreManager.isAlarmOn.collect { isAlarmOn ->
                if (isAlarmOn) binding.closeAlarm.show() else binding.closeAlarm.hide()
            }
        }
    }



    private fun openTimePickerForAdd(hr: Int, min: Int) {
        val isSystem24Hour = is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker = buildMaterialTimePicker(clockFormat, hr, min, "Set Alarm")

        picker.show(supportFragmentManager, "TAG")

        /* Ok TimePicker */
        picker.addOnPositiveButtonClickListener { insertAlarm(picker) }

        /* Cancel TimePicker */
        picker.addOnNegativeButtonClickListener { Toast.makeText(this, "Negative", Toast.LENGTH_SHORT).show() }
        /* Out TimePicker */
        picker.addOnCancelListener { Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show() }
        /* Ok + Cancel + Out TimePicker */
        picker.addOnDismissListener { Toast.makeText(this, "Dismiss", Toast.LENGTH_SHORT).show() }
    }


    private fun openTimePickerForUpdate(alarmId: Int,hr: Int, min: Int, isEnabled: Boolean) {
        val isSystem24Hour = is24HourFormat(this)
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker = buildMaterialTimePicker(clockFormat, hr, min, "Update Alarm")

        picker.show(supportFragmentManager, "TAG")

        /* Ok TimePicker */
        picker.addOnPositiveButtonClickListener {
            updateAlarm(picker,alarmId, isEnabled)
            if(isEnabled){
                disableAlarm(Alarm("$hr:$min",isEnabled))
                enableAlarm(Alarm("${picker.hour}:${picker.minute}",isEnabled))
            }
        }

        /* Cancel TimePicker */
        picker.addOnNegativeButtonClickListener { Toast.makeText(this, "Negative", Toast.LENGTH_SHORT).show() }
        /* Out TimePicker */
        picker.addOnCancelListener { Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show() }
        /* Ok + Cancel + Out TimePicker */
        picker.addOnDismissListener { Toast.makeText(this, "Dismiss", Toast.LENGTH_SHORT).show() }
    }



    private fun enableAlarm(item: Alarm) {
        val calendar = Calendar.getInstance()
        val alarmTime = convertAlarmToSimplifiedFormat(item.alarmTime)
        AlarmUtils.setAlarmCalendar(
            calendar,
            splitAlarmTime(alarmTime)[0].toInt(),
            splitAlarmTime(alarmTime)[1].toInt(),
            0
        )
        AlarmUtils.enableAlarm(
            this,
            calendar,
            splitAlarmTime(alarmTime)[0],
            splitAlarmTime(alarmTime)[1],
            convertTimeStringToInt(convertAlarmToFullTimeFormat(item.alarmTime))
        )
    }

    private fun updateAlarmStatus(id: Int,alarmTime:String, isChecked: Boolean) {
        GlobalScope.launch(Dispatchers.IO) { dao.updateAlarm(id, convertAlarmToFullTimeFormat(alarmTime), isChecked) }
    }

    private fun updateAlarm(picker: MaterialTimePicker,alarmId:Int,isEnabled: Boolean) {
        val alarmTime = convertAlarmToFullTimeFormat("${picker.hour}:${picker.minute}")
        GlobalScope.launch(Dispatchers.IO) { dao.updateAlarm(alarmId,alarmTime, isEnabled) }
    }

    private fun insertAlarm(picker: MaterialTimePicker) {
        val alarmTime = convertAlarmToFullTimeFormat("${picker.hour}:${picker.minute}")
        GlobalScope.launch(Dispatchers.IO) { dao.insertAlarm(Alarm(alarmTime, false)) }
    }


    private fun disableAlarm(item: Alarm) {
        AlarmUtils.disableAlarm(this, convertTimeStringToInt(convertAlarmToFullTimeFormat(item.alarmTime)))
    }

    private fun buildMaterialTimePicker(clockFormat: Int, hr: Int, min: Int, title: String) =
        MaterialTimePicker.Builder().setTimeFormat(clockFormat).setHour(hr).setMinute(min).setTheme(R.style.TimePicker).setTitleText(title).build()

    private fun deleteAlarm(item: Alarm) {
        GlobalScope.launch(Dispatchers.IO) { dao.deleteAlarm(item.id) }
    }

    private fun observeAlarms() {
        dao.getAllAlarmsLiveData().observe(this@HomeActivity, Observer { alarms -> alarms?.let { adapter.updateData(alarms) } })
    }

}
