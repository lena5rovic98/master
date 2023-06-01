package com.example.master.models

data class Prediction(
    var age: Float = 0.0F,
    var weight: Float = 0.0F,
    var height: Float = 0.0F,
    var gender: Float = 0.0F,
    var smoke: Float = 0.0F,
    var steps: Float = 0.0F,
    var displayTime: Float = 0.0F,
    var smiles: Float = 0.0F,
    var sms: Float = 0.0F,
    var phone: Float = 0.0F,
    var social: Float = 0.0F,
    var prediction: String = "NONE"
)
