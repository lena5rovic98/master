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
import com.example.master.extensions.normalize
import com.example.master.firebase.FirebaseReferences
import com.example.master.helpers.MinMaxNormValues
import com.example.master.ml.*
import com.example.master.models.Prediction
import com.example.master.models.User
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class PredictFragment : Fragment() {

  private var _binding: FragmentPredictBinding? = null
  private val binding get() = _binding!!

  private lateinit var predictViewModel: PredictViewModel

  private var userData: User? = null
  private var age: Float = 0.0f
  private var weight: Float = 0.0f
  private var height: Float = 0.0f
  private var gender: Float = 0.0f
  private var smoke: Float = 0.0f

  @RequiresApi(Build.VERSION_CODES.Q)
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentPredictBinding.inflate(inflater, container, false)
    predictViewModel = ViewModelProvider(this)[PredictViewModel::class.java]

    observeData()
    setListeners()

    predictViewModel.getUserData()

    return binding.root
  }

  private fun observeData() {
    predictViewModel.user.observe(viewLifecycleOwner) {
      if (userData != User()) {
        userData = it

        age = it.age.normalize(MinMaxNormValues.ageMin, MinMaxNormValues.ageMax)
        weight = it.weight.normalize(MinMaxNormValues.weightMin, MinMaxNormValues.weightMax)
        height = it.height.normalize(MinMaxNormValues.heightMin, MinMaxNormValues.heightMax)
        gender = it.gender.toFloat()
        smoke = it.smoke.toFloat()
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
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
    // calls, sms, smiles, mobile use, activity

    val model = UserData.newInstance(requireContext())
    // Creates inputs for reference.
    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 5), DataType.FLOAT32)
//    inputFeature0.loadArray(
//      intArrayOf(
////        2, 0, 1, 0, 1 // no
//       2, 1, 1, 2, 0 // yes
//      )
//    )

    val displayTime = if (FirebaseReferences.displayTime < 100) 0 else if (FirebaseReferences.displayTime > 100 && FirebaseReferences.displayTime < 240) 1 else 2
    val steps = if (FirebaseReferences.steps < 1000) 0 else if (FirebaseReferences.steps > 1000 && FirebaseReferences.displayTime < 10000) 1 else 2

    inputFeature0.loadArray(
      floatArrayOf(
        FirebaseReferences.phone,
        FirebaseReferences.sms,
        FirebaseReferences.smiles,
        displayTime.toFloat(),
        steps.toFloat()
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

  @RequiresApi(Build.VERSION_CODES.O)
  private fun predict() {
    val model = TfliteModel4.newInstance(requireContext())

    // Creates inputs for reference.
    val inputFeatureNorm = TensorBuffer.createFixedSize(intArrayOf(1, 11), DataType.FLOAT32)
    inputFeatureNorm.loadArray(
      floatArrayOf(
//        0.7380952380952379F, 0.8999999999999999F, 0.16000000000000014F, 1.0F, 1.0F, 0.6027246318741861F, 0.08541666666666667F, 0.0F, 0.0F, 0.0F, 0.0F // 0 0 1 0

//        0.16666666666666669F,
//        0.8714285714285714F,
//        0.2400000000000002F,
//        1.0F,
//        0.0F,
//        0.9510167284383452F,
//        0.07291666666666667F,
//        2.0F,
//        2.0F,
//        1.0F,
//        0.0F // 1. 0. 0. 0.


        age,
        weight,
        height,
        gender,
        smoke,
        FirebaseReferences.steps,
        FirebaseReferences.displayTime,
        FirebaseReferences.smiles,
        FirebaseReferences.sms,
        FirebaseReferences.phone,
        FirebaseReferences.social
      )
    )

    // Runs model inference and gets result.
    val outputs = model.process(inputFeatureNorm)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
    val array = outputs.outputFeature0AsTensorBuffer.floatArray
    val max = array.maxOrNull()

    val index = array.indexOfFirst { it == max }
    var predictionResult = PredictionEnum.NONE
    when (index) {
      0 -> predictionResult = PredictionEnum.STRESS
      1 -> predictionResult = PredictionEnum.ANXIETY
      2 -> predictionResult = PredictionEnum.DEPRESSION
      3 -> predictionResult = PredictionEnum.NONE
    }

    val prediction = Prediction(
      age,
      weight,
      height,
      gender,
      smoke,
      FirebaseReferences.steps,
      FirebaseReferences.displayTime,
      FirebaseReferences.smiles,
      FirebaseReferences.sms,
      FirebaseReferences.phone,
      FirebaseReferences.social,
      predictionResult.name
    )

    predictViewModel.writePrediction(
      prediction
    )
    val predictionDialog = PredictionDialog(
      requireContext(),
      predictionResult
    )
    predictionDialog.show()

    // Releases model resources if no longer used.
    model.close()
  }
}
