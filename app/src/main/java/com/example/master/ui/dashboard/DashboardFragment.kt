package com.example.master.ui.dashboard

import android.Manifest.permission.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.R
import java.util.*
import android.Manifest

class DashboardFragment : Fragment() {

  private lateinit var dashboardViewModel: DashboardViewModel
  private lateinit var textView: TextView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
    textView = root.findViewById(R.id.text_dashboard)

    getUserStats()

    requestPermissions(
      arrayOf(
        Manifest.permission.READ_PHONE_STATE
      ), 104)

    requestPermissions(arrayOf(Manifest.permission.PACKAGE_USAGE_STATS), 109)
    requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 105)
    requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.BROADCAST_SMS), 106)
    requestPermissions(
      arrayOf(Manifest.permission.READ_CONTACTS,
        READ_CALL_LOG,
        WRITE_CALL_LOG),
      101
    )

    return root
  }

  private fun getUserStats() {
    // to grant access
//    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//    startActivity(intent)

    val usageStatsManager = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val beginCal: Calendar = Calendar.getInstance()
    beginCal.set(Calendar.DATE, 1)
    beginCal.set(Calendar.MONTH, 9)
    beginCal.set(Calendar.YEAR, 2022)

    val endCal: Calendar = Calendar.getInstance()
    endCal.set(Calendar.DATE, 1)
    endCal.set(Calendar.MONTH, 10)
    endCal.set(Calendar.YEAR, 2022)

    val queryUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(
      UsageStatsManager.INTERVAL_DAILY,
      System.currentTimeMillis() - 1000 * 3600 * 24,
      System.currentTimeMillis()
    )

    // TODO: duplicated?
//    val mySortedMap: SortedMap<Long, UsageStats> = TreeMap()
//    if (queryUsageStats.isNotEmpty()) {
//
//      for (usageStats in queryUsageStats) {
//        mySortedMap[usageStats.totalTimeInForeground] = usageStats
//      }
//    }

    val sortedStatistics = queryUsageStats.sortedBy {
      it.totalTimeInForeground
    }
    val sorted = sortedStatistics.map {
      it.packageName
    }

    val socialStatistics = queryUsageStats.filter {
      it.packageName.contains("youtube") ||
      it.packageName.contains("skype") ||
      it.packageName.contains("instagram") ||
      it.packageName.contains("facebook") ||
      it.packageName.contains("tiktok")
    }
  }
}
