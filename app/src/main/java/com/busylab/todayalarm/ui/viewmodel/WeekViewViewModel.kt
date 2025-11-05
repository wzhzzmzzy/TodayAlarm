package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.model.ModelMapper.toUiModel
import com.busylab.todayalarm.domain.usecase.calendar.GetWeekCalendarUseCase
import com.busylab.todayalarm.domain.usecase.todo.GetTodoItemsUseCase
import com.busylab.todayalarm.domain.usecase.todo.TodoFilter
import com.busylab.todayalarm.ui.state.WeekViewUiState
import com.busylab.todayalarm.ui.state.WeekViewUiEvent
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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * 周视图ViewModel
 * 负责周历数据管理和日期选择逻辑
 */
@HiltViewModel
class WeekViewViewModel @Inject constructor(
    private val getWeekCalendarUseCase: GetWeekCalendarUseCase,
    private val getTodoItemsUseCase: GetTodoItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeekViewUiState())
    val uiState: StateFlow<WeekViewUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<WeekViewUiEvent>()
    val uiEvent: SharedFlow<WeekViewUiEvent> = _uiEvent.asSharedFlow()

    private var currentWeekOffset = 0

    init {
        loadWeekData()
    }

    fun onEvent(event: WeekViewUiEvent) {
        when (event) {
            is WeekViewUiEvent.DateSelected -> {
                selectDate(event.date)
            }
            is WeekViewUiEvent.WeekChanged -> {
                changeWeek(event.weekOffset)
            }
            is WeekViewUiEvent.RefreshData -> {
                loadWeekData()
            }
            is WeekViewUiEvent.ShowError -> {
                // Handled by UI layer
            }
            is WeekViewUiEvent.ShowSnackbar -> {
                // Handled by UI layer
            }
        }
    }

    private fun loadWeekData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                combine(
                    getWeekCalendarUseCase(currentWeekOffset),
                    getTodoItemsUseCase(GetTodoItemsUseCase.Params(filter = TodoFilter.PENDING))
                ) { weekCalendar, allTodos ->
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val selectedDate = _uiState.value.selectedDate ?: today

                    val selectedDateTodos = allTodos.filter {
                        it.triggerTime.date == selectedDate
                    }.map { it.toUiModel() }

                    WeekViewUiState(
                        weekCalendar = weekCalendar,
                        selectedDate = selectedDate,
                        selectedDateTodos = selectedDateTodos,
                        isLoading = false,
                        error = null
                    )
                }
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "加载周历数据失败"
                    )
                }
                .collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "加载周历数据失败"
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
            getTodoItemsUseCase(GetTodoItemsUseCase.Params(filter = TodoFilter.PENDING))
                .catch { error ->
                    _uiEvent.emit(WeekViewUiEvent.ShowError("获取待办事项失败: ${error.message}"))
                }
                .collect { allTodos ->
                    val selectedDateTodos = allTodos.filter {
                        it.triggerTime.date == date
                    }.map { it.toUiModel() }

                    _uiState.value = _uiState.value.copy(
                        selectedDateTodos = selectedDateTodos
                    )
                }
        }
    }

    private fun changeWeek(weekOffset: Int) {
        currentWeekOffset = weekOffset
        loadWeekData()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshData() {
        loadWeekData()
    }
}