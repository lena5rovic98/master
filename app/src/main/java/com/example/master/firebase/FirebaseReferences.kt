package com.example.master.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseReferences {

    var firebaseDatabase: FirebaseDatabase? = null
    var usersReference: DatabaseReference? = null

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        usersReference = firebaseDatabase!!.getReference("users")
    }
}