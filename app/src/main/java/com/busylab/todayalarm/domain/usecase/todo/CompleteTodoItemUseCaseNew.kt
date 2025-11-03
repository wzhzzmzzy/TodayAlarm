package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.database.entities.isCompleted
import com.busylab.todayalarm.data.database.entities.getMetadataObject
import com.busylab.todayalarm.data.database.entities.TodoMetadata
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import javax.inject.Inject

class CompleteTodoItemUseCaseNew @Inject constructor(
    private val todoRepository: TodoItemRepository
) {

    data class Params(
        val todoId: String,
        val completedAt: Long = System.currentTimeMillis(),
        val notes: String? = null
    )

    suspend operator fun invoke(params: Params): Result<Unit> {
        return try {
            // 获取待办事项
            val todoItem = todoRepository.getTodoItemById(params.todoId)
                ?: throw IllegalArgumentException("待办事项不存在")

            // 检查是否已完成
            if (todoItem.isCompleted()) {
                throw IllegalStateException("待办事项已完成")
            }

            // 更新完成状态
            todoRepository.completeTodoItem(params.todoId)

            // TODO: 取消相关通知
            // notificationManager.cancelNotification(todoItem)

            // TODO: 记录完成分析数据
            // analyticsManager.recordCompletion(todoItem, params.completedAt)

            // 如果有备注，更新元数据
            params.notes?.let { notes ->
                val metadata = todoItem.getMetadataObject().copy(notes = notes)
                val updatedTodo = todoItem.copy(
                    metadata = Json.encodeToString<TodoMetadata>(metadata),
                    updatedAt = System.currentTimeMillis()
                )
                todoRepository.updateTodoItem(updatedTodo)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}