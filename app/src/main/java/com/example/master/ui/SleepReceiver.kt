package com.example.master.ui

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent

class SleepReceiver: BroadcastReceiver() {

    companion object {
        fun createPendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, SleepReceiver::class.java)

            return PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }

    }

    override fun onReceive(p0: Context?, intent: Intent?) {
        var message = ""
        if (SleepSegmentEvent.hasEvents(intent)) {
            val events = SleepSegmentEvent.extractEvents(intent)
            for (event in events) {

                message = "${event.startTimeMillis} to ${event.endTimeMillis} with status ${event.status}"
                Log.d("SLEEP", "${event.startTimeMillis} to ${event.endTimeMillis} with status ${event.status}")
            }
        } else if (SleepClassifyEvent.hasEvents(intent)) {
            val events = SleepClassifyEvent.extractEvents(intent)

            for (event in events) {
                message = "Confidence: ${event.confidence} - Light: ${event.light} - Motion: ${event.motion}"
                Log.d("SLEEP", "Confidence: ${event.confidence} - Light: ${event.light} - Motion: ${event.motion}")
            }
        }
    }
}