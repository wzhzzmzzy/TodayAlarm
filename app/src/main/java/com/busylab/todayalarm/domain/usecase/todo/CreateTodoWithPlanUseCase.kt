package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.repository.TodoRepository
import com.busylab.todayalarm.domain.usecase.plan.CreatePlanFromTodoUseCase
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject

class CreateTodoWithPlanUseCase @Inject constructor(
    private val todoRepository: TodoRepository,
    private val createPlanFromTodoUseCase: CreatePlanFromTodoUseCase
) {

    data class Params(
        val title: String,
        val content: String,
        val triggerTime: kotlinx.datetime.LocalDateTime,
        val enableRepeating: Boolean = false,
        val repeatType: RepeatType = RepeatType.NONE,
        val repeatInterval: Int = 1
    )

    suspend operator fun invoke(params: Params): Result<TodoItem> {
        return try {
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            // 创建 TodoItem
            val todoItem = TodoItem(
                id = UUID.randomUUID().toString(),
                planId = null, // 初始为 null
                title = params.title,
                content = params.content,
                isCompleted = false,
                triggerTime = params.triggerTime,
                completedAt = null,
                createdAt = currentTime,
                enableRepeating = params.enableRepeating,
                repeatType = params.repeatType,
                repeatInterval = params.repeatInterval,
                isActive = true
            )

            // 保存 TodoItem
            todoRepository.insertTodoItem(todoItem)

            // 如果启用重复，创建对应的 Plan
            if (params.enableRepeating) {
                createPlanFromTodoUseCase(CreatePlanFromTodoUseCase.Params(todoItem))
                    .onSuccess { plan ->
                        // 更新 TodoItem 的 planId
                        val updatedTodoItem = todoItem.copy(planId = plan.id)
                        todoRepository.updateTodoItem(updatedTodoItem)
                    }
                    .onFailure { e ->
                        // 记录错误但不阻止 TodoItem 创建
                        // 可以在 UI 层显示警告
                        e.printStackTrace()
                    }
            }

            Result.success(todoItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}