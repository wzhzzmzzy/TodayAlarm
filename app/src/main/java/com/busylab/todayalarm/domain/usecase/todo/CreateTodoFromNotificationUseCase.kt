package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.repository.TodoRepository
import com.busylab.todayalarm.domain.repository.PlanRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject

class CreateTodoFromNotificationUseCase @Inject constructor(
    private val todoRepository: TodoRepository,
    private val planRepository: PlanRepository
) {

    data class Params(
        val planId: String
    )

    suspend operator fun invoke(params: Params): Result<TodoItem> {
        return try {
            val plan = planRepository.getPlanById(params.planId)
                ?: return Result.failure(IllegalArgumentException("计划不存在"))

            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            // 检查是否已存在今天的待办
            val existingTodos = todoRepository.getTodoItemsByPlanId(params.planId).first()
            val todayTodo = existingTodos.find {
                it.triggerTime.date == currentTime.date
            }

            if (todayTodo != null) {
                return Result.success(todayTodo)
            }

            // 创建新的待办
            val todoItem = TodoItem(
                id = UUID.randomUUID().toString(),
                planId = plan.id,
                title = plan.title,
                content = plan.content,
                isCompleted = false,
                triggerTime = currentTime,
                completedAt = null,
                createdAt = currentTime
            )

            todoRepository.insertTodoItem(todoItem)
            Result.success(todoItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}