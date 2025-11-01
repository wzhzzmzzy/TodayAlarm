package com.busylab.todayalarm.domain.repository

import com.busylab.todayalarm.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

/**
 * 待办事项仓库接口
 */
interface TodoRepository {
    /**
     * 获取所有待办事项
     */
    fun getAllTodoItems(): Flow<List<TodoItem>>

    /**
     * 获取指定日期的待办事项
     */
    fun getTodoItemsByDate(date: LocalDateTime): Flow<List<TodoItem>>

    /**
     * 根据计划ID获取待办事项
     */
    fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>>

    /**
     * 根据ID获取待办事项
     */
    suspend fun getTodoItemById(id: String): TodoItem?

    /**
     * 插入待办事项
     */
    suspend fun insertTodoItem(todoItem: TodoItem)

    /**
     * 更新待办事项
     */
    suspend fun updateTodoItem(todoItem: TodoItem)

    /**
     * 删除待办事项
     */
    suspend fun deleteTodoItem(todoItem: TodoItem)

    /**
     * 根据ID删除待办事项
     */
    suspend fun deleteTodoItemById(id: String)

    /**
     * 标记待办事项为已完成
     */
    suspend fun markTodoItemCompleted(id: String, completedAt: LocalDateTime)

    /**
     * 删除指定时间之前的已完成待办事项
     */
    suspend fun deleteCompletedTodosBefore(cutoffTime: LocalDateTime)

    /**
     * 获取未完成的待办事项
     */
    fun getIncompleteTodoItems(): Flow<List<TodoItem>>

    /**
     * 获取已完成的待办事项
     */
    fun getCompletedTodoItems(): Flow<List<TodoItem>>
}