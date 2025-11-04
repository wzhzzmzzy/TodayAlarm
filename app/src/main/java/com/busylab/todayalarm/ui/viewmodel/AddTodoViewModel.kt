package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.usecase.todo.CreateTodoWithPlanUseCase
import com.busylab.todayalarm.ui.state.AddTodoUiEvent
import com.busylab.todayalarm.ui.state.AddTodoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(
    private val createTodoWithPlanUseCase: CreateTodoWithPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddTodoUiState(
            triggerTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    )
    val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddTodoUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(content = content)
    }

    fun updateTriggerTime(triggerTime: LocalDateTime) {
        _uiState.value = _uiState.value.copy(triggerTime = triggerTime)
    }

    fun updateEnableRepeating(enableRepeating: Boolean) {
        _uiState.value = _uiState.value.copy(enableRepeating = enableRepeating)
    }

    fun updateRepeatType(repeatType: RepeatType) {
        _uiState.value = _uiState.value.copy(repeatType = repeatType)
    }

    fun updateRepeatInterval(repeatInterval: Int) {
        _uiState.value = _uiState.value.copy(repeatInterval = repeatInterval)
    }

    fun createTodo() {
        val currentState = _uiState.value

        if (currentState.title.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(AddTodoUiEvent.Error("标题不能为空"))
            }
            return
        }

        _uiState.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            createTodoWithPlanUseCase(
                CreateTodoWithPlanUseCase.Params(
                    title = currentState.title,
                    content = currentState.content,
                    triggerTime = currentState.triggerTime,
                    enableRepeating = currentState.enableRepeating,
                    repeatType = currentState.repeatType,
                    repeatInterval = currentState.repeatInterval
                )
            ).onSuccess { todoItem ->
                _uiState.value = currentState.copy(isLoading = false)
                _uiEvent.emit(AddTodoUiEvent.TodoCreated(todoItem))
            }.onFailure { e ->
                _uiState.value = currentState.copy(isLoading = false)
                _uiEvent.emit(AddTodoUiEvent.Error(e.message ?: "创建失败"))
            }
        }
    }
}