package com.example.master.ui.predict

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.example.master.*
import com.example.master.databinding.DialogPredictionBinding
import com.example.master.enum.PredictionEnum
import com.example.master.webView.WebViewActivity

class PredictionDialog (
    context: Context,
    private val prediction: PredictionEnum
): Dialog(context) {

    private var _binding: DialogPredictionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DialogPredictionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        setListeners()
    }

    @SuppressLint("StringFormatMatches")
    private fun initData() {
        when (prediction) {
            PredictionEnum.STRESS -> {
                binding.labelPredict.text = context.getString(R.string.title_stress)
                binding.imageResult.setImageResource(R.drawable.ic_stress)
                binding.labelResult.text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.label_prediction,
                        context.getString(R.string.title_stress),
                        "display time, outgoing calls and sent messages"
                    ),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
            PredictionEnum.ANXIETY -> {
                binding.labelPredict.text = context.getString(R.string.title_anxiety)
                binding.imageResult.setImageResource(R.drawable.ic_anxiety)
                binding.labelResult.text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.label_prediction,
                        context.getString(R.string.title_anxiety),
                        "social and display time"
                    ),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
            PredictionEnum.DEPRESSION -> {
                binding.labelPredict.text = context.getString(R.string.title_depression)
                binding.imageResult.setImageResource(R.drawable.ic_depression)
                binding.labelResult.text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.label_prediction,
                        context.getString(R.string.title_depression),
                        "steps number, smiles count, calls, messages and social time"
                    ),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
            PredictionEnum.NONE -> {
                binding.labelPredict.text = context.getString(R.string.title_none)
                binding.imageResult.setImageResource(R.drawable.ic_harmless)
                binding.labelResult.text = HtmlCompat.fromHtml(
                    context.getString(
                        R.string.label_prediction,
                        context.getString(R.string.title_none),
                        "all the data"
                    ),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
        }
    }

    private fun setListeners() {
        binding.labelResult.setOnClickListener {
            val webView = Intent(context, WebViewActivity::class.java)
            when (prediction) {
                PredictionEnum.STRESS -> webView.putExtra("url", context.getString(R.string.link_stress))
                PredictionEnum.ANXIETY -> webView.putExtra("url", context.getString(R.string.link_anxiety))
                PredictionEnum.DEPRESSION -> webView.putExtra("url", context.getString(R.string.link_depression))
            }
            context.startActivity(webView)
        }
    }
}

