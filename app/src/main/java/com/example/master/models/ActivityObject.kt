package com.example.master.models

import com.example.master.enum.ActivityEnum

data class ActivityObject(
    val type: ActivityEnum,
    val value: Int,
    val measurementUnit: String,
    val referenceValue: Int,
    val icon: String
)