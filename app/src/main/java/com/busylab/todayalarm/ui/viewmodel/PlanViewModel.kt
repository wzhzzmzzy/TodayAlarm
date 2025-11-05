package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.usecase.plan.CreatePlanUseCase
import com.busylab.todayalarm.domain.usecase.plan.DeletePlanUseCase
import com.busylab.todayalarm.domain.usecase.plan.GetPlanByIdUseCase
import com.busylab.todayalarm.domain.usecase.plan.UpdatePlanUseCase
import com.busylab.todayalarm.domain.usecase.validation.ValidatePlanInputUseCase
import com.busylab.todayalarm.ui.navigation.NavigationArgs
import com.busylab.todayalarm.ui.state.PlanUiEvent
import com.busylab.todayalarm.ui.state.PlanUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PlanViewModel @Inject constructor(
    private val createPlanUseCase: CreatePlanUseCase,
    private val getPlanByIdUseCase: GetPlanByIdUseCase,
    private val updatePlanUseCase: UpdatePlanUseCase,
    private val deletePlanUseCase: DeletePlanUseCase,
    private val validatePlanInputUseCase: ValidatePlanInputUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<PlanUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val planId: String? = savedStateHandle[NavigationArgs.PLAN_ID]

    init {
        if (planId != null) {
            onEvent(PlanUiEvent.LoadPlan(planId))
        }
    }

    fun onEvent(event: PlanUiEvent) {
        when (event) {
            is PlanUiEvent.LoadPlan -> loadPlan(event.planId)
            is PlanUiEvent.TitleChanged -> _uiState.update { it.copy(title = event.title) }
            is PlanUiEvent.ContentChanged -> _uiState.update { it.copy(content = event.content) }
            is PlanUiEvent.DateTimeChanged -> _uiState.update { it.copy(triggerDateTime = event.dateTime) }
            is PlanUiEvent.RepeatToggled -> _uiState.update { it.copy(isRepeating = event.isRepeating) }
            is PlanUiEvent.RepeatTypeChanged -> _uiState.update { it.copy(repeatType = event.repeatType) }
            is PlanUiEvent.RepeatIntervalChanged -> _uiState.update { it.copy(repeatInterval = event.interval) }
            is PlanUiEvent.ActiveToggled -> _uiState.update { it.copy(isActive = event.isActive) }
            is PlanUiEvent.SaveOrUpdatePlan -> saveOrUpdatePlan()
            is PlanUiEvent.DeletePlan -> deletePlan()
            else -> {}
        }
    }

    private fun loadPlan(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val plan = getPlanByIdUseCase(id).getOrThrow()
                _uiState.update {
                    it.copy(
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
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, planNotFound = true, error = "加载计划失败: ${e.message}") }
                _uiEvent.send(PlanUiEvent.ShowError("加载计划失败"))
            }
        }
    }

    private fun saveOrUpdatePlan() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val validationResult = validatePlanInputUseCase(
                title = currentState.title,
                content = currentState.content,
                triggerDateTime = currentState.triggerDateTime,
                isRepeating = currentState.isRepeating,
                repeatInterval = currentState.repeatInterval
            )

            if (validationResult.isFailure) {
                val validationException = validationResult.exceptionOrNull() as? ValidatePlanInputUseCase.ValidationException
                _uiState.update { it.copy(validationErrors = validationException?.errors ?: emptyMap()) }
                return@launch
            }
            _uiState.update { it.copy(isLoading = true, validationErrors = emptyMap()) }

            val result = if (planId == null) {
                // Create new plan
                createPlanUseCase(
                    title = currentState.title,
                    content = currentState.content,
                    triggerTime = currentState.triggerDateTime,
                    isRepeating = currentState.isRepeating,
                    repeatType = currentState.repeatType,
                    repeatInterval = currentState.repeatInterval
                )
            } else {
                // Update existing plan
                updatePlanUseCase.updatePlan(
                    planId = planId,
                    title = currentState.title,
                    content = currentState.content,
                    triggerTime = currentState.triggerDateTime,
                    isRepeating = currentState.isRepeating,
                    repeatType = currentState.repeatType,
                    repeatInterval = currentState.repeatInterval,
                    isActive = currentState.isActive
                )
            }

            result.onSuccess {
                _uiEvent.send(PlanUiEvent.ShowSnackbar(if (planId == null) "计划已创建" else "计划已更新"))
                _uiEvent.send(PlanUiEvent.NavigateBack)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _uiEvent.send(PlanUiEvent.ShowError(e.message ?: "未知错误"))
            }
        }
    }

    private fun deletePlan() {
        viewModelScope.launch {
            if (planId == null) return@launch
            _uiState.update { it.copy(isLoading = true) }
            deletePlanUseCase(planId).onSuccess {
                _uiEvent.send(PlanUiEvent.ShowSnackbar("计划已删除"))
                _uiEvent.send(PlanUiEvent.NavigateBack)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
                _uiEvent.send(PlanUiEvent.ShowError(e.message ?: "删除失败"))
            }
        }
    }
}
