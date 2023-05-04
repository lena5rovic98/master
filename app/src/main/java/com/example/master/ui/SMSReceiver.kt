package com.example.master.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SMSReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        // not working
        if (intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val a = 3
        }
    }
}