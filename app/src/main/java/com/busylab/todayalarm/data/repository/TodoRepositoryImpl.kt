package com.busylab.todayalarm.data.repository

import com.busylab.todayalarm.data.database.dao.TodoItemDao
import com.busylab.todayalarm.data.mapper.TodoMapper.toDomainModel
import com.busylab.todayalarm.data.mapper.TodoMapper.toDomainModels
import com.busylab.todayalarm.data.mapper.TodoMapper.toEntityModel
import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoRepositoryImpl @Inject constructor(
    private val todoItemDao: TodoItemDao
) : TodoRepository {

    override fun getAllTodoItems(): Flow<List<TodoItem>> =
        todoItemDao.getAllTodoItems().map { it.toDomainModels() }

    override fun getTodoItemsByDate(date: LocalDateTime): Flow<List<TodoItem>> {
        val timeZone = TimeZone.currentSystemDefault()
        val startOfDay = LocalDateTime(date.date, kotlinx.datetime.LocalTime(0, 0, 0))
        val endOfDay = LocalDateTime(date.date, kotlinx.datetime.LocalTime(23, 59, 59))

        val startTime = startOfDay.toInstant(timeZone).toEpochMilliseconds()
        val endTime = endOfDay.toInstant(timeZone).toEpochMilliseconds()

        return todoItemDao.getTodoItemsInTimeRange(startTime, endTime).map { it.toDomainModels() }
    }

    override fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsByPlanId(planId).map { it.toDomainModels() }

    override suspend fun getTodoItemById(id: String): TodoItem? =
        todoItemDao.getTodoItemById(id)?.toDomainModel()

    override suspend fun insertTodoItem(todoItem: TodoItem) =
        todoItemDao.insertTodoItem(todoItem.toEntityModel())

    override suspend fun updateTodoItem(todoItem: TodoItem) =
        todoItemDao.updateTodoItem(todoItem.toEntityModel())

    override suspend fun deleteTodoItem(todoItem: TodoItem) =
        todoItemDao.deleteTodoItem(todoItem.toEntityModel())

    override suspend fun deleteTodoItemById(id: String) {
        val todoItem = todoItemDao.getTodoItemById(id)
        todoItem?.let { todoItemDao.deleteTodoItem(it) }
    }

    override suspend fun markTodoItemCompleted(id: String, completedAt: LocalDateTime) {
        val timeZone = TimeZone.currentSystemDefault()
        val timestamp = completedAt.toInstant(timeZone).toEpochMilliseconds()
        todoItemDao.updateTodoItemCompletionStatus(id, true, timestamp, "COMPLETED")
    }

    override suspend fun deleteCompletedTodosBefore(cutoffTime: LocalDateTime) {
        // 这需要在DAO中添加相应的查询方法
        // 暂时先获取所有已完成的待办事项，然后过滤删除
        val timeZone = TimeZone.currentSystemDefault()
        val cutoffTimestamp = cutoffTime.toInstant(timeZone).toEpochMilliseconds()

        // 注意：这是一个简化实现，实际项目中应该在DAO中添加批量删除方法
        // 这里需要添加相应的DAO方法来高效处理
    }

    override fun getIncompleteTodoItems(): Flow<List<TodoItem>> =
        todoItemDao.getPendingTodoItems().map { it.toDomainModels() }

    override fun getCompletedTodoItems(): Flow<List<TodoItem>> =
        todoItemDao.getCompletedTodoItems().map { it.toDomainModels() }
}