package com.demo.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.databinding.ItemRealtimeTimezoneBinding
import com.demo.myapplication.models.TimeZoneModel
import com.demo.myapplication.utilities.CommonFunctions.getCurrentTimeInTimeZone

class AddNewTimeZoneAdapter :
    ListAdapter<TimeZoneModel, AddNewTimeZoneAdapter.TimeZoneViewHolder>(TimeZoneDiffCallback()) {

    inner class TimeZoneViewHolder(val binding: ItemRealtimeTimezoneBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeZoneViewHolder {
        val binding =
            ItemRealtimeTimezoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeZoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
        val alarmDetails = getItem(position)
        val timeZone = getCurrentTimeInTimeZone(alarmDetails.timezone)

        holder.binding.timezoneDetails = alarmDetails
        holder.binding.currentTime.text = timeZone
        holder.binding.zoneStatus.text = alarmDetails.timezone
    }

    class TimeZoneDiffCallback : DiffUtil.ItemCallback<TimeZoneModel>() {
        override fun areItemsTheSame(oldItem: TimeZoneModel, newItem: TimeZoneModel): Boolean {
            return oldItem.cityID == newItem.cityID
        }

        override fun areContentsTheSame(oldItem: TimeZoneModel, newItem: TimeZoneModel): Boolean {
            return oldItem == newItem
        }
    }
}