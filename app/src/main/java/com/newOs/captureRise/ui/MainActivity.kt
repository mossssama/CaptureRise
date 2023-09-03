package com.newOs.captureRise.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.newOs.captureRise.adapters.RecyclerViewAdapter
import com.newOs.captureRise.databinding.ActivityMainBinding
import com.newOs.captureRise.room.Alarm
import com.newOs.captureRise.room.AlarmDao
import com.newOs.captureRise.room.AlarmDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dao: AlarmDao
    private lateinit var adapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val newArrayList: ArrayList<Alarm> = ArrayList()

        dao = AlarmDatabase.getInstance(this).alarmDao

        adapter = RecyclerViewAdapter(
            newArrayList,
            onItemLongClickListener = { item -> deleteAlarm(item) },
            onItemClickListener = { item -> openTimePickerForUpdate(item.id,splitAlarmTime(item)[0].toInt(), splitAlarmTime(item)[1].toInt(), item.isEnabled) },
            onSwitchToggleListener = { item, isChecked ->
                Log.i("OsOs","${item.id}    -     ${item.alarmTime}    -    ${item.isEnabled}")
                updateAlarmStatus(item.id,item.alarmTime,isChecked)
            }
        )

        binding.addAlarm.setOnClickListener { openTimePickerForAdd(12,0) }

        binding.recyclerView.adapter = adapter

        observeAlarms()
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
        picker.addOnPositiveButtonClickListener { updateAlarm(picker,alarmId, isEnabled) }

        /* Cancel TimePicker */
        picker.addOnNegativeButtonClickListener { Toast.makeText(this, "Negative", Toast.LENGTH_SHORT).show() }
        /* Out TimePicker */
        picker.addOnCancelListener { Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show() }
        /* Ok + Cancel + Out TimePicker */
        picker.addOnDismissListener { Toast.makeText(this, "Dismiss", Toast.LENGTH_SHORT).show() }
    }

    private fun buildMaterialTimePicker(clockFormat: Int, hr: Int, min: Int, title: String) =
        MaterialTimePicker.Builder().setTimeFormat(clockFormat).setHour(hr).setMinute(min).setTitleText(title).build()

    private fun observeAlarms() {
        dao.getAllAlarmsLiveData().observe(this@MainActivity, Observer { alarms ->
            alarms?.let { adapter.updateData(alarms) }
        })
    }

    private fun updateAlarmStatus(id: Int,alarmTime:String, isChecked: Boolean) {
        GlobalScope.launch(Dispatchers.IO) { dao.updateAlarm(id,alarmTime, isChecked) }
    }

    private fun updateAlarm(picker: MaterialTimePicker,alarmId:Int,isEnabled: Boolean) {
        GlobalScope.launch(Dispatchers.IO) { dao.updateAlarm(alarmId,"${picker.hour}:${picker.minute}", isEnabled) }
    }

    private fun deleteAlarm(item: Alarm) {
        GlobalScope.launch(Dispatchers.IO) { dao.deleteAlarm(item.id) }
    }

    private fun insertAlarm(picker: MaterialTimePicker) {
        GlobalScope.launch(Dispatchers.IO) { dao.insertAlarm(Alarm("${picker.hour}:${picker.minute}", false)) }
    }

    private fun splitAlarmTime(item: Alarm) = item.alarmTime.split(":")
}
