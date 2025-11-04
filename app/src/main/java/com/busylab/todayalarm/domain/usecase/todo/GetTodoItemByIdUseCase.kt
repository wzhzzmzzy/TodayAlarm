package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.mapper.TodoMapper.toDomainModel
import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.domain.model.ModelMapper
import javax.inject.Inject

/**
 * 根据ID获取待办事项的用例
 */
class GetTodoItemByIdUseCase @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) {

    data class Params(
        val todoId: String
    )

    suspend operator fun invoke(params: Params): Result<TodoItem?> {
        return try {
            val todoEntity = todoItemRepository.getTodoItemById(params.todoId)
            val todoItem = todoEntity?.toDomainModel()
            Result.success(todoItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}