package com.busylab.todayalarm.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.busylab.todayalarm.data.database.entities.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    @Query("SELECT * FROM todo_items ORDER BY triggerTime DESC")
    fun getAllTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = 0 ORDER BY triggerTime ASC")
    fun getPendingTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE planId = :planId ORDER BY triggerTime DESC")
    fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getTodoItemById(id: String): TodoItem?

    @Query("SELECT * FROM todo_items WHERE triggerTime BETWEEN :startTime AND :endTime")
    fun getTodoItemsInTimeRange(startTime: Long, endTime: Long): Flow<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItem)

    @Update
    suspend fun updateTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Query("UPDATE todo_items SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun updateTodoItemCompletionStatus(
        id: String,
        isCompleted: Boolean,
        completedAt: Long? = if (isCompleted) System.currentTimeMillis() else null
    )

    @Query("DELETE FROM todo_items WHERE planId = :planId")
    suspend fun deleteTodoItemsByPlanId(planId: String)
}