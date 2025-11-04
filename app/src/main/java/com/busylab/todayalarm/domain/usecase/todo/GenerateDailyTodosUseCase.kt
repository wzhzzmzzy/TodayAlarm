package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*
import java.util.UUID
import javax.inject.Inject

class GenerateDailyTodosUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val todoRepository: TodoItemRepository
) {

    data class Params(
        val targetDate: LocalDate? = null // 可选，默认今天
    )

    suspend operator fun invoke(params: Params): Result<GenerateResult> {
        var lastException: Exception? = null

        // 重试3次
        for (attempt in 1..3) {
            try {
                val timeZone = TimeZone.currentSystemDefault()
                val targetDate = params.targetDate ?: Clock.System.now().toLocalDateTime(timeZone).date

                // 计算目标日期的时间范围
                val startOfDay = targetDate.atStartOfDayIn(timeZone)
                val endOfDay = targetDate.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone)

                // 获取目标日期内需要触发的所有重复计划
                val plans = planRepository.getPlansInTimeRange(
                    startOfDay.toEpochMilliseconds(),
                    endOfDay.toEpochMilliseconds()
                ).first()

                var createdCount = 0
                var skippedCount = 0

                plans.forEach { plan ->
                    if (plan.isRepeating && plan.isActive) {
                        // 检查是否已创建当天的待办
                        val existingTodos = todoRepository.getTodoItemsByPlanId(plan.id).first()
                        val todayTodo = existingTodos.find {
                            val todoDate = Instant.fromEpochMilliseconds(it.triggerTime)
                                .toLocalDateTime(timeZone).date
                            todoDate == targetDate
                        }

                        if (todayTodo == null) {
                            // 创建当天的待办
                            val todoItem = TodoItem(
                                id = UUID.randomUUID().toString(),
                                planId = plan.id,
                                title = plan.title,
                                content = plan.content,
                                isCompleted = false,
                                triggerTime = plan.triggerTime.toInstant(timeZone).toEpochMilliseconds(),
                                completedAt = null,
                                createdAt = Clock.System.now().toEpochMilliseconds(),
                                enableRepeating = false, // 生成的待办不启用重复
                                repeatType = plan.repeatType.name,
                                repeatInterval = plan.repeatInterval,
                                isActive = true
                            )
                            todoRepository.insertTodoItem(todoItem)
                            createdCount++
                        } else {
                            skippedCount++
                        }
                    }
                }

                return Result.success(GenerateResult(createdCount, skippedCount))
            } catch (e: Exception) {
                lastException = e
                if (attempt < 3) {
                    // 等待后重试
                    kotlinx.coroutines.delay(1000L * attempt)
                }
            }
        }

        return Result.failure(lastException ?: Exception("生成待办失败"))
    }
}

data class GenerateResult(
    val createdCount: Int,
    val skippedCount: Int
)