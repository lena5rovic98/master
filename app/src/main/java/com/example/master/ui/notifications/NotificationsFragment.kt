package com.example.master.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.master.R
import android.media.AudioManager
import android.widget.Toast

import androidx.core.content.ContextCompat.getSystemService




class NotificationsFragment : Fragment() {

  private lateinit var notificationsViewModel: NotificationsViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_notifications, container, false)
    val textView: TextView = root.findViewById(R.id.text_notifications)
    notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
      textView.text = it
    })

    // getRingtoneVolume()


    return root
  }

  private fun getRingtoneVolume() {
    try {
      val audio = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
      val currentRingVolume = audio?.getStreamVolume(AudioManager.STREAM_RING)
      val currentMusicVolume = audio?.getStreamVolume(AudioManager.STREAM_MUSIC)
      val currentSystemVolume = audio?.getStreamVolume(AudioManager.STREAM_SYSTEM)
      if (currentRingVolume != null) {
        Toast.makeText(context, currentRingVolume, Toast.LENGTH_LONG).show()
      }
    } catch (e: Exception) {
      Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
    }

  }
}