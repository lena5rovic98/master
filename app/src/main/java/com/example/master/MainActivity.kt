package com.example.master

import android.Manifest
import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.master.helpers.DateTimeFormatter.getPreviousDateMilliseconds
import com.example.master.helpers.DateTimeFormatter.getTodaysDateMilliseconds
import com.example.master.models.PhoneCall
import com.example.master.models.SMS
import com.example.master.models.UsageStatistics
import com.example.master.ui.SleepRequestsManager
import com.google.android.gms.common.util.CollectionUtils.setOf
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ScheduledThreadPoolExecutor
import kotlin.Array
import kotlin.IntArray
import kotlin.arrayOf

class MainActivity : AppCompatActivity() {

    companion object {
        const val MODEL_FILE = "model.tflite"
    }

    private val sleepRequestManager by lazy{
        SleepRequestsManager(this)
    }

    private lateinit var mainActivityViewModel: MainActivityViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_camera, R.id.navigation_notifications, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        mainActivityViewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        FirebaseApp.initializeApp(this)

        // loadModelFile()

        getPhoneCalls()
        getSMS()
        getUsageStats()

        requestActivityRecognitionPermission()

//        sleepRequestManager.requestSleepUpdates(requestPermission = {
//            permissionRequester.launch(ACTIVITY_RECOGNITION)
//        })
        sleepRequestManager.subscribeToSleepUpdates()

        val options = NLClassifier.NLClassifierOptions.builder().build()
        val nlClassifier = NLClassifier.createFromFileAndOptions(
            this, MODEL_FILE, options
        )

        val executor = ScheduledThreadPoolExecutor(1)

        executor.execute {
            val results = nlClassifier.classify("beautiful and positive day today")
            Log.d("classifier negative", results[0].score.toString())
            Log.d("classifier positive", results[1].score.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("DefaultLocale")
    private fun getPhoneCalls() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.WRITE_CALL_LOG
                ),
                102
            )
        }

        // all callLogs ever
        // val managedCursor: Cursor? = contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)

        // getting only the past few days data
        // TODO: write data from last time accessed to app
        val days = Date(System.currentTimeMillis() - 1L * 24 * 3600 * 1000).time
        val cursor = contentResolver.query(CallLog.Calls.CONTENT_URI, null, "date" + ">?", arrayOf("" + days), "date DESC")

        val number = cursor?.getColumnIndex(CallLog.Calls.NUMBER)
        val type = cursor?.getColumnIndex(CallLog.Calls.TYPE)
        val date = cursor?.getColumnIndex(CallLog.Calls.DATE)
        val duration = cursor?.getColumnIndex(CallLog.Calls.DURATION)

        while (cursor?.moveToNext() == true) {
            val callType = type?.let { cursor.getString(it) }
            val callDate = date?.let { cursor.getString(it) }
            val callDayTime = Date(java.lang.Long.valueOf(callDate))
            var dir = ""
            when (callType?.toInt()) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                CallLog.Calls.REJECTED_TYPE -> dir = "REJECTED"
            }

            val phoneCall = PhoneCall(
                number?.let { cursor.getString(it) }!!,
                dir,
                callDayTime.time.toString(),
                duration?.let { cursor.getString(it) }!!
            )

            mainActivityViewModel.writeCallLog(
                callDayTime,
                phoneCall
            )
        }
        cursor?.close()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSMS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_SMS,
                   ),
                103
            )
        }

        // TODO: write data from last time accessed to app
        val uriSms = Uri.parse("content://sms/")
        val days = Date(System.currentTimeMillis() - 1L * 24 * 3600 * 1000).time
        val cursor = contentResolver.query(uriSms, null, "date" + ">?", arrayOf("" + days), "date DESC")

        // val cursor: Cursor? = contentResolver?.query(uriSms, null, null, null, null)

        cursor?.moveToFirst()
        while (cursor?.moveToNext() == true) {
            val phNumber = cursor.getString(2) // Telephony.Sms.ADDRESS
            val dateTime = cursor.getString(4) // Telephony.Sms.DATE
            val smsDateTime = Date(java.lang.Long.valueOf(dateTime))
            val type = cursor.getString(9) // Telephony.Sms.TYPE
            val body = cursor.getString(12) // Telephony.Sms.BODY
            var messageType = ""
            when (type) {
                "1" -> messageType = "received"
                "2" -> messageType = "sent"
            }

            val message = SMS(phNumber, messageType, body, smsDateTime.time.toString())
            Log.d("sms: ", message.toString())
            mainActivityViewModel.writeSMS(smsDateTime, message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUsageStats() {
        // to grant access
//    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
//    startActivity(intent)

        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // TODO: write data from last time accessed to app
        val startDate = getPreviousDateMilliseconds(8)
        val endDate = getTodaysDateMilliseconds()

        val queryUsageStats: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startDate,
            endDate
        )

        val usedPackages = queryUsageStats.filter {
            return@filter it.totalTimeInForeground != 0L
        }

        for (usedPackage in usedPackages) {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val date = formatter.format(Date(usedPackage.firstTimeStamp))

            val usageStatistics = UsageStatistics(
                usedPackage.packageName,
                usedPackage.totalTimeInForeground,
                date,
                UsageStatistics.isSocialNetwork(usedPackage.packageName)
            )

            mainActivityViewModel.writeUsageStatistics(usedPackage.firstTimeStamp, usageStatistics)
        }
    }

    private fun requestActivityRecognitionPermission() {

        if (ContextCompat.checkSelfPermission(this@MainActivity,
                android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 101)
        }
    }

    private fun loadModelFile(): MappedByteBuffer? {
        val assetFileDescriptor = this.assets.openFd("classification.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = fileInputStream.getChannel()
        val startOffset = assetFileDescriptor.startOffset
        val len = assetFileDescriptor.length
        // return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len)

        val interpreter = Interpreter(fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len), null)

        val row = arrayOf(4000, 2000, 21,22, 3982, 3, 90)
//        val input = FloatArray(1)
//        input[0] = str.toFloat()
        val output = Array(1) {
            IntArray(
                1
            )
        }

        try {
            interpreter.run(row, output)
            val res = output[0][0]
        } catch (e: Exception) {
            val a = 3
        }
        return null
    }
}