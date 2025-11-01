package com.busylab.todayalarm.domain.model

import kotlinx.datetime.LocalDateTime

data class PlanUiModel(
    val id: String,
    val title: String,
    val content: String,
    val triggerTime: LocalDateTime,
    val isRepeating: Boolean,
    val repeatType: RepeatType,
    val repeatInterval: Int,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val nextTriggerTime: LocalDateTime? = null, // 下次触发时间
    val formattedTime: String = "", // 格式化的时间显示
    val formattedDate: String = "", // 格式化的日期显示
    val isOverdue: Boolean = false // 是否已过期
)