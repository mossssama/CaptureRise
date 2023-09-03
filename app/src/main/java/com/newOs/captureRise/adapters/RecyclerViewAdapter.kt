package com.newOs.captureRise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.newOs.captureRise.pojo.AlarmItem
import com.newOs.captureRise.R

class RecyclerViewAdapter(
    private val alarmsList:List<AlarmItem>,
    private val onItemLongClickListener: (AlarmItem) -> Unit,
    private val onItemClickListener: (AlarmItem) -> Unit,
    private val onSwitchToggleListener: (AlarmItem, Boolean) -> Unit
): RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = alarmsList[position]
        holder.alarmTime.text=currentItem.alarmTime
        holder.isEnabled.isChecked=currentItem.isEnabled

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener(currentItem)
            true // Consume the long-press event
        }

        holder.itemView.setOnClickListener { onItemClickListener(currentItem) }

        holder.isEnabled.setOnCheckedChangeListener { _, isChecked -> onSwitchToggleListener(currentItem, isChecked) }

    }

    override fun getItemCount(): Int = alarmsList.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val alarmTime: TextView = itemView.findViewById(R.id.alarmTime)
        val isEnabled: SwitchMaterial = itemView.findViewById(R.id.isAlarmOn)
    }

}
