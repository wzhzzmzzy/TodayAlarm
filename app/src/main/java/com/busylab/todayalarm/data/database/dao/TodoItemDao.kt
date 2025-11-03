package com.busylab.todayalarm.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.data.database.entities.CategoryCount
import com.busylab.todayalarm.data.database.entities.PriorityCount
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao {

    // ==================== 基础查询 ====================

    @Query("SELECT * FROM todo_items ORDER BY triggerTime DESC")
    fun getAllTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getTodoItemById(id: String): TodoItem?

    @Query("SELECT * FROM todo_items WHERE planId = :planId ORDER BY triggerTime DESC")
    fun getTodoItemsByPlanId(planId: String): Flow<List<TodoItem>>

    // ==================== 状态查询 ====================

    @Query("SELECT * FROM todo_items WHERE status = :status ORDER BY triggerTime ASC")
    fun getTodoItemsByStatus(status: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = 0 AND isArchived = 0 ORDER BY triggerTime ASC")
    fun getPendingTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedTodoItems(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE triggerTime < :currentTime AND isCompleted = 0 AND status != 'CANCELLED' ORDER BY triggerTime ASC")
    fun getOverdueTodoItems(currentTime: Long = System.currentTimeMillis()): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE status = 'SNOOZED' ORDER BY reminderTime ASC")
    fun getSnoozedTodoItems(): Flow<List<TodoItem>>

    // ==================== 时间范围查询 ====================

    @Query("SELECT * FROM todo_items WHERE triggerTime BETWEEN :startTime AND :endTime ORDER BY triggerTime ASC")
    fun getTodoItemsInTimeRange(startTime: Long, endTime: Long): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE DATE(triggerTime/1000, 'unixepoch', 'localtime') = DATE(:timestamp/1000, 'unixepoch', 'localtime') ORDER BY triggerTime ASC")
    fun getTodoItemsByDate(timestamp: Long): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE completedAt BETWEEN :startTime AND :endTime ORDER BY completedAt DESC")
    fun getCompletedTodoItemsInTimeRange(startTime: Long, endTime: Long): Flow<List<TodoItem>>

    // ==================== 分类和优先级查询 ====================

    @Query("SELECT * FROM todo_items WHERE category = :category ORDER BY priority DESC, triggerTime ASC")
    fun getTodoItemsByCategory(category: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE priority = :priority ORDER BY triggerTime ASC")
    fun getTodoItemsByPriority(priority: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE category = :category AND priority = :priority ORDER BY triggerTime ASC")
    fun getTodoItemsByCategoryAndPriority(category: String, priority: String): Flow<List<TodoItem>>

    // ==================== 搜索查询 ====================

    @Query("SELECT * FROM todo_items WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY triggerTime DESC")
    fun searchTodoItems(query: String): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE tags LIKE '%' || :tag || '%' ORDER BY triggerTime DESC")
    fun getTodoItemsByTag(tag: String): Flow<List<TodoItem>>

    // ==================== 统计查询 ====================

    @Query("SELECT COUNT(*) FROM todo_items")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    suspend fun getCompletedCount(): Int

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 0 AND isArchived = 0")
    suspend fun getPendingCount(): Int

    @Query("SELECT COUNT(*) FROM todo_items WHERE triggerTime < :currentTime AND isCompleted = 0 AND status != 'CANCELLED'")
    suspend fun getOverdueCount(currentTime: Long = System.currentTimeMillis()): Int

    @Query("SELECT category, COUNT(*) as count FROM todo_items GROUP BY category")
    suspend fun getCategoryDistribution(): List<CategoryCount>

    @Query("SELECT priority, COUNT(*) as count FROM todo_items GROUP BY priority")
    suspend fun getPriorityDistribution(): List<PriorityCount>

    @Query("SELECT AVG(completedAt - triggerTime) FROM todo_items WHERE isCompleted = 1 AND completedAt > triggerTime")
    suspend fun getAverageCompletionTime(): Long?

    // ==================== 数据操作 ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItem(todoItem: TodoItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodoItems(todoItems: List<TodoItem>)

    @Update
    suspend fun updateTodoItem(todoItem: TodoItem)

    @Update
    suspend fun updateTodoItems(todoItems: List<TodoItem>)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodoItems(todoItems: List<TodoItem>)

    // ==================== 便捷更新方法 ====================

    @Query("UPDATE todo_items SET isCompleted = :isCompleted, completedAt = :completedAt, status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoItemCompletionStatus(
        id: String,
        isCompleted: Boolean,
        completedAt: Long?,
        status: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE todo_items SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoItemStatus(
        id: String,
        status: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE todo_items SET priority = :priority, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoItemPriority(
        id: String,
        priority: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE todo_items SET category = :category, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTodoItemCategory(
        id: String,
        category: String,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE todo_items SET reminderTime = :reminderTime, snoozeCount = snoozeCount + 1, status = 'SNOOZED', updatedAt = :updatedAt WHERE id = :id")
    suspend fun snoozeTodoItem(
        id: String,
        reminderTime: Long,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE todo_items SET isArchived = :isArchived, updatedAt = :updatedAt WHERE id = :id")
    suspend fun archiveTodoItem(
        id: String,
        isArchived: Boolean,
        updatedAt: Long = System.currentTimeMillis()
    )

    // ==================== 批量操作 ====================

    @Query("UPDATE todo_items SET isCompleted = 1, completedAt = :completedAt, status = 'COMPLETED', updatedAt = :updatedAt WHERE id IN (:ids)")
    suspend fun batchCompleteTodoItems(
        ids: List<String>,
        completedAt: Long = System.currentTimeMillis(),
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("UPDATE todo_items SET isArchived = 1, updatedAt = :updatedAt WHERE id IN (:ids)")
    suspend fun batchArchiveTodoItems(
        ids: List<String>,
        updatedAt: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM todo_items WHERE id IN (:ids)")
    suspend fun batchDeleteTodoItems(ids: List<String>)

    @Query("DELETE FROM todo_items WHERE planId = :planId")
    suspend fun deleteTodoItemsByPlanId(planId: String)

    @Query("DELETE FROM todo_items WHERE isCompleted = 1 AND completedAt < :beforeTime")
    suspend fun deleteOldCompletedTodoItems(beforeTime: Long)

    // ==================== 清理操作 ====================

    @Query("DELETE FROM todo_items WHERE isArchived = 1")
    suspend fun deleteArchivedTodoItems()

    @Query("DELETE FROM todo_items WHERE status = 'CANCELLED'")
    suspend fun deleteCancelledTodoItems()

    @Query("UPDATE todo_items SET status = 'OVERDUE' WHERE triggerTime < :currentTime AND isCompleted = 0 AND status = 'PENDING'")
    suspend fun updateOverdueTodoItems(currentTime: Long = System.currentTimeMillis())
}