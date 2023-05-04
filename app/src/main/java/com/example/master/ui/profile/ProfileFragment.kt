package com.example.master.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.databinding.FragmentProfileBinding
import com.example.master.ui.firebase.FirebaseAuthentication

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
                binding.editTextAge.setText(it.age)
                binding.editTextWeight.setText(it.weight.toString())
                binding.editTextHeight.setText(it.height)
                binding.switchAllowAccess.isChecked = it.shareData
            }
         }
    }

    private fun setListeners() {
        binding.buttonLogout.setOnClickListener {
            FirebaseAuthentication.logOut()
            activity?.finish()
        }
    }
}
