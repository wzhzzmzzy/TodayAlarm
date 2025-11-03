package com.busylab.todayalarm.domain.repository

import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.data.database.entities.TodoStatus
import com.busylab.todayalarm.data.database.entities.TodoPriority
import com.busylab.todayalarm.data.database.entities.TodoCategory
import com.busylab.todayalarm.data.database.entities.TodoStatistics
import kotlinx.coroutines.flow.Flow

interface TodoItemRepository {

    // ==================== 基础操作 ====================
    fun getAllTodoItems(): Flow<List<TodoItem>>
    suspend fun getTodoItemById(id: String): TodoItem?
    fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>>
    suspend fun insertTodoItem(todoItem: TodoItem)
    suspend fun updateTodoItem(todoItem: TodoItem)
    suspend fun deleteTodoItem(todoItem: TodoItem)

    // ==================== 状态管理 ====================
    fun getTodoItemsByStatus(status: TodoStatus): Flow<List<TodoItem>>
    fun getPendingTodoItems(): Flow<List<TodoItem>>
    fun getCompletedTodoItems(): Flow<List<TodoItem>>
    fun getOverdueTodoItems(): Flow<List<TodoItem>>
    fun getSnoozedTodoItems(): Flow<List<TodoItem>>

    // ==================== 时间查询 ====================
    fun getTodoItemsInTimeRange(startTime: Long, endTime: Long): Flow<List<TodoItem>>
    fun getTodoItemsByDate(timestamp: Long): Flow<List<TodoItem>>
    fun getTodayTodoItems(): Flow<List<TodoItem>>
    fun getThisWeekTodoItems(): Flow<List<TodoItem>>

    // ==================== 分类查询 ====================
    fun getTodoItemsByCategory(category: TodoCategory): Flow<List<TodoItem>>
    fun getTodoItemsByPriority(priority: TodoPriority): Flow<List<TodoItem>>
    fun searchTodoItems(query: String): Flow<List<TodoItem>>

    // ==================== 状态更新 ====================
    suspend fun completeTodoItem(id: String)
    suspend fun uncompleteTodoItem(id: String)
    suspend fun cancelTodoItem(id: String)
    suspend fun snoozeTodoItem(id: String, snoozeMinutes: Int)
    suspend fun updateTodoItemPriority(id: String, priority: TodoPriority)
    suspend fun updateTodoItemCategory(id: String, category: TodoCategory)
    suspend fun archiveTodoItem(id: String)
    suspend fun updateTodoItemCompletionStatus(id: String, isCompleted: Boolean)

    // ==================== 批量操作 ====================
    suspend fun batchCompleteTodoItems(ids: List<String>)
    suspend fun batchDeleteTodoItems(ids: List<String>)
    suspend fun batchArchiveTodoItems(ids: List<String>)
    suspend fun deleteTodoItemsByPlanId(planId: String)

    // ==================== 统计功能 ====================
    suspend fun getTodoStatistics(): TodoStatistics
    suspend fun getTodoStatisticsInTimeRange(startTime: Long, endTime: Long): TodoStatistics

    // ==================== 清理功能 ====================
    suspend fun cleanupOldCompletedTodos(daysOld: Int = 30)
    suspend fun updateOverdueTodos()
}