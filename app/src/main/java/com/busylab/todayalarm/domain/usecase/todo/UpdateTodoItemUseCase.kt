package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.mapper.TodoMapper.toDomainModel
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.domain.model.ModelMapper
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject

/**
 * 更新待办事项的用例
 */
class UpdateTodoItemUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) {

    data class Params(
        val todoId: String,
        val title: String,
        val content: String,
        val triggerTime: LocalDateTime,
        val enableRepeating: Boolean,
        val repeatType: RepeatType,
        val repeatInterval: Int
    )

    suspend operator fun invoke(params: Params): Result<TodoItem> {
        return try {
            // 获取现有的 TodoItem
            val existingTodo = todoItemRepository.getTodoItemById(params.todoId)
                ?: return Result.failure(Exception("Todo item not found"))

            // 更新字段
            val updatedTodo = existingTodo.copy(
                title = params.title,
                content = params.content,
                triggerTime = params.triggerTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds(),
                enableRepeating = params.enableRepeating,
                repeatType = params.repeatType.name,
                repeatInterval = params.repeatInterval,
                updatedAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
            )

            // 保存更新
            todoItemRepository.updateTodoItem(updatedTodo)

            // 返回领域模型
            val domainTodoItem = updatedTodo.toDomainModel()
            Result.success(domainTodoItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}