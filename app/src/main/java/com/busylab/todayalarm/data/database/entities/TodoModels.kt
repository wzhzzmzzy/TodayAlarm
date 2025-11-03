package com.busylab.todayalarm.data.database.entities

import kotlinx.serialization.Serializable
import java.util.Calendar

@Serializable
data class TodoTag(
    val id: String,
    val name: String,
    val color: String
)

@Serializable
data class TodoAttachment(
    val id: String,
    val name: String,
    val type: String, // image, audio, text
    val path: String,
    val size: Long,
    val createdAt: Long
)

@Serializable
data class TodoMetadata(
    val location: String? = null,
    val weather: String? = null,
    val mood: String? = null,
    val difficulty: Int? = null, // 1-5
    val estimatedDuration: Int? = null, // 分钟
    val actualDuration: Int? = null,
    val notes: String? = null
)

// 统计相关数据类
data class TodoStatistics(
    val totalCount: Int,
    val completedCount: Int,
    val pendingCount: Int,
    val overdueCount: Int,
    val completionRate: Float,
    val averageCompletionTime: Long,
    val categoryDistribution: Map<TodoCategory, Int>,
    val priorityDistribution: Map<TodoPriority, Int>
)

// 时间范围查询数据类
data class TodoTimeRange(
    val startTime: Long,
    val endTime: Long,
    val label: String
) {
    companion object {
        fun today(): TodoTimeRange {
            val now = Calendar.getInstance()
            val startOfDay = now.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfDay = now.apply {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            return TodoTimeRange(startOfDay, endOfDay, "今天")
        }

        fun thisWeek(): TodoTimeRange {
            val now = Calendar.getInstance()
            val startOfWeek = now.apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val endOfWeek = now.apply {
                add(Calendar.DAY_OF_WEEK, 6)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            return TodoTimeRange(startOfWeek, endOfWeek, "本周")
        }
    }
}

// 统计查询的数据类
data class CategoryCount(
    val category: String,
    val count: Int
)

data class PriorityCount(
    val priority: String,
    val count: Int
)