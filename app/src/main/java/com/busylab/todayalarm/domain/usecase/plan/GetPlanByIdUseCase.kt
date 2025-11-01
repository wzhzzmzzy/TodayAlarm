package com.busylab.todayalarm.domain.usecase.plan

import com.busylab.todayalarm.data.datastore.UserPreferencesDataStore
import com.busylab.todayalarm.domain.exception.BusinessException
import com.busylab.todayalarm.domain.model.ModelMapper.toUiModel
import com.busylab.todayalarm.domain.model.PlanUiModel
import com.busylab.todayalarm.domain.repository.PlanRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPlanByIdUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(planId: String): Result<PlanUiModel> {
        return try {
            if (planId.isBlank()) {
                Result.failure(BusinessException.InvalidInputException("计划ID不能为空"))
            } else {
                val plan = planRepository.getPlanById(planId)
                if (plan == null) {
                    Result.failure(BusinessException.PlanNotFoundException(planId))
                } else {
                    val userPreferences = userPreferencesDataStore.userPreferences.first()
                    val planUiModel = plan.toUiModel(userPreferences)
                    Result.success(planUiModel)
                }
            }
        } catch (e: Exception) {
            Result.failure(BusinessException.DatabaseException("获取计划失败", e))
        }
    }
}