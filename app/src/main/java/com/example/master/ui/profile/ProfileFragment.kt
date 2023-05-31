package com.example.master.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.R
import com.example.master.databinding.FragmentProfileBinding
import com.example.master.firebase.FirebaseAuthentication

class ProfileFragment: Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        getData()
        initData()
        setListeners()

        return binding.root
    }

    private fun getData() {
        profileViewModel.getUser()
    }

    private fun initData() {
        activity?.let {
            profileViewModel.user.observe(
                viewLifecycleOwner
            ) {
                binding.labelFullName.text = it.fullName
                binding.labelEmail.text = it.email
                binding.editTextAge.setText(it.age.toString())
                binding.editTextWeight.setText(it.weight.toString())
                binding.editTextHeight.setText(it.height.toString())
                binding.radioFemale.isChecked = it.gender == 1
                binding.radioMale.isChecked = it.gender == 0
                binding.switchSmoker.isChecked = it.smoke != 0
                binding.switchAllowAccess.isChecked = it.shareData
            }
        }
    }

    private fun setListeners() {
        binding.buttonLogout.setOnClickListener {
            FirebaseAuthentication.logOut()
            activity?.finish()
        }

        binding.buttonSaveChanges.setOnClickListener {
            profileViewModel.updateUserData(
                Integer.parseInt(binding.editTextAge.text.toString()),
                Integer.parseInt(binding.editTextWeight.text.toString()),
                Integer.parseInt(binding.editTextHeight.text.toString()),
                if (binding.radioFemale.isChecked) 1 else 0,
                if (binding.switchSmoker.isChecked) 1 else 0,
                binding.switchAllowAccess.isChecked
            )
            getData()
            Toast.makeText(context, R.string.toast_user_updated, Toast.LENGTH_LONG).show()
        }
    }
}
