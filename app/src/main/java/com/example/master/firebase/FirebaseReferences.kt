package com.example.master.firebase

import com.example.master.models.Prediction
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseReferences {

    var firebaseDatabase: FirebaseDatabase? = null
    var usersReference: DatabaseReference? = null
    var activityReference: DatabaseReference? = null
    var predictionsReference: DatabaseReference? = null

    var inputData: Prediction = Prediction()

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        usersReference = firebaseDatabase!!.getReference("users")
        activityReference = firebaseDatabase!!.getReference("activities")
        predictionsReference = firebaseDatabase!!.getReference("predictions")
    }
}