package com.example.master

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseAuthentication
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.DateTimeFormatter
import com.example.master.models.PhoneCall
import com.example.master.models.SMS
import com.example.master.models.UsageStatistics
import java.util.*

class MainActivityViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun writeCallLog(callDate: Date, phoneCall: PhoneCall) {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(callDate)
        val timeId = DateTimeFormatter.getTimeId(callDate)

        FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("callLogs")
            ?.child(dateId)
            ?.child(timeId)
            ?.setValue(phoneCall)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun writeSMS(callDate: Date, sms: SMS) {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(callDate)
        val timeId = DateTimeFormatter.getTimeId(callDate)

        FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("sms")
            ?.child(dateId)
            ?.child(timeId)
            ?.setValue(sms)
    }

    fun writeUsageStatistics(dateTime: Long, statistics: UsageStatistics) {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(dateTime)

        FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("usageStatistics")
            ?.child(dateId)
            ?.child(UsageStatistics.firebaseId(statistics.packageName))
            ?.setValue(statistics)
    }

    // fun read
}
