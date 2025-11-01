package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.datastore.UserPreferencesDataStore
import com.busylab.todayalarm.domain.model.ModelMapper.toUiModel
import com.busylab.todayalarm.domain.model.TodoItemUiModel
import com.busylab.todayalarm.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetTodoItemsUseCase @Inject constructor(
    private val todoRepository: TodoRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    fun getPendingTodoItems(): Flow<List<TodoItemUiModel>> {
        return combine(
            todoRepository.getIncompleteTodoItems(),
            userPreferencesDataStore.userPreferences
        ) { todoItems, preferences ->
            todoItems.map { it.toUiModel(preferences) }
        }
    }

    fun getCompletedTodoItems(): Flow<List<TodoItemUiModel>> {
        return combine(
            todoRepository.getCompletedTodoItems(),
            userPreferencesDataStore.userPreferences
        ) { todoItems, preferences ->
            todoItems.map { it.toUiModel(preferences) }
        }
    }

    fun getTodoItemsForWeek(weekOffset: Int = 0): Flow<List<TodoItemUiModel>> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val startOfWeekInstant = now.toInstant(TimeZone.currentSystemDefault())
            .plus(weekOffset * 7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        val startOfWeek = startOfWeekInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            .let { date ->
                val instant = date.toInstant(TimeZone.currentSystemDefault())
                    .plus(-date.dayOfWeek.ordinal, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                instant.toLocalDateTime(TimeZone.currentSystemDefault())
            }
        val endOfWeekInstant = startOfWeek.toInstant(TimeZone.currentSystemDefault())
            .plus(6, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        val endOfWeek = endOfWeekInstant.toLocalDateTime(TimeZone.currentSystemDefault())

        val startTime = startOfWeek.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endTime = endOfWeek.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

        return combine(
            todoRepository.getTodoItemsByDate(startOfWeek), // 这里需要实现一个获取时间范围内待办事项的方法
            userPreferencesDataStore.userPreferences
        ) { todoItems, preferences ->
            todoItems.map { it.toUiModel(preferences) }
        }
    }
}