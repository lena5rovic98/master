package com.example.master.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.master.databinding.ItemSummaryBinding
import com.example.master.models.DetectedFace

class SummaryRecyclerViewAdapter : ListAdapter<DetectedFace, SummaryRecyclerViewAdapter.SummaryHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DetectedFace>() {
            override fun areItemsTheSame(oldItem: DetectedFace, newItem: DetectedFace): Boolean {
                return oldItem.dateTime == newItem.dateTime
            }

            override fun areContentsTheSame(oldItem: DetectedFace, newItem: DetectedFace): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryHolder {
        return SummaryHolder(ItemSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SummaryHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SummaryHolder(val binding: ItemSummaryBinding): RecyclerView.ViewHolder(binding.root) {

        private lateinit var mSummary: DetectedFace

        fun bind(summary: DetectedFace) {
            mSummary = summary
            binding.labelSummaryType.text = "Type"
            binding.labelSummaryValue.text = "0km"
        }
    }
}