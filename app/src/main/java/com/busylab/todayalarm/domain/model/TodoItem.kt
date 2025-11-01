package com.busylab.todayalarm.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * 待办事项领域模型
 */
data class TodoItem(
    val id: String,
    val planId: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean = false,
    val triggerTime: LocalDateTime,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime
)