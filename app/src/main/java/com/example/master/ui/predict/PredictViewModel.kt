package com.example.master.ui.predict

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseAuthentication
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.DateTimeFormatter
import com.example.master.models.Prediction
import java.time.LocalDateTime

class PredictViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun writePrediction(prediction: Prediction) {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val dateId = DateTimeFormatter.getDateTimeId(LocalDateTime.now())

        FirebaseReferences.predictionsReference
            ?.child(userId)
            ?.child(dateId)
            ?.setValue(prediction)
    }
}