package com.example.master.helpers

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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

    @SuppressLint("SimpleDateFormat")
    fun getDateId(dateInMilliseconds: Long): String {
        val formatter = SimpleDateFormat("ddMMyyyy")
        return formatter.format(Date(dateInMilliseconds))
    }

    fun getTodaysDateMilliseconds(): Long { // in format 2023/05/11 00:00:00" in milliseconds
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        val month = if (currentMonth < 10) "0${currentMonth}" else currentMonth
        val day = if (Calendar.getInstance().get(Calendar.DATE) < 10) "0${
            Calendar.getInstance().get(Calendar.DATE)
        }" else Calendar.getInstance().get(Calendar.DATE)

        val date = "$year/$month/$day 00:00:00"
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val todaysDate: Date = formatter.parse(date)
        return todaysDate.time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPreviousDateMilliseconds(numberOfDaysInPast: Int): Long {
        val now: Instant = Instant.now()
        val yesterday: Instant = now.minus(numberOfDaysInPast.toLong(), ChronoUnit.DAYS)

        val year = yesterday.toString().take(4)
        val month = yesterday.toString().take(7).drop(5)
        val day = yesterday.toString().take(9).drop(7)

        val date = "$year/$month/$day 00:00:00"
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val previousDate: Date = formatter.parse(date)
        return previousDate.time
    }
}
