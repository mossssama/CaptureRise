package com.newOs.captureRise.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.newOs.captureRise.managers.MyAlarmManager
import com.newOs.captureRise.R
import com.newOs.captureRise.adapters.RecyclerViewAdapter
import com.newOs.captureRise.databinding.ActivityMainBinding
import com.newOs.captureRise.room.Alarm
import com.newOs.captureRise.room.AlarmDao
import com.newOs.captureRise.room.AlarmDatabase
import com.newOs.captureRise.pojo.AlarmItem
import com.newOs.captureRise.utils.AlarmUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var dao: AlarmDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val newArrayList:ArrayList<AlarmItem> =ArrayList()

        dao = AlarmDatabase.getInstance(this).alarmDao

        val adapter = RecyclerViewAdapter(newArrayList,
            onItemLongClickListener = { item -> deleteAlarm(item) },
            onItemClickListener = { item -> openTimePicker(splitAlarmTime(item)[0].toInt(),splitAlarmTime(item)[1].toInt(),item.isEnabled,"Edit Alarm") },
            onSwitchToggleListener = { item, isChecked -> updateAlarmEnable(item, isChecked) }
        )
        adapter.notifyDataSetChanged()

        binding.addAlarm.setOnClickListener{ openTimePicker(12,0,false,"Set Alarm") }

        observeAlarms(newArrayList, adapter)

        binding.recyclerView.adapter = adapter

        binding.closeAlarm.setOnClickListener {
            MyAlarmManager.initialize(this)
            MyAlarmManager.stopAlarm()
        }
    }


    private fun openTimePicker(hr: Int,min: Int,isEnabled:Boolean,title: String) {
        val isSystem24Hour = is24HourFormat(this)
        val clockFormat = if(isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val picker = buildMaterialTimePicker(clockFormat,hr,min,title)

        picker.show(supportFragmentManager,"TAG")

        /* Ok TimePicker */
        picker.addOnPositiveButtonClickListener {
            if(title=="Set Alarm") insertAlarm(picker)
            else updateAlarm(picker,"$hr:$min",isEnabled)
        }

        /* Cancel TimePicker */
        picker.addOnNegativeButtonClickListener{ Toast.makeText(this,"Negative",Toast.LENGTH_SHORT).show() }
        /* Out TimePicker*/
        picker.addOnCancelListener { Toast.makeText(this,"Cancel",Toast.LENGTH_SHORT).show() }
        /* Ok + Cancel + Out TimePicker */
        picker.addOnDismissListener{ Toast.makeText(this,"Dismiss",Toast.LENGTH_SHORT).show() }

    }

    private fun buildMaterialTimePicker(clockFormat: Int,hr:Int,min:Int,title:String) = MaterialTimePicker.Builder().setTimeFormat(clockFormat).setHour(hr).setMinute(min).setTitleText(title).build()

    private fun observeAlarms(
        newArrayList: ArrayList<AlarmItem>,
        adapter: RecyclerViewAdapter
    ) {
        dao.getAllAlarmsLiveData().observe(this@MainActivity, androidx.lifecycle.Observer<List<Alarm>> { alarms ->
            updateRecyclerViewArrayList(newArrayList, alarms,adapter)
            adapter.notifyDataSetChanged()
        })
    }

    private fun updateRecyclerViewArrayList(
        newArrayList: ArrayList<AlarmItem>,
        alarms: List<Alarm>,
        adapter: RecyclerViewAdapter
    ) {
        newArrayList.clear()
        newArrayList.addAll(convertToAlarmTimes(alarms))
        adapter.notifyDataSetChanged()
    }


    private fun updateAlarmEnable(item: AlarmItem, isChecked: Boolean) {
        GlobalScope.launch(Dispatchers.IO) {
            if(isChecked){
                val calendar = Calendar.getInstance()
                AlarmUtils.setAlarmCalendar(calendar,splitAlarmTime(item)[0].toInt(),splitAlarmTime(item)[1].toInt(),0)
                AlarmUtils.enableAlarm(this@MainActivity,calendar,splitAlarmTime(item)[0],convertTimeStringToInt(item.alarmTime))
            }
            else AlarmUtils.disableAlarm(this@MainActivity,convertTimeStringToInt(item.alarmTime))

            dao.updateAlarmEnable(item.alarmTime, isChecked)
        }
    }

    private fun deleteAlarm(item: AlarmItem) {
        GlobalScope.launch(Dispatchers.IO) { dao.deleteAlarm(item.alarmTime) }
    }

    private fun insertAlarm(picker: MaterialTimePicker) {
        GlobalScope.launch(Dispatchers.IO) { dao.insertAlarm(Alarm("${picker.hour}:${picker.minute}", false)) }
    }

    private fun updateAlarm(picker: MaterialTimePicker,alarmTime: String,isEnabled: Boolean) {
        GlobalScope.launch(Dispatchers.IO) { dao.deleteAlarm(alarmTime);    dao.insertAlarm(Alarm("${picker.hour}:${picker.minute}", isEnabled)) }
    }

    private fun convertToAlarmTimes(alarms: List<Alarm>): List<AlarmItem> = alarms.map { alarm -> AlarmItem(alarm.alarmTime, alarm.isEnabled) }

    private fun splitAlarmTime(item: AlarmItem) = item.alarmTime.split(":")

    private fun convertTimeStringToInt(timeStr: String): Int = timeStr.replace(":", "").toInt()


}