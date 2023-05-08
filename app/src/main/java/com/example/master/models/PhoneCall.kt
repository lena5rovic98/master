package com.example.master.models

data class PhoneCall(
    val phoneNumber: String = "",
    val callType: String = "", // OUTGOING, INCOMING, MISSED, REJECTED
    val date: String = "", // GMT+2
    val duration: String = ""
)
