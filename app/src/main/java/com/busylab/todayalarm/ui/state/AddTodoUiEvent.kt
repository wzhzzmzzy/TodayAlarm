package com.busylab.todayalarm.ui.state

import com.busylab.todayalarm.domain.model.TodoItem

sealed class AddTodoUiEvent {
    data class TodoCreated(val todoItem: TodoItem) : AddTodoUiEvent()
    data class Error(val message: String) : AddTodoUiEvent()
}