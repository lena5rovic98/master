package com.example.master.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseReferences
import com.example.master.models.User
import com.example.master.ui.firebase.FirebaseAuthentication
import com.google.firebase.database.*


class ProfileViewModel: ViewModel() {

    private val _user = MutableLiveData<User>().apply {
        // value = User("lala", "lala", 22, 42f, 312)
        // value = User()
    }
    val user: LiveData<User> = _user

    fun getUser() {
        val userId = FirebaseAuthentication.getUser()?.uid!!

//        FirebaseReferences.usersReference?.child(userId)?.get()?.addOnSuccessListener {
//            val a = 4
//        }?.addOnFailureListener{
//            val a = 3
//        }

//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val user: User = snapshot.getValue(User::class.java) as User
//                _user.value = user
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                throw error.toException()
//            }
//        }
//        FirebaseReferences.usersReference?.child(userId)?.addValueEventListener(listener)

//        FirebaseReferences.usersReference?.child(userId)?.get()?.addOnSuccessListener {
//            Log.i("firebase", "Got value ${it.value}")
//        }?.addOnFailureListener{
//            Log.e("firebase", "Error getting data", it)
//        }

        val userData = FirebaseReferences.usersReference?.child(userId)
//        val rootRef = FirebaseDatabase.getInstance().reference
//        var usersRef: DatabaseReference? = rootRef.child("users").child(userId)
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