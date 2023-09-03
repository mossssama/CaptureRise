package com.newOs.captureRise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.newOs.captureRise.R
import com.newOs.captureRise.room.Alarm

class RecyclerViewAdapter(
    private var alarmsList: List<Alarm>,
    private val onItemLongClickListener: (Alarm) -> Unit,
    private val onItemClickListener: (Alarm) -> Unit,
    private val onSwitchToggleListener: (Alarm, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = alarmsList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = alarmsList.size

    fun updateData(newAlarmsList: List<Alarm>) {
        alarmsList = newAlarmsList
        notifyDataSetChanged()
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val alarmTime: TextView = itemView.findViewById(R.id.alarmTime)
        private val isEnabled: SwitchMaterial = itemView.findViewById(R.id.isAlarmOn)

        fun bind(item: Alarm) {
            alarmTime.text = item.alarmTime
            isEnabled.isChecked = item.isEnabled

            itemView.setOnLongClickListener {
                onItemLongClickListener(item)
                true // Consume the long-press event
            }

            itemView.setOnClickListener { onItemClickListener(item) }

            isEnabled.setOnClickListener { onSwitchToggleListener(item,isEnabled.isChecked) }
//            isEnabled.setOnCheckedChangeListener { _, isChecked -> onSwitchToggleListener(item, isChecked) }

        }
    }
}
