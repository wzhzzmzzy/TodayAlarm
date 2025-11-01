package com.busylab.todayalarm.domain.model

import kotlinx.datetime.LocalDateTime

data class TodoItemUiModel(
    val id: String,
    val planId: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean,
    val triggerTime: LocalDateTime,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val formattedTime: String = "",
    val formattedDate: String = "",
    val timeAgo: String = "", // 相对时间显示，如"2小时前"
    val isOverdue: Boolean = false
)