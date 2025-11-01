package com.busylab.todayalarm.ui.state

import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.model.PlanUiModel
import com.busylab.todayalarm.domain.model.TodoItemUiModel
import com.busylab.todayalarm.domain.model.WeekCalendarModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * 主页UI状态
 */
data class HomeUiState(
    val weekCalendar: WeekCalendarModel? = null,
    val selectedDate: LocalDate? = null,
    val todayTodos: List<TodoItemUiModel> = emptyList(),
    val selectedDateTodos: List<TodoItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 添加计划UI状态
 */
data class AddPlanUiState(
    val title: String = "",
    val content: String = "",
    val triggerDateTime: LocalDateTime? = null,
    val isRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatInterval: Int = 1,
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val isSaved: Boolean = false
)

/**
 * 计划列表UI状态
 */
data class PlanListUiState(
    val plans: List<PlanUiModel> = emptyList(),
    val activePlans: List<PlanUiModel> = emptyList(),
    val inactivePlans: List<PlanUiModel> = emptyList(),
    val filterType: PlanFilterType = PlanFilterType.ALL,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 编辑计划UI状态
 */
data class EditPlanUiState(
    val planId: String = "",
    val title: String = "",
    val content: String = "",
    val triggerDateTime: LocalDateTime? = null,
    val isRepeating: Boolean = false,
    val repeatType: RepeatType = RepeatType.NONE,
    val repeatInterval: Int = 1,
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val validationErrors: Map<String, String> = emptyMap(),
    val isUpdated: Boolean = false,
    val planNotFound: Boolean = false
)

/**
 * 计划过滤类型
 */
enum class PlanFilterType {
    ALL,        // 所有计划
    ACTIVE,     // 活跃计划
    INACTIVE    // 非活跃计划
}

/**
 * UI事件基类
 */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class ShowError(val error: String) : UiEvent
    object NavigateBack : UiEvent
}

/**
 * 主页UI事件
 */
sealed interface HomeUiEvent : UiEvent {
    data class DateSelected(val date: LocalDate) : HomeUiEvent
    object RefreshData : HomeUiEvent
    data class WeekChanged(val weekOffset: Int) : HomeUiEvent
    object DebugNotification : HomeUiEvent
    data class ShowSnackbar(val message: String) : HomeUiEvent
    data class ShowError(val error: String) : HomeUiEvent
    object NavigateBack : HomeUiEvent
}

/**
 * 添加计划UI事件
 */
sealed interface AddPlanUiEvent : UiEvent {
    data class TitleChanged(val title: String) : AddPlanUiEvent
    data class ContentChanged(val content: String) : AddPlanUiEvent
    data class DateTimeChanged(val dateTime: LocalDateTime) : AddPlanUiEvent
    data class RepeatToggled(val isRepeating: Boolean) : AddPlanUiEvent
    data class RepeatTypeChanged(val repeatType: RepeatType) : AddPlanUiEvent
    data class RepeatIntervalChanged(val interval: Int) : AddPlanUiEvent
    object SavePlan : AddPlanUiEvent
    object ClearValidationErrors : AddPlanUiEvent
    data class ShowSnackbar(val message: String) : AddPlanUiEvent
    data class ShowError(val error: String) : AddPlanUiEvent
    object NavigateBack : AddPlanUiEvent
}

/**
 * 计划列表UI事件
 */
sealed interface PlanListUiEvent : UiEvent {
    data class FilterChanged(val filterType: PlanFilterType) : PlanListUiEvent
    data class PlanToggled(val planId: String, val isActive: Boolean) : PlanListUiEvent
    data class PlanDeleted(val planId: String) : PlanListUiEvent
    object RefreshPlans : PlanListUiEvent
    data class ShowSnackbar(val message: String) : PlanListUiEvent
    data class ShowError(val error: String) : PlanListUiEvent
    object NavigateBack : PlanListUiEvent
}

/**
 * 编辑计划UI事件
 */
sealed interface EditPlanUiEvent : UiEvent {
    data class LoadPlan(val planId: String) : EditPlanUiEvent
    data class TitleChanged(val title: String) : EditPlanUiEvent
    data class ContentChanged(val content: String) : EditPlanUiEvent
    data class DateTimeChanged(val dateTime: LocalDateTime) : EditPlanUiEvent
    data class RepeatToggled(val isRepeating: Boolean) : EditPlanUiEvent
    data class RepeatTypeChanged(val repeatType: RepeatType) : EditPlanUiEvent
    data class RepeatIntervalChanged(val interval: Int) : EditPlanUiEvent
    data class ActiveToggled(val isActive: Boolean) : EditPlanUiEvent
    object UpdatePlan : EditPlanUiEvent
    object DeletePlan : EditPlanUiEvent
    object ClearValidationErrors : EditPlanUiEvent
    data class ShowSnackbar(val message: String) : EditPlanUiEvent
    data class ShowError(val error: String) : EditPlanUiEvent
    object NavigateBack : EditPlanUiEvent
}