package com.busylab.todayalarm.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * 待办事项领域模型
 */
data class TodoItem(
    val id: String,
    val planId: String? = null, // 改为可选，支持独立待办
    val title: String,
    val content: String,
    val isCompleted: Boolean = false,
    val triggerTime: LocalDateTime,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime,

    // 新增重复设置字段
    val enableRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatInterval: Int = 1,
    val isActive: Boolean = true
)