package com.example.master.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.databinding.FragmentHomeBinding
import com.example.master.models.DetectedFace
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
      _binding = FragmentHomeBinding.inflate(inflater, container, false)

      homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

      initRecyclerView()

      return binding.root
    }

    private fun initRecyclerView() {
      if (binding.recyclerViewSummary.adapter != null) return
      binding.recyclerViewSummary.adapter = SummaryRecyclerViewAdapter()
      val faces = ArrayList<DetectedFace>()
      faces.add(DetectedFace())
      faces.add(DetectedFace())
      faces.add(DetectedFace())
      faces.add(DetectedFace())
      faces.add(DetectedFace())
      faces.add(DetectedFace())
      (binding.recyclerViewSummary.adapter as SummaryRecyclerViewAdapter).submitList(faces)
    }

}