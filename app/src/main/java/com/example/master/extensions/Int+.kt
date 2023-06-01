package com.example.master.extensions

fun Int.normalize(min: Float, max: Float): Float {
    return (this - min) / (max - min)
}