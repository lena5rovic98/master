package com.example.master.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.master.MainActivity
import com.example.master.databinding.ActivityWelcomeBinding
import com.example.master.ui.firebase.FirebaseAuthentication

class WelcomeActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseAuthentication.getInstance(this)

        if (FirebaseAuthentication.isUserLoggedIn()) {
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
}
