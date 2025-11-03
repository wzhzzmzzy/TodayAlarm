package com.busylab.todayalarm.data.repository

import com.busylab.todayalarm.data.database.dao.TodoItemDao
import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.data.database.entities.TodoStatus
import com.busylab.todayalarm.data.database.entities.TodoPriority
import com.busylab.todayalarm.data.database.entities.TodoCategory
import com.busylab.todayalarm.data.database.entities.TodoStatistics
import com.busylab.todayalarm.data.database.entities.TodoTimeRange
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoItemRepositoryImpl @Inject constructor(
    private val todoItemDao: TodoItemDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : TodoItemRepository {

    // ==================== 基础操作 ====================

    override fun getAllTodoItems(): Flow<List<TodoItem>> = todoItemDao.getAllTodoItems()

    override suspend fun getTodoItemById(id: String): TodoItem? =
        withContext(dispatcher) {
            todoItemDao.getTodoItemById(id)
        }

    override fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsByPlanId(planId)

    override suspend fun insertTodoItem(todoItem: TodoItem) =
        withContext(dispatcher) {
            todoItemDao.insertTodoItem(todoItem)
        }

    override suspend fun updateTodoItem(todoItem: TodoItem) =
        withContext(dispatcher) {
            todoItemDao.updateTodoItem(todoItem.copy(updatedAt = System.currentTimeMillis()))
        }

    override suspend fun deleteTodoItem(todoItem: TodoItem) =
        withContext(dispatcher) {
            todoItemDao.deleteTodoItem(todoItem)
        }

    // ==================== 状态管理 ====================

    override fun getTodoItemsByStatus(status: TodoStatus): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsByStatus(status.name)

    override fun getPendingTodoItems(): Flow<List<TodoItem>> =
        todoItemDao.getPendingTodoItems()

    override fun getCompletedTodoItems(): Flow<List<TodoItem>> =
        todoItemDao.getCompletedTodoItems()

    override fun getOverdueTodoItems(): Flow<List<TodoItem>> =
        todoItemDao.getOverdueTodoItems()

    override fun getSnoozedTodoItems(): Flow<List<TodoItem>> =
        todoItemDao.getSnoozedTodoItems()

    // ==================== 时间查询 ====================

    override fun getTodoItemsInTimeRange(startTime: Long, endTime: Long): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsInTimeRange(startTime, endTime)

    override fun getTodoItemsByDate(timestamp: Long): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsByDate(timestamp)

    override fun getTodayTodoItems(): Flow<List<TodoItem>> =
        TodoTimeRange.today().let { range ->
            todoItemDao.getTodoItemsInTimeRange(range.startTime, range.endTime)
        }

    override fun getThisWeekTodoItems(): Flow<List<TodoItem>> =
        TodoTimeRange.thisWeek().let { range ->
            todoItemDao.getTodoItemsInTimeRange(range.startTime, range.endTime)
        }

    // ==================== 分类查询 ====================

    override fun getTodoItemsByCategory(category: TodoCategory): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsByCategory(category.name)

    override fun getTodoItemsByPriority(priority: TodoPriority): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsByPriority(priority.name)

    override fun searchTodoItems(query: String): Flow<List<TodoItem>> =
        todoItemDao.searchTodoItems(query)

    // ==================== 状态更新 ====================

    override suspend fun completeTodoItem(id: String) =
        withContext(dispatcher) {
            val now = System.currentTimeMillis()
            todoItemDao.updateTodoItemCompletionStatus(
                id = id,
                isCompleted = true,
                completedAt = now,
                status = TodoStatus.COMPLETED.name,
                updatedAt = now
            )
        }

    override suspend fun uncompleteTodoItem(id: String) =
        withContext(dispatcher) {
            todoItemDao.updateTodoItemCompletionStatus(
                id = id,
                isCompleted = false,
                completedAt = null,
                status = TodoStatus.PENDING.name
            )
        }

    override suspend fun cancelTodoItem(id: String) =
        withContext(dispatcher) {
            todoItemDao.updateTodoItemStatus(id, TodoStatus.CANCELLED.name)
        }

    override suspend fun snoozeTodoItem(id: String, snoozeMinutes: Int) =
        withContext(dispatcher) {
            val snoozeTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000)
            todoItemDao.snoozeTodoItem(id, snoozeTime)
        }

    override suspend fun updateTodoItemPriority(id: String, priority: TodoPriority) =
        withContext(dispatcher) {
            todoItemDao.updateTodoItemPriority(id, priority.name)
        }

    override suspend fun updateTodoItemCategory(id: String, category: TodoCategory) =
        withContext(dispatcher) {
            todoItemDao.updateTodoItemCategory(id, category.name)
        }

    override suspend fun archiveTodoItem(id: String) =
        withContext(dispatcher) {
            todoItemDao.archiveTodoItem(id, true)
        }

    override suspend fun updateTodoItemCompletionStatus(id: String, isCompleted: Boolean) =
        withContext(dispatcher) {
            val now = System.currentTimeMillis()
            todoItemDao.updateTodoItemCompletionStatus(
                id = id,
                isCompleted = isCompleted,
                completedAt = if (isCompleted) now else null,
                status = if (isCompleted) TodoStatus.COMPLETED.name else TodoStatus.PENDING.name
            )
        }

    // ==================== 批量操作 ====================

    override suspend fun batchCompleteTodoItems(ids: List<String>) =
        withContext(dispatcher) {
            todoItemDao.batchCompleteTodoItems(ids)
        }

    override suspend fun batchDeleteTodoItems(ids: List<String>) =
        withContext(dispatcher) {
            todoItemDao.batchDeleteTodoItems(ids)
        }

    override suspend fun batchArchiveTodoItems(ids: List<String>) =
        withContext(dispatcher) {
            todoItemDao.batchArchiveTodoItems(ids)
        }

    override suspend fun deleteTodoItemsByPlanId(planId: String) =
        withContext(dispatcher) {
            todoItemDao.deleteTodoItemsByPlanId(planId)
        }

    // ==================== 统计功能 ====================

    override suspend fun getTodoStatistics(): TodoStatistics =
        withContext(dispatcher) {
            val totalCount = todoItemDao.getTotalCount()
            val completedCount = todoItemDao.getCompletedCount()
            val pendingCount = todoItemDao.getPendingCount()
            val overdueCount = todoItemDao.getOverdueCount()
            val categoryDistribution = todoItemDao.getCategoryDistribution()
                .associate { TodoCategory.valueOf(it.category) to it.count }
            val priorityDistribution = todoItemDao.getPriorityDistribution()
                .associate { TodoPriority.valueOf(it.priority) to it.count }
            val averageCompletionTime = todoItemDao.getAverageCompletionTime() ?: 0L

            TodoStatistics(
                totalCount = totalCount,
                completedCount = completedCount,
                pendingCount = pendingCount,
                overdueCount = overdueCount,
                completionRate = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f,
                averageCompletionTime = averageCompletionTime,
                categoryDistribution = categoryDistribution,
                priorityDistribution = priorityDistribution
            )
        }

    override suspend fun getTodoStatisticsInTimeRange(startTime: Long, endTime: Long): TodoStatistics =
        withContext(dispatcher) {
            // 实现时间范围内的统计查询
            // 这里需要添加相应的DAO方法
            getTodoStatistics() // 简化实现，实际应该基于时间范围查询
        }

    // ==================== 清理功能 ====================

    override suspend fun cleanupOldCompletedTodos(daysOld: Int) =
        withContext(dispatcher) {
            val cutoffTime = System.currentTimeMillis() - (daysOld * 24 * 60 * 60 * 1000L)
            todoItemDao.deleteOldCompletedTodoItems(cutoffTime)
        }

    override suspend fun updateOverdueTodos() =
        withContext(dispatcher) {
            todoItemDao.updateOverdueTodoItems()
        }
}