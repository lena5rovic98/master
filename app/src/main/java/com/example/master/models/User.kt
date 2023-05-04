package com.example.master.models

data class User(
    val email: String = "",
    val fullName: String = "",
    val age: Int = 0,
    val weight: Int = 0,
    val height: Int = 0,
    val shareData: Boolean = true
)
