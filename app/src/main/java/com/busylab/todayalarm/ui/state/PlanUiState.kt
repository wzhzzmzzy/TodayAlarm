package com.busylab.todayalarm.ui.state

import com.busylab.todayalarm.domain.model.RepeatType
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class PlanUiState(
    val planId: String? = null,
    val title: String = "",
    val content: String = "",
    val triggerDateTime: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val isRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.DAILY,
    val repeatInterval: Int = 1,
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val planNotFound: Boolean = false
)