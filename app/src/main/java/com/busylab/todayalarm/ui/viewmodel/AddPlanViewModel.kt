package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.usecase.plan.CreatePlanUseCase
import com.busylab.todayalarm.domain.usecase.validation.InputValidator
import com.busylab.todayalarm.ui.state.AddPlanUiEvent
import com.busylab.todayalarm.ui.state.AddPlanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class AddPlanViewModel @Inject constructor(
    private val createPlanUseCase: CreatePlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddPlanUiState(
            triggerDateTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let {
                    val timeZone = TimeZone.currentSystemDefault()
                    val instant = it.toInstant(timeZone)
                    val newInstant = instant.plus(1, DateTimeUnit.HOUR, timeZone)
                    newInstant.toLocalDateTime(timeZone)
                }
        )
    )
    val uiState: StateFlow<AddPlanUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddPlanUiEvent>()
    val uiEvent: SharedFlow<AddPlanUiEvent> = _uiEvent.asSharedFlow()

    fun onEvent(event: AddPlanUiEvent) {
        when (event) {
            is AddPlanUiEvent.TitleChanged -> {
                updateTitle(event.title)
            }
            is AddPlanUiEvent.ContentChanged -> {
                updateContent(event.content)
            }
            is AddPlanUiEvent.DateTimeChanged -> {
                updateDateTime(event.dateTime)
            }
            is AddPlanUiEvent.RepeatToggled -> {
                updateRepeatToggle(event.isRepeating)
            }
            is AddPlanUiEvent.RepeatTypeChanged -> {
                updateRepeatType(event.repeatType)
            }
            is AddPlanUiEvent.RepeatIntervalChanged -> {
                updateRepeatInterval(event.interval)
            }
            is AddPlanUiEvent.SavePlan -> {
                savePlan()
            }
            is AddPlanUiEvent.ClearValidationErrors -> {
                clearValidationErrors()
            }
            is AddPlanUiEvent.ShowSnackbar -> {
                // Handled by UI layer
            }
            is AddPlanUiEvent.ShowError -> {
                // Handled by UI layer
            }
            is AddPlanUiEvent.NavigateBack -> {
                // Handled by UI layer
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

    private fun savePlan() {
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
                val result = createPlanUseCase(
                    title = state.title,
                    content = state.content,
                    triggerTime = state.triggerDateTime!!,
                    isRepeating = state.isRepeating,
                    repeatType = state.repeatType,
                    repeatInterval = state.repeatInterval
                )

                result.fold(
                    onSuccess = { planId ->
                        _uiState.value = state.copy(
                            isLoading = false,
                            isSaved = true
                        )
                        _uiEvent.emit(AddPlanUiEvent.ShowSnackbar("计划创建成功"))
                        _uiEvent.emit(AddPlanUiEvent.NavigateBack)
                    },
                    onFailure = { error ->
                        _uiState.value = state.copy(
                            isLoading = false,
                            error = error.message ?: "保存计划失败"
                        )
                        _uiEvent.emit(AddPlanUiEvent.ShowError("保存计划失败: ${error.message}"))
                    }
                )
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = e.message ?: "保存计划失败"
                )
                _uiEvent.emit(AddPlanUiEvent.ShowError("保存计划失败: ${e.message}"))
            }
        }
    }

    private fun validateInput(state: AddPlanUiState): Map<String, String> {
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

    fun resetState() {
        _uiState.value = AddPlanUiState(
            triggerDateTime = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let {
                    val timeZone = TimeZone.currentSystemDefault()
                    val instant = it.toInstant(timeZone)
                    val newInstant = instant.plus(1, DateTimeUnit.HOUR, timeZone)
                    newInstant.toLocalDateTime(timeZone)
                }
        )
    }
}

