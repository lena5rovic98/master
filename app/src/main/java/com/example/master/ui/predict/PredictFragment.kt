package com.example.master.ui.predict

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.databinding.FragmentPredictBinding
import com.example.master.ml.UserData
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class PredictFragment : Fragment() {

  private var _binding: FragmentPredictBinding? = null
  private val binding get() = _binding!!

  private lateinit var predictViewModel: PredictViewModel

  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentPredictBinding.inflate(inflater, container, false)
    predictViewModel = ViewModelProvider(this).get(PredictViewModel::class.java)

    setListeners()
    predictCondition()

    return binding.root
  }

  private fun setListeners() {
    binding.buttonPredict.setOnClickListener {

    }
  }

  private fun predictCondition() {
    // calls, sms, mobile use, activity, danger.

    val model = UserData.newInstance(requireContext())
    // Creates inputs for reference.
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 5), DataType.FLOAT32)
    inputFeature0.loadArray(
      intArrayOf(
      //  2, 0, 1, 0, 1
       2, 1, 1, 2, 0
      )
    )

    // Runs model inference and gets result.
    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

    if (outputFeature0[0] > 0.5) {
      binding.labelOutput.text = "In danger? Yes - ${outputFeature0[0]}"
    } else {
      binding.labelOutput.text = "In danger? No - ${outputFeature0[0]}"
    }

    // Releases model resources if no longer used.
    model.close()

  }
}
