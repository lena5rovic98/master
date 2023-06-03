package com.example.master.ui.trends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.master.R

class TrendsFragment : Fragment() {

  private lateinit var trendsViewModel: TrendsViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    trendsViewModel =
            ViewModelProvider(this).get(TrendsViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_trends, container, false)
    val textView: TextView = root.findViewById(R.id.text_notifications)

    return root
  }
}
