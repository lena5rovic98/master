package com.example.master.ui.predict

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.example.master.R
import com.example.master.databinding.DialogPredictionBinding
import com.example.master.enum.DangerEnum

class DangerDialog(
    context: Context,
    private val inDanger: DangerEnum
): Dialog(context) {

    private var _binding: DialogPredictionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DialogPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
        // val height = (context.resources.displayMetrics.heightPixels * 0.9).toInt()

        window?.setLayout(width, WRAP_CONTENT)

        initData()
    }

    private fun initData() {
        when (inDanger) {
            DangerEnum.YES -> {
                binding.imageResult.setImageResource(R.drawable.ic_danger)
                binding.labelResult.text = context.getString(R.string.label_dangerous_condition)
            }
            DangerEnum.NO -> {
                binding.imageResult.setImageResource(R.drawable.ic_harmless)
                binding.labelResult.text = context.getString(R.string.label_non_dangerous_condition)
            }
        }
    }
}