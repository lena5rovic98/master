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
import com.example.master.enum.DangerEnum
import com.example.master.enum.PredictionEnum
import com.example.master.ml.TfliteModel1
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

    return binding.root
  }

  private fun setListeners() {
    binding.buttonCheck.setOnClickListener {
      checkIfInDanger()
    }
    binding.buttonPredictToday.setOnClickListener {
      predict()
    }
    binding.buttonPredictWeekly.setOnClickListener {
      predict()
    }
  }

  private fun checkIfInDanger() {
    // calls, sms, mobile use, activity, danger.

    val model = UserData.newInstance(requireContext())
    // Creates inputs for reference.
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 5), DataType.FLOAT32)
    inputFeature0.loadArray(
      intArrayOf(
//        2, 0, 1, 0, 1 // no
       2, 1, 1, 2, 0 // yes
      )
    )

    // Runs model inference and gets result.
    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

    val inDanger: DangerEnum
    if (outputFeature0[0] > 0.5) {
      binding.labelOutput.text = "In danger? Yes - ${outputFeature0[0]}"
      inDanger = DangerEnum.YES
    } else {
      binding.labelOutput.text = "In danger? No - ${outputFeature0[0]}"
      inDanger = DangerEnum.NO
    }

    // Releases model resources if no longer used.
    model.close()

    val predictDialog = DangerDialog(
      requireContext(),
      inDanger
    )
    predictDialog.show()
  }

  private fun predict() {
    val model = TfliteModel1.newInstance(requireContext())

    // Creates inputs for reference.
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 11), DataType.FLOAT32)
    //inputFeature0.loadBuffer(byteBuffer)
    inputFeature0.loadArray(
      intArrayOf(
       44,65,175,1,0,18698,467,0,2,2,1
//         30,81,200,1,0,10821,389,1,1,2,1 
//        48,103,197,1,1,19290,69,0,2,1,0 // 3
      )
    )

    // Runs model inference and gets result.
    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
    val array = outputs.outputFeature0AsTensorBuffer.floatArray
    binding.labelOutput.text = "${array[0]}, ${array[1]}, ${array[2]}, ${array[3]}"

    val index = array.indexOfFirst { it == 1.0f }
    var predictionResult = PredictionEnum.NONE
    when (index) {
      0 -> predictionResult = PredictionEnum.STRESS
      1 -> predictionResult = PredictionEnum.ANXIETY
      2 -> predictionResult = PredictionEnum.DEPRESSION
      3 -> predictionResult = PredictionEnum.NONE
    }

    val predictionDialog = PredictionDialog(
      requireContext(),
      predictionResult
    )
    predictionDialog.show()

    // Releases model resources if no longer used.
    model.close()
  }
}
