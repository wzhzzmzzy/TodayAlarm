package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.data.database.entities.TodoCategory
import com.busylab.todayalarm.data.database.entities.TodoPriority
import com.busylab.todayalarm.data.database.entities.TodoTimeRange
import com.busylab.todayalarm.data.database.entities.getPriorityEnum
import com.busylab.todayalarm.data.mapper.TodoMapper.toDomainModels
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTodoItemsUseCase @Inject constructor(
    private val todoRepository: TodoItemRepository
) {

    data class Params(
        val filter: TodoFilter = TodoFilter.ALL,
        val sortBy: TodoSortBy = TodoSortBy.TIME_ASC,
        val timeRange: TodoTimeRange? = null,
        val category: TodoCategory? = null,
        val priority: TodoPriority? = null,
        val searchQuery: String? = null
    )

    operator fun invoke(params: Params): Flow<List<com.busylab.todayalarm.domain.model.TodoItem>> {
        return when (params.filter) {
            TodoFilter.ALL -> todoRepository.getAllTodoItems()
            TodoFilter.PENDING -> todoRepository.getPendingTodoItems()
            TodoFilter.COMPLETED -> todoRepository.getCompletedTodoItems()
            TodoFilter.OVERDUE -> todoRepository.getOverdueTodoItems()
            TodoFilter.TODAY -> todoRepository.getTodayTodoItems()
            TodoFilter.THIS_WEEK -> todoRepository.getThisWeekTodoItems()
            TodoFilter.SNOOZED -> todoRepository.getSnoozedTodoItems()
            TodoFilter.BY_CATEGORY -> params.category?.let {
                todoRepository.getTodoItemsByCategory(it)
            } ?: todoRepository.getAllTodoItems()
            TodoFilter.BY_PRIORITY -> params.priority?.let {
                todoRepository.getTodoItemsByPriority(it)
            } ?: todoRepository.getAllTodoItems()
            TodoFilter.SEARCH -> params.searchQuery?.let {
                todoRepository.searchTodoItems(it)
            } ?: todoRepository.getAllTodoItems()
            TodoFilter.TIME_RANGE -> params.timeRange?.let {
                todoRepository.getTodoItemsInTimeRange(it.startTime, it.endTime)
            } ?: todoRepository.getAllTodoItems()
        }.map { todoItems ->
            // 应用排序
            when (params.sortBy) {
                TodoSortBy.TIME_ASC -> todoItems.sortedBy { it.triggerTime }
                TodoSortBy.TIME_DESC -> todoItems.sortedByDescending { it.triggerTime }
                TodoSortBy.PRIORITY_DESC -> todoItems.sortedWith(
                    compareByDescending<TodoItem> { it.getPriorityEnum().ordinal }
                        .thenBy { it.triggerTime }
                )
                TodoSortBy.STATUS -> todoItems.sortedWith(
                    compareBy<TodoItem> { it.isCompleted }
                        .thenBy { it.triggerTime }
                )
                TodoSortBy.CREATED_DESC -> todoItems.sortedByDescending { it.createdAt }
                TodoSortBy.UPDATED_DESC -> todoItems.sortedByDescending { it.updatedAt }
            }
        }.map {
            it.toDomainModels()
        }
    }
}

enum class TodoFilter {
    ALL, PENDING, COMPLETED, OVERDUE, TODAY, THIS_WEEK, SNOOZED,
    BY_CATEGORY, BY_PRIORITY, SEARCH, TIME_RANGE
}

enum class TodoSortBy {
    TIME_ASC, TIME_DESC, PRIORITY_DESC, STATUS, CREATED_DESC, UPDATED_DESC
}