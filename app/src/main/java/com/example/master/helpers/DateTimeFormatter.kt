package com.example.master.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

object DateTimeFormatter {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateTimeId(dateTime: LocalDateTime): String {
        val day = if (dateTime.dayOfMonth < 10) "0${dateTime.dayOfMonth}" else dateTime.dayOfMonth
        val month = if (dateTime.monthValue < 10) "0${dateTime.monthValue}" else dateTime.monthValue
        val hour = if (dateTime.hour < 10) "0${dateTime.hour}" else dateTime.hour
        val minute = if (dateTime.minute < 10) "0${dateTime.minute}" else dateTime.minute

        return "$day$month${dateTime.year}$hour$minute"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateId(dateTime: LocalDateTime): String {
        val day = if (dateTime.dayOfMonth < 10) "0${dateTime.dayOfMonth}" else dateTime.dayOfMonth
        val month = if (dateTime.monthValue < 10) "0${dateTime.monthValue}" else dateTime.monthValue

        return "$day$month${dateTime.year}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeId(dateTime: LocalDateTime): String {
        val hour = if (dateTime.hour < 10) "0${dateTime.hour}" else dateTime.hour
        val minute = if (dateTime.minute < 10) "0${dateTime.minute}" else dateTime.minute
        val second = if (dateTime.second < 10) "0${dateTime.second}" else dateTime.second

        return "$hour$minute$second"
    }
}