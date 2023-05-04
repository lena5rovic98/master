package com.example.master.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.master.MainActivity
import com.example.master.databinding.ActivityWelcomeBinding
import com.example.master.firebase.FirebaseReferences
import com.example.master.models.User
import com.example.master.ui.firebase.FirebaseAuthentication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class WelcomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAuthentication.getInstance(this)

        if (FirebaseAuthentication.isUserLoggedIn()) {
            getUser()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            setListeners()
        }
    }

    private fun setListeners() {
        binding.buttonLogin.setOnClickListener {
            if (
                FirebaseAuthentication.login(
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString()
                )
            ) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        binding.labelDontHaveAccount.setOnClickListener {
            val registerDialog = RegisterDialog(this)
            registerDialog.show()
        }
    }

    fun getUser() {
        val userData = FirebaseReferences.usersReference?.child(FirebaseAuthentication.getUser()?.uid!!)
//        val rootRef = FirebaseDatabase.getInstance().reference
//        var usersRef: DatabaseReference? = rootRef.child("users").child(userId)
        userData?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User = snapshot.getValue(User::class.java) as User
                val a = user
                // _user.value = user
            }

            override fun onCancelled(error: DatabaseError) {
                throw error.toException()
            }
        }
        )
    }
}