package com.demo.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.databinding.ItemStopwatchBinding
import com.demo.myapplication.models.LapDetails

class LapDetailsAdapter : ListAdapter<LapDetails, LapDetailsAdapter.LapDetailsViewHolder>(
    LapDetailsDiffCallback()
) {

    inner class LapDetailsViewHolder(val binding: ItemStopwatchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapDetailsViewHolder {
        val binding = ItemStopwatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LapDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LapDetailsViewHolder, position: Int) {
        val lapDetails = getItem(position)
        holder.binding.lapDetails = lapDetails
    }

    class LapDetailsDiffCallback : DiffUtil.ItemCallback<LapDetails>() {
        override fun areItemsTheSame(oldItem: LapDetails, newItem: LapDetails): Boolean {
            return (oldItem.lap == newItem.lap)
        }

        override fun areContentsTheSame(oldItem: LapDetails, newItem: LapDetails): Boolean {
            return (oldItem.lap == newItem.lap && oldItem.overallTime == newItem.overallTime && oldItem.lapTime == newItem.lapTime)
        }
    }
}
