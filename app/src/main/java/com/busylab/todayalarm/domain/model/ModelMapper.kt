package com.busylab.todayalarm.domain.model

import com.busylab.todayalarm.data.datastore.UserPreferences
import kotlinx.datetime.*

object ModelMapper {

    fun Plan.toUiModel(userPreferences: UserPreferences): PlanUiModel {
        val repeatRule = RepeatRule(
            type = repeatType,
            interval = repeatInterval
        )

        return PlanUiModel(
            id = id,
            title = title,
            content = content,
            triggerTime = triggerTime,
            isRepeating = isRepeating,
            repeatType = repeatType,
            repeatInterval = repeatInterval,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt,
            nextTriggerTime = if (isRepeating) repeatRule.getNextTriggerTime(triggerTime) else null,
            formattedTime = formatTime(triggerTime, userPreferences.timeFormat24Hour),
            formattedDate = formatDate(triggerTime),
            isOverdue = triggerTime < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    fun PlanUiModel.toDomainModel(): Plan {
        return Plan(
            id = id,
            title = title,
            content = content,
            triggerTime = triggerTime,
            isRepeating = isRepeating,
            repeatType = repeatType,
            repeatInterval = repeatInterval,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun TodoItem.toUiModel(userPreferences: UserPreferences): TodoItemUiModel {
        return TodoItemUiModel(
            id = id,
            planId = planId,
            title = title,
            content = content,
            isCompleted = isCompleted,
            triggerTime = triggerTime,
            completedAt = completedAt,
            createdAt = createdAt,
            formattedTime = formatTime(triggerTime, userPreferences.timeFormat24Hour),
            formattedDate = formatDate(triggerTime),
            timeAgo = calculateTimeAgo(triggerTime),
            isOverdue = !isCompleted && triggerTime < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    }

    fun TodoItemUiModel.toDomainModel(): TodoItem {
        return TodoItem(
            id = id,
            planId = planId,
            title = title,
            content = content,
            isCompleted = isCompleted,
            triggerTime = triggerTime,
            completedAt = completedAt,
            createdAt = createdAt
        )
    }

    private fun formatTime(dateTime: LocalDateTime, is24Hour: Boolean): String {
        return if (is24Hour) {
            "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
        } else {
            val hour = if (dateTime.hour == 0) 12 else if (dateTime.hour > 12) dateTime.hour - 12 else dateTime.hour
            val amPm = if (dateTime.hour < 12) "AM" else "PM"
            "${hour}:${dateTime.minute.toString().padStart(2, '0')} $amPm"
        }
    }

    private fun formatDate(dateTime: LocalDateTime): String {
        return "${dateTime.year}年${dateTime.monthNumber}月${dateTime.dayOfMonth}日"
    }

    private fun calculateTimeAgo(dateTime: LocalDateTime): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val duration = now.toInstant(TimeZone.currentSystemDefault()) -
                      dateTime.toInstant(TimeZone.currentSystemDefault())

        return when {
            duration.inWholeMinutes < 1 -> "刚刚"
            duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes}分钟前"
            duration.inWholeHours < 24 -> "${duration.inWholeHours}小时前"
            duration.inWholeDays < 7 -> "${duration.inWholeDays}天前"
            duration.inWholeDays < 30 -> "${duration.inWholeDays / 7}周前"
            duration.inWholeDays < 365 -> "${duration.inWholeDays / 30}个月前"
            else -> "${duration.inWholeDays / 365}年前"
        }
    }
}