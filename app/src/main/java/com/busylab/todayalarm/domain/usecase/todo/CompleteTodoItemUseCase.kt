package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.domain.repository.TodoItemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompleteTodoItemUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) {
    suspend operator fun invoke(todoItemId: String): Result<Unit> {
        return try {
            todoItemRepository.updateTodoItemCompletionStatus(todoItemId, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}