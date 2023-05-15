package com.example.master

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseAuthentication
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.DateTimeFormatter
import com.example.master.models.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
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

    fun writeSteps(dateId: String, steps: Int) {
        val userId = FirebaseAuthentication.getUser()?.uid!!

        FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("steps")
            ?.child(dateId)
            ?.setValue(steps)
    }

    private val _detectedFaces = MutableLiveData<ArrayList<DetectedFace>>().apply {
        value = arrayListOf()
    }
    val detectedFaces: LiveData<ArrayList<DetectedFace>> = _detectedFaces

    @RequiresApi(Build.VERSION_CODES.O)
    fun readDetectedFacesData() {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(LocalDateTime.now())
        val userData = FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("detectedFace")
            ?.child(dateId)

        val faces = arrayListOf<DetectedFace>()
        userData?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                for (snapshot in snapshots.children) {
                    val face = snapshot.getValue(DetectedFace::class.java)
                    if (face != null) {
                        faces.add(face)
                    }
                    _detectedFaces.value = faces
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        )
    }

    private val _messages = MutableLiveData<ArrayList<SMS>>().apply {
        value = arrayListOf()
    }
    val messages: LiveData<ArrayList<SMS>> = _messages

    @RequiresApi(Build.VERSION_CODES.O)
    fun readSMS() {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(LocalDateTime.now())
        val userData = FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("sms")
            ?.child("10052023") //dateId

        val sms = arrayListOf<SMS>()
        userData?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                for (snapshot in snapshots.children) {
                    val oneSMS = snapshot.getValue(SMS::class.java)
                    if (oneSMS != null) {
                        sms.add(oneSMS)
                    }
                    _messages.value = sms
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        )
    }

    private val _calls = MutableLiveData<ArrayList<PhoneCall>>().apply {
        value = arrayListOf()
    }
    val calls: LiveData<ArrayList<PhoneCall>> = _calls

    @RequiresApi(Build.VERSION_CODES.O)
    fun readCallLog() {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(LocalDateTime.now())
        val userData = FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("callLogs")
            ?.child(dateId)

        val phoneCalls = arrayListOf<PhoneCall>()
        userData?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                for (snapshot in snapshots.children) {
                    val singlePhoneCall = snapshot.getValue(PhoneCall::class.java)
                    if (singlePhoneCall != null) {
                        phoneCalls.add(singlePhoneCall)
                    }
                    _calls.value = phoneCalls
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        )
    }

    private val _usageStat = MutableLiveData<ArrayList<UsageStatistics>>().apply {
        value = arrayListOf()
    }
    val usageStat: LiveData<ArrayList<UsageStatistics>> = _usageStat

    @RequiresApi(Build.VERSION_CODES.O)
    fun readUsageStatistics() {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(LocalDateTime.now())
        val userData = FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("usageStatistics")
            ?.child("13052023")

        val statistics = arrayListOf<UsageStatistics>()
        userData?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshots: DataSnapshot) {
                for (snapshot in snapshots.children) {
                    val stat = snapshot.getValue(UsageStatistics::class.java)
                    if (stat != null) {
                        statistics.add(stat)
                    }
                    _usageStat.value = statistics
                }
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        )
    }

    private val _steps = MutableLiveData<Int>().apply { }
    val steps: LiveData<Int> = _steps

    @RequiresApi(Build.VERSION_CODES.O)
    fun readSteps() {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(LocalDateTime.now())
        val userData = FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("steps")
            ?.child(dateId)

        userData?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val steps = snapshot.getValue(Int::class.java)
                _steps.value = steps
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        )
    }
}
