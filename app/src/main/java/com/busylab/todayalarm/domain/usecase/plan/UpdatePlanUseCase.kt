package com.busylab.todayalarm.domain.usecase.plan

import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.exception.BusinessException
import com.busylab.todayalarm.domain.model.ModelMapper.toDomainModel
import com.busylab.todayalarm.domain.model.PlanUiModel
import com.busylab.todayalarm.domain.repository.PlanRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdatePlanUseCase @Inject constructor(
    private val planRepository: PlanRepository
) {
    suspend operator fun invoke(planUiModel: PlanUiModel): Result<Unit> {
        return try {
            // 验证输入
            if (planUiModel.title.isBlank()) {
                return Result.failure(IllegalArgumentException("标题不能为空"))
            }
            if (planUiModel.content.isBlank()) {
                return Result.failure(IllegalArgumentException("内容不能为空"))
            }

            val plan = planUiModel.toDomainModel()
            planRepository.updatePlan(plan)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateActiveStatus(planId: String, isActive: Boolean): Result<Unit> {
        return try {
            if (planId.isBlank()) {
                Result.failure(BusinessException.InvalidInputException("计划ID不能为空"))
            } else {
                planRepository.updatePlanActiveStatus(planId, isActive)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(BusinessException.DatabaseException("更新计划状态失败", e))
        }
    }

    suspend fun updatePlan(
        planId: String,
        title: String,
        content: String,
        triggerTime: LocalDateTime,
        isRepeating: Boolean,
        repeatType: RepeatType,
        repeatInterval: Int,
        isActive: Boolean
    ): Result<Unit> {
        return try {
            if (planId.isBlank()) {
                return Result.failure(BusinessException.InvalidInputException("计划ID不能为空"))
            }
            if (title.isBlank()) {
                return Result.failure(BusinessException.InvalidInputException("标题不能为空"))
            }
            if (content.isBlank()) {
                return Result.failure(BusinessException.InvalidInputException("内容不能为空"))
            }

            val existingPlan = planRepository.getPlanById(planId)
                ?: return Result.failure(BusinessException.PlanNotFoundException(planId))

            val updatedPlan = existingPlan.copy(
                title = title,
                content = content,
                triggerTime = triggerTime,
                isRepeating = isRepeating,
                repeatType = repeatType,
                repeatInterval = repeatInterval,
                isActive = isActive,
                updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )

            planRepository.updatePlan(updatedPlan)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(BusinessException.DatabaseException("更新计划失败", e))
        }
    }
}

