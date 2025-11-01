package com.busylab.todayalarm.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * 计划领域模型
 */
data class Plan(
    val id: String,
    val title: String,
    val content: String,
    val triggerTime: LocalDateTime,
    val isRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatInterval: Int = 1,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

/**
 * 重复类型
 */
enum class RepeatType {
    NONE,       // 不重复
    DAILY,      // 每日
    WEEKLY,     // 每周
    MONTHLY,    // 每月
    YEARLY      // 每年
}