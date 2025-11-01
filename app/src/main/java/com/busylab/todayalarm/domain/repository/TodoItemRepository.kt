package com.busylab.todayalarm.domain.repository

import com.busylab.todayalarm.data.database.entities.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemRepository {
    fun getAllTodoItems(): Flow<List<TodoItem>>
    fun getPendingTodoItems(): Flow<List<TodoItem>>
    fun getCompletedTodoItems(): Flow<List<TodoItem>>
    fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>>
    suspend fun getTodoItemById(id: String): TodoItem?
    fun getTodoItemsInTimeRange(startTime: Long, endTime: Long): Flow<List<TodoItem>>
    suspend fun insertTodoItem(todoItem: TodoItem)
    suspend fun updateTodoItem(todoItem: TodoItem)
    suspend fun deleteTodoItem(todoItem: TodoItem)
    suspend fun updateTodoItemCompletionStatus(id: String, isCompleted: Boolean)
    suspend fun deleteTodoItemsByPlanId(planId: String)
}