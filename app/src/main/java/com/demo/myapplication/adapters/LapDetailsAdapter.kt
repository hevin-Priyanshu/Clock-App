package com.demo.myapplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.databinding.ItemStopwatchBinding
import com.demo.myapplication.models.LapDetailsModel

class LapDetailsAdapter : ListAdapter<LapDetailsModel, LapDetailsAdapter.LapDetailsViewHolder>(LapDetailsDiffCallback()) {

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

    class LapDetailsDiffCallback : DiffUtil.ItemCallback<LapDetailsModel>() {
        override fun areItemsTheSame(oldItem: LapDetailsModel, newItem: LapDetailsModel): Boolean {
            return (oldItem.lap == newItem.lap)
        }

        override fun areContentsTheSame(oldItem: LapDetailsModel, newItem: LapDetailsModel): Boolean {
            return (oldItem.lap == newItem.lap && oldItem.overallTime == newItem.overallTime && oldItem.lapTime == newItem.lapTime)
        }
    }
}
