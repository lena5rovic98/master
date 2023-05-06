package com.example.master.models

data class DetectedFace(
    val smilingProbability: Float = 0f,
    val leftEyeOpenProbability: Float = 0f,
    val rightEyeOpenProbability: Float = 0f,
    val dateTime: String = "",
    val location: NetworkLocation? = null
)
