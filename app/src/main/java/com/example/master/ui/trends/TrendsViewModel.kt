package com.example.master.ui.trends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseAuthentication
import com.example.master.firebase.FirebaseReferences
import com.example.master.models.Prediction
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class TrendsViewModel : ViewModel() {

    private val _predicitons = MutableLiveData<ArrayList<Prediction>>().apply {
        value = arrayListOf()
    }
    val predictions: LiveData<ArrayList<Prediction>> = _predicitons

    fun getData() {
        val userId = FirebaseAuthentication.getUser()?.uid!!

        val predictionsArray: ArrayList<Prediction> = arrayListOf()
        FirebaseReferences.predictionsReference
            ?.child(userId)
            ?.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val pred = child.getValue(Prediction::class.java)
                        if (pred != null) {
                            predictionsArray.add(pred)
                        }
                    }
                    _predicitons.value = predictionsArray
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException()
                }
            })
    }

}
