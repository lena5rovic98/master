package com.example.master.ui.predict

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseAuthentication
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.DateTimeFormatter
import com.example.master.models.Prediction
import com.example.master.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    private val _user = MutableLiveData<User>().apply {
        value = User()
    }
    val user: LiveData<User> = _user

    fun getUserData() {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        val userData = FirebaseReferences.usersReference?.child(userId)

        userData?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User = snapshot.getValue(User::class.java) as User
                _user.value = user
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        )
    }
}