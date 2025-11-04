package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.usecase.todo.CreateTodoWithPlanUseCase
import com.busylab.todayalarm.domain.usecase.todo.GetTodoItemByIdUseCase
import com.busylab.todayalarm.domain.usecase.todo.UpdateTodoItemUseCase
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
    private val createTodoWithPlanUseCase: CreateTodoWithPlanUseCase,
    private val getTodoItemByIdUseCase: GetTodoItemByIdUseCase,
    private val updateTodoItemUseCase: UpdateTodoItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddTodoUiState(
            triggerTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        )
    )
    val uiState: StateFlow<AddTodoUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddTodoUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // 当前编辑的TodoId，用于区分创建和编辑模式
    private var currentTodoId: String? = null

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

    /**
     * 加载待办事项用于编辑
     */
    fun loadTodoForEdit(todoId: String) {
        currentTodoId = todoId
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            getTodoItemByIdUseCase(GetTodoItemByIdUseCase.Params(todoId))
                .onSuccess { todoItem ->
                    if (todoItem != null) {
                        _uiState.value = _uiState.value.copy(
                            title = todoItem.title,
                            content = todoItem.content,
                            triggerTime = todoItem.triggerTime,
                            enableRepeating = todoItem.enableRepeating,
                            repeatType = todoItem.repeatType,
                            repeatInterval = todoItem.repeatInterval,
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        _uiEvent.emit(AddTodoUiEvent.Error("待办事项不存在"))
                    }
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    _uiEvent.emit(AddTodoUiEvent.Error(e.message ?: "加载失败"))
                }
        }
    }

    /**
     * 更新待办事项
     */
    fun updateTodo() {
        val currentState = _uiState.value
        val todoId = currentTodoId

        if (todoId == null) {
            viewModelScope.launch {
                _uiEvent.emit(AddTodoUiEvent.Error("无效的待办事项ID"))
            }
            return
        }

        if (currentState.title.isBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(AddTodoUiEvent.Error("标题不能为空"))
            }
            return
        }

        _uiState.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            updateTodoItemUseCase(
                UpdateTodoItemUseCase.Params(
                    todoId = todoId,
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
                _uiEvent.emit(AddTodoUiEvent.Error(e.message ?: "更新失败"))
            }
        }
    }
}