package com.busylab.todayalarm.domain.usecase.validation

import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

/**
 * 验证计划输入的用例
 */
class ValidatePlanInputUseCase @Inject constructor() {

    class ValidationException(val errors: Map<String, String>) : Exception("Input validation failed")

    operator fun invoke(
        title: String,
        content: String,
        triggerDateTime: LocalDateTime,
        isRepeating: Boolean,
        repeatInterval: Int
    ): Result<Unit> {
        val errors = mutableMapOf<String, String>()

        if (title.isBlank()) {
            errors["title"] = "计划标题不能为空"
        }

        if (isRepeating && repeatInterval <= 0) {
            errors["repeatInterval"] = "重复间隔必须大于0"
        }

        // 可以根据需要添加更多验证规则，例如：
        // if (content.length > 500) {
        //     errors["content"] = "内容不能超过500个字符"
        // }

        return if (errors.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(errors))
        }
    }
}
