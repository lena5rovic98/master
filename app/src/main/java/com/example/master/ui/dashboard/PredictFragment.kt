package com.example.master.ui.dashboard

import android.Manifest
import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.WRITE_CALL_LOG
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.R
import java.text.SimpleDateFormat
import java.util.*

class PredictFragment : Fragment() {

  private lateinit var predictViewModel: PredictViewModel
  private lateinit var textView: TextView

  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    predictViewModel =
      ViewModelProvider(this).get(PredictViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_predict, container, false)
    textView = root.findViewById(R.id.text_dashboard)

    // getByDays()

    // getStatsForToday()

    requestPermissions(
      arrayOf(
        Manifest.permission.READ_PHONE_STATE
      ), 104
    )

    requestPermissions(arrayOf(Manifest.permission.PACKAGE_USAGE_STATS), 109)
    requestPermissions(
      arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
      105
    )
    requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.BROADCAST_SMS), 106)
    requestPermissions(
      arrayOf(
        Manifest.permission.READ_CONTACTS,
        READ_CALL_LOG,
        WRITE_CALL_LOG
      ),
      101
    )

    return root
  }

  // today's current statistics isnt written in database, only shown to user
  fun getStatsForToday() {
    val mUsageStatsManager = requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager?
    if (mUsageStatsManager != null) {

      val year = Calendar.getInstance().get(Calendar.YEAR)
      val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
      val month = if (currentMonth < 10) "0${currentMonth}" else currentMonth
      var day = if (Calendar.getInstance().get(Calendar.DATE) < 10) "0${
        Calendar.getInstance().get(Calendar.DATE)
      }" else Calendar.getInstance().get(Calendar.DATE)

      val todaysDate = "$year/$month/$day 00:00:00"
      val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
      var date: Date = formatter.parse(todaysDate)
      val startDate = date.time

      val queryUsageStats = mUsageStatsManager.queryAndAggregateUsageStats(startDate, System.currentTimeMillis())

      for (stat in queryUsageStats.entries) {
        val minutes = stat.value.totalTimeInForeground / 1000 / 60
        val seconds = stat.value.totalTimeInForeground / 1000 % 60
        Log.d("log", "${stat.key} $minutes m $seconds s")
      }
    }
  }
}
