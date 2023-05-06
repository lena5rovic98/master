package com.example.master.firebase

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.example.master.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

@SuppressLint("StaticFieldLeak")
object FirebaseAuthentication {

    var firebaseAuth: FirebaseAuth? = null
    private var context: Context? = null

    fun getInstance(context: Context) {
        firebaseAuth = FirebaseAuth.getInstance()
        FirebaseAuthentication.context = context
    }

    fun register(email: String, password: String, fullName: String, age: Int, weight: Int, height: Int): Boolean {
        var isRegistrationSuccessful = true
        firebaseAuth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Registration successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    isRegistrationSuccessful = true
                    val user = User(email, fullName, age, weight, height)
                    FirebaseReferences.usersReference?.child(firebaseAuth!!.currentUser!!.uid)?.setValue(user)
                } else {
                    Toast.makeText(
                        context,
                        "Registration failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                    isRegistrationSuccessful = false
                }
            })
        return isRegistrationSuccessful
    }

    fun login(email: String, password: String): Boolean {
        var isLoginSuccessful = true
        firebaseAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login successful!", Toast.LENGTH_LONG).show()
                    isLoginSuccessful = true
                } else {
                    Toast.makeText(context, "Login failed! Please try again later", Toast.LENGTH_LONG).show()
                    isLoginSuccessful = false
                }
            })
        return isLoginSuccessful
    }

    fun getUser(): com.google.firebase.auth.FirebaseUser? {
        return firebaseAuth?.currentUser
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth?.currentUser != null
    }

    fun logOut() {
        firebaseAuth?.signOut()
    }
}
