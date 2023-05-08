package com.example.master.helpers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*

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
    fun getDateId(dateTime: Date): String {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Belgrade"))
        cal.time = dateTime
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH] + 1 // January = 0, February = 1 ... December = 11
        val day = cal[Calendar.DAY_OF_MONTH]

        val monthId = if (month < 10) "0$month" else month
        val dayId = if (day < 10) "0$day" else day

        return "$dayId$monthId${year}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeId(dateTime: LocalDateTime): String {
        val hour = if (dateTime.hour < 10) "0${dateTime.hour}" else dateTime.hour
        val minute = if (dateTime.minute < 10) "0${dateTime.minute}" else dateTime.minute
        val second = if (dateTime.second < 10) "0${dateTime.second}" else dateTime.second

        return "$hour$minute$second"
    }

    fun getTimeId(dateTime: Date): String {
        val hour = if (dateTime.hours < 10) "0${dateTime.hours}" else dateTime.hours
        val minute = if (dateTime.minutes < 10) "0${dateTime.minutes}" else dateTime.minutes
        val second = if (dateTime.seconds < 10) "0${dateTime.seconds}" else dateTime.seconds

        return "$hour$minute$second"
    }
}
