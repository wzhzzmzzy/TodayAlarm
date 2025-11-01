package com.busylab.todayalarm.data.repository

import com.busylab.todayalarm.data.database.dao.TodoItemDao
import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodoItemRepositoryImpl @Inject constructor(
    private val todoItemDao: TodoItemDao
) : TodoItemRepository {

    override fun getAllTodoItems(): Flow<List<TodoItem>> = todoItemDao.getAllTodoItems()

    override fun getPendingTodoItems(): Flow<List<TodoItem>> = todoItemDao.getPendingTodoItems()

    override fun getCompletedTodoItems(): Flow<List<TodoItem>> = todoItemDao.getCompletedTodoItems()

    override fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsByPlanId(planId)

    override suspend fun getTodoItemById(id: String): TodoItem? = todoItemDao.getTodoItemById(id)

    override fun getTodoItemsInTimeRange(startTime: Long, endTime: Long): Flow<List<TodoItem>> =
        todoItemDao.getTodoItemsInTimeRange(startTime, endTime)

    override suspend fun insertTodoItem(todoItem: TodoItem) = todoItemDao.insertTodoItem(todoItem)

    override suspend fun updateTodoItem(todoItem: TodoItem) = todoItemDao.updateTodoItem(todoItem)

    override suspend fun deleteTodoItem(todoItem: TodoItem) = todoItemDao.deleteTodoItem(todoItem)

    override suspend fun updateTodoItemCompletionStatus(id: String, isCompleted: Boolean) =
        todoItemDao.updateTodoItemCompletionStatus(id, isCompleted)

    override suspend fun deleteTodoItemsByPlanId(planId: String) =
        todoItemDao.deleteTodoItemsByPlanId(planId)
}