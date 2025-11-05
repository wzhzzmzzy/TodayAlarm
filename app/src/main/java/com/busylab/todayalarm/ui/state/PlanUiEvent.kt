package com.busylab.todayalarm.ui.state

import com.busylab.todayalarm.domain.model.RepeatType
import kotlinx.datetime.LocalDateTime

sealed class PlanUiEvent {
    data class LoadPlan(val planId: String) : PlanUiEvent()
    data class TitleChanged(val title: String) : PlanUiEvent()
    data class ContentChanged(val content: String) : PlanUiEvent()
    data class DateTimeChanged(val dateTime: LocalDateTime) : PlanUiEvent()
    data class RepeatToggled(val isRepeating: Boolean) : PlanUiEvent()
    data class RepeatTypeChanged(val repeatType: RepeatType) : PlanUiEvent()
    data class RepeatIntervalChanged(val interval: Int) : PlanUiEvent()
    data class ActiveToggled(val isActive: Boolean) : PlanUiEvent()
    object SaveOrUpdatePlan : PlanUiEvent()
    object DeletePlan : PlanUiEvent()

    // Channel Events
    data class ShowSnackbar(val message: String) : PlanUiEvent()
    data class ShowError(val error: String) : PlanUiEvent()
    object NavigateBack : PlanUiEvent()
}