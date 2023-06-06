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
import com.example.master.enum.PredictionEnum
import com.example.master.models.Prediction
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class TrendsFragment : Fragment() {

  private var _binding: FragmentTrendsBinding? = null
  private val binding get() = _binding!!

  private lateinit var trendsViewModel: TrendsViewModel
  private var predictions: ArrayList<Prediction> = arrayListOf()

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentTrendsBinding.inflate(inflater, container, false)
    trendsViewModel = ViewModelProvider(this)[TrendsViewModel::class.java]

    observeData()
    trendsViewModel.getData()

    return binding.root
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun observeData() {
    trendsViewModel.predictions.observe(viewLifecycleOwner) {
      if (it.isNotEmpty()) {
        predictions = it
        initBarChart()
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun initBarChart() {
    val barEntriesArrayList: ArrayList<BarEntry> = arrayListOf()

    barEntriesArrayList.add(BarEntry(
      0F,
      predictions.filter { it.prediction == PredictionEnum.DEPRESSION.name }.count().toFloat()
    ))
    barEntriesArrayList.add(BarEntry(
      1F,
      predictions.filter { it.prediction == PredictionEnum.ANXIETY.name }.count().toFloat()
    ))
    barEntriesArrayList.add(BarEntry(
      2F,
      predictions.filter { it.prediction == PredictionEnum.STRESS.name }.count().toFloat()
    ))
    barEntriesArrayList.add(BarEntry(
      3F,
      predictions.filter { it.prediction == PredictionEnum.NONE.name }.count().toFloat()
    ))

    val barDataSet = BarDataSet(barEntriesArrayList, "")
    val barData = BarData(barDataSet)
    binding.barChart.data = barData
    context?.getColor(R.color.purple_200)?.let { barDataSet.setColors(it) }
    barDataSet.valueTextColor = Color.BLACK
    barDataSet.valueTextSize = 16f
    barDataSet.setDrawValues(false)
    binding.barChart.description.isEnabled = false

    val labels = arrayOf(
      PredictionEnum.DEPRESSION.name,
      PredictionEnum.ANXIETY.name,
      PredictionEnum.STRESS.name,
      PredictionEnum.NONE.name,
      )

    binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
    binding.barChart.xAxis.granularity = 1f
    binding.barChart.xAxis.setLabelCount(labels.size, false)
    binding.barChart.xAxis.setCenterAxisLabels(false)
    binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

    binding.barChart.invalidate()
  }
}
