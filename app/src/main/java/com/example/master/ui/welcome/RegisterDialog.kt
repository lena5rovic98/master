package com.example.master.ui.welcome

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.master.databinding.DialogRegisterBinding
import com.example.master.firebase.FirebaseAuthentication

class RegisterDialog(context: Context): Dialog(context) {

    private var _binding: DialogRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DialogRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
        val height = (context.resources.displayMetrics.heightPixels * 0.9).toInt()

        window?.setLayout(width, height)

        setListeners()
    }

    private fun setListeners() {
        binding.buttonRegister.setOnClickListener {
            if (
                FirebaseAuthentication.register(
                    binding.editTextEmail.text.toString(),
                    binding.editTextPassword.text.toString(),
                    binding.editTextFullName.text.toString(),
                    Integer.parseInt(binding.editTextAge.text.toString()),
                    Integer.parseInt(binding.editTextWeight.text.toString()),
                    Integer.parseInt(binding.editTextHeight.text.toString())
                )
            ) {
                this.dismiss()
            }
        }
    }
}