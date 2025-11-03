package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.usecase.calendar.GetWeekCalendarUseCase
import com.busylab.todayalarm.domain.usecase.todo.GetTodoItemsUseCase
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import com.busylab.todayalarm.ui.state.HomeUiEvent
import com.busylab.todayalarm.ui.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getWeekCalendarUseCase: GetWeekCalendarUseCase,
    private val getTodoItemsUseCase: GetTodoItemsUseCase,
    private val planRepository: PlanRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    private var currentWeekOffset = 0

    init {
        loadData()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.DateSelected -> {
                selectDate(event.date)
            }
            is HomeUiEvent.RefreshData -> {
                loadData()
            }
            is HomeUiEvent.WeekChanged -> {
                changeWeek(event.weekOffset)
            }

            HomeUiEvent.DebugNotification -> {
                debugAlarm()
            }

            HomeUiEvent.NavigateBack -> {
                // Handled by UI layer
            }
            is HomeUiEvent.ShowError -> {
                // Handled by UI layer
            }
            is HomeUiEvent.ShowSnackbar -> {
                // Handled by UI layer
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                combine(
                    getWeekCalendarUseCase(currentWeekOffset),
                    getTodoItemsUseCase.getPendingTodoItems()
                ) { weekCalendar, allTodos ->
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val selectedDate = _uiState.value.selectedDate ?: today

                    val todayTodos = allTodos.filter {
                        it.triggerTime.date == today
                    }

                    val selectedDateTodos = allTodos.filter {
                        it.triggerTime.date == selectedDate
                    }

                    HomeUiState(
                        weekCalendar = weekCalendar,
                        selectedDate = selectedDate,
                        todayTodos = todayTodos,
                        selectedDateTodos = selectedDateTodos,
                        isLoading = false,
                        error = null
                    )
                }
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "加载数据失败"
                    )
                }
                .collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载数据失败"
                )
            }
        }
    }

    private fun selectDate(date: LocalDate) {
        viewModelScope.launch {
            val currentState = _uiState.value

            // 更新选中日期
            _uiState.value = currentState.copy(selectedDate = date)

            // 获取选中日期的待办事项
            getTodoItemsUseCase.getPendingTodoItems()
                .catch { error ->
                    _uiEvent.emit(HomeUiEvent.ShowError("获取待办事项失败: ${error.message}"))
                }
                .collect { allTodos ->
                    val selectedDateTodos = allTodos.filter {
                        it.triggerTime.date == date
                    }

                    _uiState.value = _uiState.value.copy(
                        selectedDateTodos = selectedDateTodos
                    )
                }
        }
    }

    private fun changeWeek(weekOffset: Int) {
        currentWeekOffset = weekOffset
        loadData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshData() {
        loadData()
    }

    private fun debugAlarm() {
        viewModelScope.launch {
            try {
                val timeZone = TimeZone.currentSystemDefault()
                val now = Clock.System.now()
                val triggerTime = now.plus(60, DateTimeUnit.SECOND, timeZone)
                val nowLocalDateTime = now.toLocalDateTime(timeZone)

                // 创建一个调试用的计划
                val debugPlan = Plan(
                    id = "debug_plan_${UUID.randomUUID()}",
                    title = "调试闹钟",
                    content = "这是一个调试闹钟，用于测试AlarmManager定时逻辑是否正常。",
                    triggerTime = triggerTime.toLocalDateTime(timeZone),
                    repeatType = RepeatType.NONE,
                    isActive = true,
                    createdAt = nowLocalDateTime,
                    updatedAt = nowLocalDateTime
                )

                // 先将计划保存到数据库
                planRepository.insertPlan(debugPlan)

                // 再使用闹钟调度器安排闹钟
                alarmScheduler.scheduleAlarm(debugPlan)

                // 显示成功消息
                _uiEvent.emit(HomeUiEvent.ShowSnackbar("测试闹钟已安排在60秒后"))
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.ShowError("安排调试闹钟失败: ${e.message}"))
            }
        }
    }
}