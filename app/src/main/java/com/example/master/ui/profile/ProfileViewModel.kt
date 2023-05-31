package com.example.master.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.master.firebase.FirebaseReferences
import com.example.master.models.User
import com.example.master.firebase.FirebaseAuthentication
import com.google.firebase.database.*

class ProfileViewModel: ViewModel() {

    private val _user = MutableLiveData<User>().apply {
        value = User()
    }
    val user: LiveData<User> = _user

    fun getUser() {
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

    fun updateUserData(age: Int, weight: Int, height: Int, gender: Int, smoke: Int, shareData: Boolean) {
        val userId = FirebaseAuthentication.getUser()?.uid!!
        FirebaseReferences.usersReference?.child(userId)?.child("age")?.setValue(age)
        FirebaseReferences.usersReference?.child(userId)?.child("weight")?.setValue(weight)
        FirebaseReferences.usersReference?.child(userId)?.child("height")?.setValue(height)
        FirebaseReferences.usersReference?.child(userId)?.child("gender")?.setValue(gender)
        FirebaseReferences.usersReference?.child(userId)?.child("smoke")?.setValue(smoke)
        FirebaseReferences.usersReference?.child(userId)?.child("shareData")?.setValue(shareData)
    }
}
