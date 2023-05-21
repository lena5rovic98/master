package com.example.master.models

data class SMS(
    val phoneNumber: String = "",
    val type: String = "", // "received", "sent" TODO: enum
    val body: String = "",
    val date: String = ""
)
