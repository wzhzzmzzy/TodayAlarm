package com.busylab.todayalarm.domain.usecase.calendar

import com.busylab.todayalarm.data.datastore.UserPreferences
import com.busylab.todayalarm.data.datastore.UserPreferencesDataStore
import com.busylab.todayalarm.domain.model.DayModel
import com.busylab.todayalarm.domain.model.PlanUiModel
import com.busylab.todayalarm.domain.model.TodoItemUiModel
import com.busylab.todayalarm.domain.model.WeekCalendarModel
import com.busylab.todayalarm.domain.usecase.plan.GetPlansUseCase
import com.busylab.todayalarm.domain.usecase.todo.GetTodoItemsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetWeekCalendarUseCase @Inject constructor(
    private val getPlansUseCase: GetPlansUseCase,
    private val getTodoItemsUseCase: GetTodoItemsUseCase,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    operator fun invoke(weekOffset: Int = 0): Flow<WeekCalendarModel> {
        return combine(
            getPlansUseCase.getPlansForWeek(weekOffset),
            getTodoItemsUseCase.getTodoItemsForWeek(weekOffset),
            userPreferencesDataStore.userPreferences
        ) { plans, todoItems, preferences ->
            createWeekCalendarModel(plans, todoItems, weekOffset, preferences)
        }
    }

    private fun createWeekCalendarModel(
        plans: List<PlanUiModel>,
        todoItems: List<TodoItemUiModel>,
        weekOffset: Int,
        preferences: UserPreferences
    ): WeekCalendarModel {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val startOfWeekInstant = now.toInstant(TimeZone.currentSystemDefault())
            .plus(weekOffset * 7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        val startOfWeek = startOfWeekInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            .let { date ->
                val instant = date.toInstant(TimeZone.currentSystemDefault())
                    .plus(-date.dayOfWeek.ordinal, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                instant.toLocalDateTime(TimeZone.currentSystemDefault())
            }

        val days = (0..6).map { dayOffset ->
            val dateInstant = startOfWeek.toInstant(TimeZone.currentSystemDefault())
                .plus(dayOffset, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            val date = dateInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val dayPlans = plans.filter {
                it.triggerTime.date == date.date
            }
            val dayTodoItems = todoItems.filter {
                it.triggerTime.date == date.date
            }

            DayModel(
                date = date.date,
                dayOfWeek = getDayOfWeekName(date.dayOfWeek),
                dayOfMonth = date.dayOfMonth,
                isToday = date.date == now.date,
                isCurrentMonth = date.monthNumber == now.monthNumber,
                todoItems = dayTodoItems,
                plans = dayPlans,
                hasEvents = dayPlans.isNotEmpty() || dayTodoItems.isNotEmpty()
            )
        }

        val weekRange = "${startOfWeek.year}年${startOfWeek.monthNumber}月${startOfWeek.dayOfMonth}日"

        return WeekCalendarModel(
            days = days,
            weekRange = weekRange,
            currentWeekOffset = weekOffset
        )
    }

    private fun getDayOfWeekName(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "周一"
            DayOfWeek.TUESDAY -> "周二"
            DayOfWeek.WEDNESDAY -> "周三"
            DayOfWeek.THURSDAY -> "周四"
            DayOfWeek.FRIDAY -> "周五"
            DayOfWeek.SATURDAY -> "周六"
            DayOfWeek.SUNDAY -> "周日"
        }
    }
}