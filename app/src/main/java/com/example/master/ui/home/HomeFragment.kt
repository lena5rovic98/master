package com.example.master.ui.home

import android.os.Build
import android.os.Bundle
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
import java.util.concurrent.TimeUnit

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
                        it.smilingProbability > 0.5
                    }.count()

                    val faces = ActivityObject(
                        ActivityEnum.FACES,
                        smilingCount,
                        "smiles",
                        it.size,
                        R.drawable.ic_smile
                    )
                    updateRecyclerView(faces)
                }
            }
        }

        this.let {
            homeViewModel.messages.observe(this) {
                val sentSMSCount = it.filter {
                    it.type == "sent"
                }.count()

                if (it.isNotEmpty()) {
                    val sms = ActivityObject(
                        ActivityEnum.SMS,
                        sentSMSCount,
                        "SMS",
                        it.size,
                        R.drawable.ic_sms
                    )
                    updateRecyclerView(sms)
                }
            }
        }

        this.let {
            homeViewModel.calls.observe(this) {
                val outgoingCallsCount = it.filter {
                    it.callType == "OUTGOING"
                }.count()
                if (it.isNotEmpty()) {
                    val calls = ActivityObject(
                        ActivityEnum.PHONE_CALLS,
                        outgoingCallsCount,
                        "calls",
                        it.size,
                        R.drawable.ic_phone_call
                    )
                    updateRecyclerView(calls)
                }
            }
        }

        this.let {
            homeViewModel.usageStat.observe(this) {
                var socialTime = 0L
                var totalTime = 0L

                it.map {  totalTime += it.totalTime }

                it.filter{
                    it.isSocialNetwork
                }. map {
                    socialTime += it.totalTime
                }

                // social time
                if (totalTime != 0L) { // total time, because user can use only non social apps :)
                    val socialTimeObject = ActivityObject(
                        ActivityEnum.SOCIAL_NETWORKS,
                        socialTime.toInt(),
                        "h/min",
                        totalTime.toInt(),
                        R.drawable.ic_social_network
                    )
                    updateRecyclerView(socialTimeObject)
                }

                // display time
                if (totalTime != 0L) {
                    val displayTimeObject = ActivityObject(
                        ActivityEnum.DISPLAY_TIME,
                        totalTime.toInt(),
                        "h/min",
                        TimeUnit.HOURS.toMillis(2).toInt(), // 2 hours in milliseconds
                        R.drawable.ic_app_usage
                    )
                    updateRecyclerView(displayTimeObject)
                }
            }
        }

        this.let {
            homeViewModel.steps.observe(this) {
                val steps = ActivityObject(
                    ActivityEnum.STEPS,
                    it ?: 0,
                    "steps",
                    100, // TODO: set to 6000, 10000
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