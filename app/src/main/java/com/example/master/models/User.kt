package com.example.master.models

data class User(
    val email: String = "",
    val fullName: String = "",
    val age: Int = 0,
    val weight: Float = 0f,
    val height: Int = 0,
    val shareData: Boolean = true
)
