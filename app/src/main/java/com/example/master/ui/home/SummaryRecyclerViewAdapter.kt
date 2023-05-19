package com.example.master.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.master.databinding.ItemSummaryBinding
import com.example.master.enum.ActivityEnum
import com.example.master.models.ActivityObject

class SummaryRecyclerViewAdapter : ListAdapter<ActivityObject, SummaryRecyclerViewAdapter.SummaryHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ActivityObject>() {
            override fun areItemsTheSame(oldItem: ActivityObject, newItem: ActivityObject): Boolean {
                return oldItem.type == newItem.type
            }

            override fun areContentsTheSame(oldItem: ActivityObject, newItem: ActivityObject): Boolean {
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

        private lateinit var mSummary: ActivityObject

        fun bind(summary: ActivityObject) {
            mSummary = summary

            binding.imageSummary.setImageResource(mSummary.icon)
            binding.labelSummaryType.text = mSummary.type
            when (mSummary.type) {
                ActivityEnum.SOCIAL_NETWORKS, ActivityEnum.DISPLAY_TIME -> {
                    val socialMinutes = (mSummary.value / (1000*60)) % 60
                    val socialHours = mSummary.value / (1000 * 60 * 60) % 24
                    val totalMinutes = (mSummary.referenceValue / (1000*60)) % 60
                    val totalHours = mSummary.referenceValue / (1000 * 60 * 60) % 24

                    binding.labelSummaryValue.text = "${socialHours}h ${socialMinutes}min / ${totalHours}h ${totalMinutes}min"

                    binding.progressBar.progress = (socialHours * 60) + socialMinutes
                    binding.progressBar.max = (totalHours * 60) + totalMinutes
                }
                else -> {
                    binding.labelSummaryValue.text = "${mSummary.value}/${mSummary.referenceValue} ${mSummary.measurementUnit}"
                    binding.progressBar.progress = mSummary.value
                    binding.progressBar.max = mSummary.referenceValue
                }
            }
        }
    }
}
