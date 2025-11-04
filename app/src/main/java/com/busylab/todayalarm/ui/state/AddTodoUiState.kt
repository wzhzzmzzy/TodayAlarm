package com.busylab.todayalarm.ui.state

import com.busylab.todayalarm.domain.model.RepeatType
import kotlinx.datetime.LocalDateTime

data class AddTodoUiState(
    val title: String = "",
    val content: String = "",
    val triggerTime: LocalDateTime,
    val enableRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.DAILY,
    val repeatInterval: Int = 1,
    val isLoading: Boolean = false
)