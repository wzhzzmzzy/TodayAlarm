package com.busylab.todayalarm.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {

    fun formatTime(timestamp: Long, is24Hour: Boolean = true): String {
        val format = if (is24Hour) "HH:mm" else "hh:mm a"
        return SimpleDateFormat(format, Locale.getDefault()).format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long, is24Hour: Boolean = true): String {
        val timeFormat = if (is24Hour) "HH:mm" else "hh:mm a"
        val format = "yyyy-MM-dd $timeFormat"
        return SimpleDateFormat(format, Locale.getDefault()).format(Date(timestamp))
    }

    fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getEndOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    fun getStartOfWeek(timestamp: Long, weekStartDay: Int = Calendar.MONDAY): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.firstDayOfWeek = weekStartDay
        calendar.set(Calendar.DAY_OF_WEEK, weekStartDay)
        return getStartOfDay(calendar.timeInMillis)
    }

    fun getEndOfWeek(timestamp: Long, weekStartDay: Int = Calendar.MONDAY): Long {
        val startOfWeek = getStartOfWeek(timestamp, weekStartDay)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startOfWeek
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        return getEndOfDay(calendar.timeInMillis)
    }
}