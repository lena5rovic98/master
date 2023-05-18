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
import com.example.master.R
import com.example.master.databinding.FragmentHomeBinding
import com.example.master.enum.ActivityEnum
import com.example.master.models.ActivityObject
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

        initRecyclerView()

        readDataForRecyclerView()
        observeDataForRecyclerView()

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
                if (it.isNotEmpty()) {
                    val smilingCount = it.filter {
                        it.smilingProbability > 1
                    }.count()

                    val faces = ActivityObject(
                        ActivityEnum.FACES,
                        smilingCount,
                        "smiles",
                        it.size,
                        R.drawable.ic_step
                    )
                    updateRecyclerView(faces)
                }
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
                    it ?: 0,
                    "steps",
                    5000,
                    R.drawable.ic_step
                )
                updateRecyclerView(steps)
            }
        }
    }

    private fun initRecyclerView() {
      if (binding.recyclerViewSummary.adapter != null) return
      binding.recyclerViewSummary.adapter = SummaryRecyclerViewAdapter()
    }

    private fun updateRecyclerView(activityObject: ActivityObject) {
        activityObjects.add(activityObject)
        (binding.recyclerViewSummary.adapter as SummaryRecyclerViewAdapter).submitList(activityObjects)
        (binding.recyclerViewSummary.adapter as SummaryRecyclerViewAdapter).notifyDataSetChanged()
    }
}