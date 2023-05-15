package com.example.master.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.databinding.FragmentHomeBinding
import com.example.master.enum.ActivityEnum
import com.example.master.models.ActivityObject
import com.example.master.models.DetectedFace
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel
    private var activityObjects: ArrayList<ActivityObject> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
      inflater: LayoutInflater,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        readDataForRecyclerView()
        observeDataForRecyclerView()

        initRecyclerView()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun readDataForRecyclerView() {
        homeViewModel.readDetectedFacesData()
        homeViewModel.readSMS()
        homeViewModel.readCallLog()
        homeViewModel.readUsageStatistics()
        homeViewModel.readSteps()
    }

    private fun observeDataForRecyclerView() {
        this.let {
            homeViewModel.detectedFaces.observe(this) {
                val faces = it
                val smilling = faces.filter {
                    it.smilingProbability > 1
                }
                Log.d("Faces num: ", faces.size.toString())
            }
        }

        this.let {
            homeViewModel.messages.observe(this) {
                val sms = it
                Log.d("SMS num: ", sms.size.toString())
            }
        }

        this.let {
            homeViewModel.calls.observe(this) {
                val phoneCalls = it
                Log.d("Phone calls num: ", phoneCalls.size.toString())
            }
        }

        this.let {
            homeViewModel.usageStat.observe(this) {
                val statistics = it
                Log.d("Statistics: ", statistics.size.toString())
            }
        }

        this.let {
            homeViewModel.steps.observe(this) {
                val steps = ActivityObject(
                    ActivityEnum.STEPS,
                    it,
                    "steps",
                    5000,
                    "ic_step"
                )
                activityObjects.add(steps)

                Log.d("Steps: ", steps.toString())
            }
        }
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