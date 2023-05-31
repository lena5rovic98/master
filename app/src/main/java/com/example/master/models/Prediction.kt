package com.example.master.models

data class Prediction(
    val age: Float = 0.0F,
    val weight: Float = 0.0F,
    val height: Float = 0.0F,
    val gender: Float = 0.0F,
    val smoke: Float = 0.0F,
    val steps: Float = 0.0F,
    val displayTime: Float = 0.0F,
    val smiles: Float = 0.0F,
    val sms: Float = 0.0F,
    val phone: Float = 0.0F,
    val social: Float = 0.0F,
    val prediction: String = "NONE"
)
