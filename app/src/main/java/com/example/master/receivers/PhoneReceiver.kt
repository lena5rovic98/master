package com.example.master.receivers

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.example.master.firebase.FirebaseAuthentication
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.DateTimeFormatter
import com.example.master.models.NetworkLocation
import com.example.master.models.ReceivedCall
import java.time.LocalDateTime

class PhoneReceiver: BroadcastReceiver(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    private lateinit var context: Context
    private var receivedState: String? = ""

    override fun onReceive(context: Context, intent: Intent?) {
        this.context = context

        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        this.receivedState = state
        // val incomingOutcomingNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        when (state) {
            TelephonyManager.EXTRA_STATE_RINGING -> { }
            TelephonyManager.EXTRA_STATE_OFFHOOK -> { }
            TelephonyManager.EXTRA_STATE_IDLE -> { }
        }

        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        // for Latitude & Longitude
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1f, this);
    }

    override fun onLocationChanged(location: Location) {
        val result = "Latitude: " + location.latitude + " , Longitude: " + location.longitude

        val receivedCall = ReceivedCall(
            receivedState,
            NetworkLocation(location.latitude, location.longitude, location.altitude),
            LocalDateTime.now().toString()
        )

        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(LocalDateTime.now())
        val timeId = DateTimeFormatter.getTimeId(LocalDateTime.now())
        FirebaseReferences.activityReference
            ?.child(userId)
            ?.child(dateId)
            ?.child("receivedCallStates")
            ?.child(timeId)
            ?.setValue(receivedCall)
    }
}
