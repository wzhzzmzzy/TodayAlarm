package com.busylab.todayalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.busylab.todayalarm.domain.usecase.plan.DeletePlanUseCase
import com.busylab.todayalarm.domain.usecase.plan.GetPlansUseCase
import com.busylab.todayalarm.domain.usecase.plan.UpdatePlanUseCase
import com.busylab.todayalarm.ui.state.PlanFilterType
import com.busylab.todayalarm.ui.state.PlanListUiEvent
import com.busylab.todayalarm.ui.state.PlanListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlanListViewModel @Inject constructor(
    private val getPlansUseCase: GetPlansUseCase,
    private val updatePlanUseCase: UpdatePlanUseCase,
    private val deletePlanUseCase: DeletePlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanListUiState())
    val uiState: StateFlow<PlanListUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PlanListUiEvent>()
    val uiEvent: SharedFlow<PlanListUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadPlans()
    }

    fun onEvent(event: PlanListUiEvent) {
        when (event) {
            is PlanListUiEvent.FilterChanged -> {
                changeFilter(event.filterType)
            }
            is PlanListUiEvent.PlanToggled -> {
                togglePlan(event.planId, event.isActive)
            }
            is PlanListUiEvent.PlanDeleted -> {
                deletePlan(event.planId)
            }
            is PlanListUiEvent.RefreshPlans -> {
                loadPlans()
            }

            PlanListUiEvent.NavigateBack -> {
                // Handled by UI layer
            }
            is PlanListUiEvent.ShowError -> {
                // Handled by UI layer
            }
            is PlanListUiEvent.ShowSnackbar -> {
                // Handled by UI layer
            }
        }
    }

    private fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            getPlansUseCase()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "加载计划列表失败"
                    )
                }
                .collect { plans ->
                    val activePlans = plans.filter { it.isActive }
                    val inactivePlans = plans.filter { !it.isActive }

                    _uiState.value = _uiState.value.copy(
                        plans = plans,
                        activePlans = activePlans,
                        inactivePlans = inactivePlans,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    private fun changeFilter(filterType: PlanFilterType) {
        _uiState.value = _uiState.value.copy(filterType = filterType)
    }

    private fun togglePlan(planId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val result = updatePlanUseCase.updateActiveStatus(planId, isActive)

                result.fold(
                    onSuccess = {
                        _uiEvent.emit(
                            PlanListUiEvent.ShowSnackbar(
                                if (isActive) "计划已启用" else "计划已停用"
                            )
                        )
                        // 数据会通过Flow自动更新
                    },
                    onFailure = { error ->
                        _uiEvent.emit(
                            PlanListUiEvent.ShowError("更新计划状态失败: ${error.message}")
                        )
                    }
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    PlanListUiEvent.ShowError("更新计划状态失败: ${e.message}")
                )
            }
        }
    }

    private fun deletePlan(planId: String) {
        viewModelScope.launch {
            try {
                val result = deletePlanUseCase(planId)

                result.fold(
                    onSuccess = {
                        _uiEvent.emit(PlanListUiEvent.ShowSnackbar("计划已删除"))
                        // 数据会通过Flow自动更新
                    },
                    onFailure = { error ->
                        _uiEvent.emit(
                            PlanListUiEvent.ShowError("删除计划失败: ${error.message}")
                        )
                    }
                )
            } catch (e: Exception) {
                _uiEvent.emit(
                    PlanListUiEvent.ShowError("删除计划失败: ${e.message}")
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun refreshPlans() {
        loadPlans()
    }

    /**
     * 获取当前过滤后的计划列表
     */
    fun getFilteredPlans() = when (_uiState.value.filterType) {
        PlanFilterType.ALL -> _uiState.value.plans
        PlanFilterType.ACTIVE -> _uiState.value.activePlans
        PlanFilterType.INACTIVE -> _uiState.value.inactivePlans
    }
}