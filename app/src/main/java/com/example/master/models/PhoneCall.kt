package com.example.master.models

data class PhoneCall(
    val phoneNumber: String = "",
    val callType: String = "", // OUTGOING, INCOMING, MISSED, REJECTED TODO: enum
    val date: String = "", // GMT+2
    val duration: String = ""
)
