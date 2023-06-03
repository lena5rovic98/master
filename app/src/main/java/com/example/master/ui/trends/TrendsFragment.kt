package com.example.master.ui.trends

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.R
import com.example.master.databinding.FragmentTrendsBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class TrendsFragment : Fragment() {

  private var _binding: FragmentTrendsBinding? = null
  private val binding get() = _binding!!

  private lateinit var trendsViewModel: TrendsViewModel

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentTrendsBinding.inflate(inflater, container, false)
    trendsViewModel = ViewModelProvider(this).get(TrendsViewModel::class.java)

    initBarChart()

    return binding.root
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun initBarChart() {
    val barEntriesArrayList: ArrayList<BarEntry> = arrayListOf()

    barEntriesArrayList.add(BarEntry(1f, 4f))
    barEntriesArrayList.add(BarEntry(2f, 6f))
    barEntriesArrayList.add(BarEntry(3f, 8f))
    barEntriesArrayList.add(BarEntry(4f, 2f))
    barEntriesArrayList.add(BarEntry(5f, 4f))
    barEntriesArrayList.add(BarEntry(6f, 1f))

    val barDataSet = BarDataSet(barEntriesArrayList, "Geeks for Geeks")
    val barData = BarData(barDataSet)
    binding.barChart.data = barData
    context?.getColor(R.color.purple_200)?.let { barDataSet.setColors(it) }
    barDataSet.valueTextColor = Color.BLACK
    barDataSet.valueTextSize = 16f
    binding.barChart.description.isEnabled = false
  }
}
