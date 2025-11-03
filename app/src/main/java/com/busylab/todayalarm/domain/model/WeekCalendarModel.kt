package com.busylab.todayalarm.domain.model

import kotlinx.datetime.LocalDate

data class WeekCalendarModel(
    val days: List<DayModel>,
    val weekRange: String, // 如 "2024年1月1日 - 1月7日"
    val currentWeekOffset: Int = 0 // 相对于当前周的偏移量
)

data class DayModel(
    val date: LocalDate,
    val dayOfWeek: String, // 如 "周一"
    val dayOfMonth: Int,
    val isToday: Boolean,
    val isCurrentMonth: Boolean,
    val todoItems: List<TodoItem> = emptyList(),
    val plans: List<PlanUiModel> = emptyList(),
    val hasEvents: Boolean = false
)