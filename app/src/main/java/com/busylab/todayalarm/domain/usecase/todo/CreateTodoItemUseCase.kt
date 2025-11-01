package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import kotlinx.datetime.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateTodoItemUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) {
    suspend operator fun invoke(
        planId: String,
        title: String,
        content: String,
        triggerTime: LocalDateTime
    ): Result<String> {
        return try {
            val todoItem = TodoItem(
                planId = planId,
                title = title,
                content = content,
                triggerTime = triggerTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            )

            todoItemRepository.insertTodoItem(todoItem)
            Result.success(todoItem.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}