package com.busylab.todayalarm.domain.usecase.validation

import com.busylab.todayalarm.domain.model.RepeatType
import kotlinx.datetime.*

object InputValidator {

    fun validatePlanTitle(title: String): ValidationResult {
        return when {
            title.isBlank() -> ValidationResult.Error("标题不能为空")
            title.length > 100 -> ValidationResult.Error("标题不能超过100个字符")
            else -> ValidationResult.Success
        }
    }

    fun validatePlanContent(content: String): ValidationResult {
        return when {
            content.isBlank() -> ValidationResult.Error("内容不能为空")
            content.length > 500 -> ValidationResult.Error("内容不能超过500个字符")
            else -> ValidationResult.Success
        }
    }

    fun validateTriggerTime(triggerTime: LocalDateTime): ValidationResult {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val futureLimit = now.toInstant(TimeZone.currentSystemDefault())
            .plus(10, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return when {
            triggerTime <= now -> ValidationResult.Error("提醒时间必须在未来")
            triggerTime > futureLimit -> ValidationResult.Error("提醒时间不能超过10年")
            else -> ValidationResult.Success
        }
    }

    fun validateRepeatInterval(interval: Int, repeatType: RepeatType): ValidationResult {
        return when {
            interval <= 0 -> ValidationResult.Error("重复间隔必须大于0")
            repeatType == RepeatType.DAILY && interval > 365 -> ValidationResult.Error("每日重复间隔不能超过365天")
            repeatType == RepeatType.WEEKLY && interval > 52 -> ValidationResult.Error("每周重复间隔不能超过52周")
            repeatType == RepeatType.MONTHLY && interval > 12 -> ValidationResult.Error("每月重复间隔不能超过12个月")
            repeatType == RepeatType.YEARLY && interval > 10 -> ValidationResult.Error("每年重复间隔不能超过10年")
            else -> ValidationResult.Success
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }
}