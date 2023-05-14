package com.example.master.ui.camera

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseAuthentication
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.DateTimeFormatter
import com.example.master.models.DetectedFace
import java.time.LocalDateTime

class CameraViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun writeFaceRecognition(detectedFace: DetectedFace) {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateId(LocalDateTime.now())
        val timeId = DateTimeFormatter.getTimeId(LocalDateTime.now())
        FirebaseReferences.activityReference
            ?.child(userId)
            ?.child("detectedFace")
            ?.child(dateId)
            ?.child(timeId)
            ?.setValue(detectedFace)
    }
}