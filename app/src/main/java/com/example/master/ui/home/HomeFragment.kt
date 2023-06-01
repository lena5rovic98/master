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
import com.example.master.extensions.normalize
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.MinMaxNormValues
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
                    FirebaseReferences.inputData.smiles = if (smilingCount > it.count()) 1F else 0F
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
                    FirebaseReferences.inputData.sms = if (sentSMSCount > it.count()) 1F else 0F
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
                    FirebaseReferences.inputData.phone = if (outgoingCallsCount > it.count()) 1F else 0F
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
                    FirebaseReferences.inputData.social = if (socialTime > totalTime * 0.5) 1F else 0F
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
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime).toInt()
                    FirebaseReferences.inputData.displayTime = minutes.normalize(MinMaxNormValues.displayTimeMin, MinMaxNormValues.displayTimeMax)
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
                FirebaseReferences.inputData.steps = 35.normalize(MinMaxNormValues.stepsMin, MinMaxNormValues.stepsMax).toFloat()
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