package com.busylab.todayalarm.domain.model

import kotlinx.datetime.*

data class RepeatRule(
    val type: RepeatType,
    val interval: Int = 1,
    val endDate: LocalDateTime? = null,
    val maxOccurrences: Int? = null,
    val daysOfWeek: Set<DayOfWeek> = emptySet(), // 周重复时指定星期几
    val dayOfMonth: Int? = null, // 月重复时指定几号
    val monthOfYear: Int? = null // 年重复时指定几月
) {
    fun getNextTriggerTime(currentTime: LocalDateTime): LocalDateTime? {
        return when (type) {
            RepeatType.NONE -> null
            RepeatType.DAILY -> addDays(currentTime, interval)
            RepeatType.WEEKLY -> addDays(currentTime, interval * 7)
            RepeatType.MONTHLY -> addMonths(currentTime, interval)
            RepeatType.YEARLY -> addYears(currentTime, interval)
        }
    }

    private fun addDays(dateTime: LocalDateTime, days: Int): LocalDateTime {
        val instant = dateTime.toInstant(TimeZone.currentSystemDefault())
        val newInstant = instant.plus(days, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        return newInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private fun addMonths(dateTime: LocalDateTime, months: Int): LocalDateTime {
        val instant = dateTime.toInstant(TimeZone.currentSystemDefault())
        val newInstant = instant.plus(months, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
        return newInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private fun addYears(dateTime: LocalDateTime, years: Int): LocalDateTime {
        val instant = dateTime.toInstant(TimeZone.currentSystemDefault())
        val newInstant = instant.plus(years, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
        return newInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    }

    private fun calculateCustomRepeat(currentTime: LocalDateTime): LocalDateTime? {
        // 简化的自定义重复逻辑实现
        return when {
            daysOfWeek.isNotEmpty() -> addDays(currentTime, 7) // 简化为每周重复
            dayOfMonth != null -> addMonths(currentTime, 1) // 简化为每月重复
            monthOfYear != null -> addYears(currentTime, 1) // 简化为每年重复
            else -> null
        }
    }
}