package com.demo.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.myapplication.R
import com.demo.myapplication.databinding.ItemTimeZoneBinding
import com.demo.myapplication.models.TimeZoneModel

class TimeZoneAdapter(
    private val context: Context,
    private val selectedPositions: ArrayList<Int>,
    private val onItemClick: (ArrayList<Int>) -> Unit
) : ListAdapter<TimeZoneModel, TimeZoneAdapter.TimeZoneViewHolder>(TimeZoneDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeZoneViewHolder {
        val binding =
            ItemTimeZoneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeZoneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {

        val lapDetails = getItem(position)
        holder.binding.timeZone = lapDetails


        if (selectedPositions.contains(position)) {
            holder.binding.checkBox.setImageDrawable(
                AppCompatResources.getDrawable(
                    context, R.drawable.ic_check
                )
            )
        } else {
            holder.binding.checkBox.setImageDrawable(
                AppCompatResources.getDrawable(
                    context, R.drawable.ic_uncheck
                )
            )
        }

        holder.itemView.setOnClickListener {
            if (selectedPositions.contains(position)) {
                selectedPositions.remove(position)
            } else {
                selectedPositions.add(position)
            }
            notifyItemChanged(position)
            onItemClick.invoke(selectedPositions)
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class TimeZoneViewHolder(val binding: ItemTimeZoneBinding) :
        RecyclerView.ViewHolder(binding.root)

    class TimeZoneDiffUtil : DiffUtil.ItemCallback<TimeZoneModel>() {
        override fun areItemsTheSame(oldItem: TimeZoneModel, newItem: TimeZoneModel): Boolean {
            return oldItem.cityID == newItem.cityID
        }

        override fun areContentsTheSame(oldItem: TimeZoneModel, newItem: TimeZoneModel): Boolean {
            return oldItem == newItem
        }
    }

}