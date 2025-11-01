package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.usecase.plan.DeletePlanUseCase
import com.busylab.todayalarm.domain.usecase.plan.GetPlanByIdUseCase
import com.busylab.todayalarm.domain.usecase.plan.UpdatePlanUseCase
import com.busylab.todayalarm.domain.usecase.validation.InputValidator
import com.busylab.todayalarm.ui.state.EditPlanUiEvent
import com.busylab.todayalarm.ui.state.EditPlanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class EditPlanViewModel @Inject constructor(
    private val getPlanByIdUseCase: GetPlanByIdUseCase,
    private val updatePlanUseCase: UpdatePlanUseCase,
    private val deletePlanUseCase: DeletePlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditPlanUiState())
    val uiState: StateFlow<EditPlanUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<EditPlanUiEvent>()
    val uiEvent: SharedFlow<EditPlanUiEvent> = _uiEvent.asSharedFlow()

    fun onEvent(event: EditPlanUiEvent) {
        when (event) {
            is EditPlanUiEvent.LoadPlan -> {
                loadPlan(event.planId)
            }
            is EditPlanUiEvent.TitleChanged -> {
                updateTitle(event.title)
            }
            is EditPlanUiEvent.ContentChanged -> {
                updateContent(event.content)
            }
            is EditPlanUiEvent.DateTimeChanged -> {
                updateDateTime(event.dateTime)
            }
            is EditPlanUiEvent.RepeatToggled -> {
                updateRepeatToggle(event.isRepeating)
            }
            is EditPlanUiEvent.RepeatTypeChanged -> {
                updateRepeatType(event.repeatType)
            }
            is EditPlanUiEvent.RepeatIntervalChanged -> {
                updateRepeatInterval(event.interval)
            }
            is EditPlanUiEvent.ActiveToggled -> {
                updateActiveStatus(event.isActive)
            }
            is EditPlanUiEvent.UpdatePlan -> {
                updatePlan()
            }
            is EditPlanUiEvent.DeletePlan -> {
                deletePlan()
            }
            is EditPlanUiEvent.ClearValidationErrors -> {
                clearValidationErrors()
            }

            EditPlanUiEvent.NavigateBack -> {
                // Handled by UI layer
            }
            is EditPlanUiEvent.ShowError -> {
                // Handled by UI layer
            }
            is EditPlanUiEvent.ShowSnackbar -> {
                // Handled by UI layer
            }
        }
    }

    private fun loadPlan(planId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                planId = planId,
                isLoading = true,
                error = null
            )

            try {
                val result = getPlanByIdUseCase(planId)

                result.fold(
                    onSuccess = { plan ->
                        _uiState.value = EditPlanUiState(
                            planId = plan.id,
                            title = plan.title,
                            content = plan.content,
                            triggerDateTime = plan.triggerTime,
                            isRepeating = plan.isRepeating,
                            repeatType = plan.repeatType,
                            repeatInterval = plan.repeatInterval,
                            isActive = plan.isActive,
                            isLoading = false
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            planNotFound = true,
                            error = error.message ?: "计划不存在"
                        )
                        _uiEvent.emit(EditPlanUiEvent.ShowError("加载计划失败: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    planNotFound = true,
                    error = e.message ?: "加载计划失败"
                )
                _uiEvent.emit(EditPlanUiEvent.ShowError("加载计划失败: ${e.message}"))
            }
        }
    }

    private fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            validationErrors = _uiState.value.validationErrors - "title"
        )
    }

    private fun updateContent(content: String) {
        _uiState.value = _uiState.value.copy(
            content = content,
            validationErrors = _uiState.value.validationErrors - "content"
        )
    }

    private fun updateDateTime(dateTime: LocalDateTime) {
        _uiState.value = _uiState.value.copy(
            triggerDateTime = dateTime,
            validationErrors = _uiState.value.validationErrors - "triggerDateTime"
        )
    }

    private fun updateRepeatToggle(isRepeating: Boolean) {
        _uiState.value = _uiState.value.copy(
            isRepeating = isRepeating,
            repeatType = if (isRepeating) RepeatType.DAILY else RepeatType.NONE
        )
    }

    private fun updateRepeatType(repeatType: RepeatType) {
        _uiState.value = _uiState.value.copy(repeatType = repeatType)
    }

    private fun updateRepeatInterval(interval: Int) {
        _uiState.value = _uiState.value.copy(
            repeatInterval = interval,
            validationErrors = _uiState.value.validationErrors - "repeatInterval"
        )
    }

    private fun updateActiveStatus(isActive: Boolean) {
        _uiState.value = _uiState.value.copy(isActive = isActive)
    }

    private fun updatePlan() {
        viewModelScope.launch {
            val state = _uiState.value

            // 验证输入
            val validationErrors = validateInput(state)
            if (validationErrors.isNotEmpty()) {
                _uiState.value = state.copy(validationErrors = validationErrors)
                return@launch
            }

            _uiState.value = state.copy(isLoading = true, error = null)

            try {
                val result = updatePlanUseCase.updatePlan(
                    planId = state.planId,
                    title = state.title,
                    content = state.content,
                    triggerTime = state.triggerDateTime!!,
                    isRepeating = state.isRepeating,
                    repeatType = state.repeatType,
                    repeatInterval = state.repeatInterval,
                    isActive = state.isActive
                )

                result.fold(
                    onSuccess = {
                        _uiState.value = state.copy(
                            isLoading = false,
                            isUpdated = true
                        )
                        _uiEvent.emit(EditPlanUiEvent.ShowSnackbar("计划更新成功"))
                        _uiEvent.emit(EditPlanUiEvent.NavigateBack)
                    },
                    onFailure = { error ->
                        _uiState.value = state.copy(
                            isLoading = false,
                            error = error.message ?: "更新计划失败"
                        )
                        _uiEvent.emit(EditPlanUiEvent.ShowError("更新计划失败: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "更新计划失败"
                )
                _uiEvent.emit(EditPlanUiEvent.ShowError("更新计划失败: ${e.message}"))
            }
        }
    }

    private fun deletePlan() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.value = state.copy(isLoading = true, error = null)

            try {
                val result = deletePlanUseCase(state.planId)

                result.fold(
                    onSuccess = {
                        _uiState.value = state.copy(isLoading = false)
                        _uiEvent.emit(EditPlanUiEvent.ShowSnackbar("计划已删除"))
                        _uiEvent.emit(EditPlanUiEvent.NavigateBack)
                    },
                    onFailure = { error ->
                        _uiState.value = state.copy(
                            isLoading = false,
                            error = error.message ?: "删除计划失败"
                        )
                        _uiEvent.emit(EditPlanUiEvent.ShowError("删除计划失败: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "删除计划失败"
                )
                _uiEvent.emit(EditPlanUiEvent.ShowError("删除计划失败: ${e.message}"))
            }
        }
    }

    private fun validateInput(state: EditPlanUiState): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // 验证标题
        val titleValidation = InputValidator.validatePlanTitle(state.title)
        if (titleValidation is InputValidator.ValidationResult.Error) {
            errors["title"] = titleValidation.message
        }

        // 验证内容
        val contentValidation = InputValidator.validatePlanContent(state.content)
        if (contentValidation is InputValidator.ValidationResult.Error) {
            errors["content"] = contentValidation.message
        }

        // 验证触发时间
        state.triggerDateTime?.let { dateTime ->
            val timeValidation = InputValidator.validateTriggerTime(dateTime)
            if (timeValidation is InputValidator.ValidationResult.Error) {
                errors["triggerDateTime"] = timeValidation.message
            }
        } ?: run {
            errors["triggerDateTime"] = "请选择提醒时间"
        }

        // 验证重复间隔
        if (state.isRepeating) {
            val intervalValidation = InputValidator.validateRepeatInterval(
                state.repeatInterval,
                state.repeatType
            )
            if (intervalValidation is InputValidator.ValidationResult.Error) {
                errors["repeatInterval"] = intervalValidation.message
            }
        }

        return errors
    }

    private fun clearValidationErrors() {
        _uiState.value = _uiState.value.copy(validationErrors = emptyMap())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}