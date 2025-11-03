package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.usecase.todo.GetTodoItemsUseCaseNew
import com.busylab.todayalarm.domain.usecase.todo.CompleteTodoItemUseCaseNew
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 简化的待办列表ViewModel
 * 只处理列表展示和完成状态切换
 */
@HiltViewModel
class SimpleTodoListViewModel @Inject constructor(
    private val getTodoItemsUseCase: GetTodoItemsUseCaseNew,
    private val completeTodoItemUseCase: CompleteTodoItemUseCaseNew
) : ViewModel() {

    private val _uiState = MutableStateFlow(SimpleTodoListUiState())
    val uiState: StateFlow<SimpleTodoListUiState> = _uiState.asStateFlow()

    init {
        loadTodoItems()
    }

    fun loadTodoItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                getTodoItemsUseCase(GetTodoItemsUseCaseNew.Params())
                    .collect { todoItems ->
                        _uiState.update {
                            it.copy(
                                todoItems = todoItems.sortedWith(
                                    compareBy<TodoItem> { item -> item.isCompleted }
                                        .thenByDescending { item -> item.triggerTime }
                                ),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message ?: "加载失败"
                    )
                }
            }
        }
    }

    fun toggleComplete(todoId: String) {
        viewModelScope.launch {
            try {
                completeTodoItemUseCase(CompleteTodoItemUseCaseNew.Params(todoId))
                    .onSuccess {
                        // 重新加载数据以更新状态
                        loadTodoItems()
                    }
                    .onFailure { throwable ->
                        _uiState.update {
                            it.copy(error = throwable.message ?: "操作失败")
                        }
                    }
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(error = throwable.message ?: "操作失败")
                }
            }
        }
    }
}

/**
 * 简化的UI状态
 */
data class SimpleTodoListUiState(
    val todoItems: List<TodoItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)