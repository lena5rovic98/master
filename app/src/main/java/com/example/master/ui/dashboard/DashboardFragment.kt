package com.example.master.ui.dashboard

import android.Manifest.permission.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.R
import java.util.*
import android.provider.CallLog

import android.content.pm.PackageManager

import android.os.Build
import androidx.core.content.ContextCompat.checkSelfPermission
import android.Manifest
import android.net.Uri
import androidx.annotation.RequiresApi

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
//    dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
//      textView.text = it
//    })
    getUserStats()
    requestContactPermission()
    getSMS()
    // showContacts()

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

  private fun requestContactPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(requireContext(), Manifest.permission.READ_CALL_LOG) !== PackageManager.PERMISSION_GRANTED) {
      requestPermissions(
        arrayOf(Manifest.permission.READ_CONTACTS,
          READ_CALL_LOG,
          WRITE_CALL_LOG),
        101
      )
    } else {
      getCallLogs()
    }
  }

  fun getCallLogs() {
//    val projection = arrayOf(
//      CallLog.Calls.CACHED_NAME,
//      CallLog.Calls.NUMBER,
//      CallLog.Calls.TYPE,
//      CallLog.Calls.DATE
//    )
//
//    val cursor: Cursor? = context?.contentResolver?.query(CallLog.Calls.CONTENT_URI, projection, null, null, null)
//    while (cursor?.moveToNext() == true) {
//      val name: String = cursor.getString(0)
//      val number: String = cursor.getString(1)
//      val type: String = cursor.getString(2) // https://developer.android.com/reference/android/provider/CallLog.Calls.html#TYPE
//      val time: String = cursor.getString(3) // epoch time - https://developer.android.com/reference/java/text/DateFormat.html#parse(java.lang.String
//    }
//    cursor?.close()

    val sb = StringBuffer()

    val managedCursor: Cursor? = context?.contentResolver?.query(CallLog.Calls.CONTENT_URI, null, null, null, null)

    val number = managedCursor?.getColumnIndex(CallLog.Calls.NUMBER)
    // val name = managedCursor?.getColumnIndex(CallLog.Calls.CACHED_NAME)
    val type = managedCursor?.getColumnIndex(CallLog.Calls.TYPE)
    val date = managedCursor?.getColumnIndex(CallLog.Calls.DATE)
    val duration = managedCursor?.getColumnIndex(CallLog.Calls.DURATION)
    sb.append("Call Details :")
    while (managedCursor?.moveToNext() == true) {
      val phNumber = number?.let { managedCursor.getString(it) }
      val callType = type?.let { managedCursor.getString(it) }
      val callDate = date?.let { managedCursor.getString(it) }
      val callDayTime = Date(java.lang.Long.valueOf(callDate))
      val callDuration = duration?.let { managedCursor.getString(it) }
      var dir: String? = null
      val dircode = callType?.toInt()
      when (dircode) {
        CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
        CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
        CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
        CallLog.Calls.REJECTED_TYPE -> dir = "REJECTED"
      }
      sb.append("\nPhone Number:--- $phNumber \nCall Type:--- $dir \nCall Date:--- $callDayTime \nCall duration in sec :--- $callDuration")
      sb.append("\n----------------------------------")
    }
    managedCursor?.close()
    textView.text = sb
    // call.setText(sb)
  }

  private fun showContacts() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
        requireContext(),
        Manifest.permission.READ_CALL_LOG
      ) !== PackageManager.PERMISSION_GRANTED
    ) {
      requestPermissions(
        arrayOf(
          Manifest.permission.READ_CONTACTS,
          READ_CALL_LOG,
          WRITE_CALL_LOG
        ),
        101
      )
    } else {
      getCallLogs()
    }

  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 101) {
//      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//        // Permission is granted
//        getCallLogs()
//      } else {
//        Toast.makeText(
//          context,
//          "Until you grant the permission, we canot display the names",
//          Toast.LENGTH_SHORT
//        ).show()
//      }
    }
  }

  fun getSMS() {
    requestPermissions(
      arrayOf(Manifest.permission.READ_SMS),
      102
    )


    val SMS_INBOX: Uri = Uri.parse("content://sms/conversations/")

    val uriSms = Uri.parse("content://sms/")
    val cursor: Cursor? = activity?.contentResolver?.query(uriSms, null, null, null, null)

    cursor?.moveToFirst()
    var messages = ""
    while (cursor?.moveToNext() == true) {
      val phNumber = cursor.getString(2)
      val type = cursor.getString(9)
      val body = cursor.getString(12)
      when (type) {
        "1" -> messages += "Contact: $phNumber, text: $body, type: received\n"
        "2" -> messages += "Contact: $phNumber, text: $body, type: received\n"
      }
    }
    textView.text = messages
  }

  fun getLocation() {

  }
}
