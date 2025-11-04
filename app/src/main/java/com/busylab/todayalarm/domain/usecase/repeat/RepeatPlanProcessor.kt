package com.busylab.todayalarm.domain.usecase.repeat

import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.exception.BusinessException
import com.busylab.todayalarm.domain.model.RepeatRule
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepeatPlanProcessor @Inject constructor(
    private val planRepository: PlanRepository,
    private val todoRepository: TodoRepository
) {
    suspend fun processTriggeredPlan(planId: String): Result<Unit> {
        return try {
            val plan = planRepository.getPlanById(planId)
                ?: return Result.failure(BusinessException.PlanNotFoundException(planId))

            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            // 如果是重复计划，计算下次触发时间并更新
            if (plan.isRepeating) {
                val repeatRule = RepeatRule(
                    type = plan.repeatType,
                    interval = plan.repeatInterval
                )
                val nextTriggerTime = repeatRule.getNextTriggerTime(plan.triggerTime)

                if (nextTriggerTime != null) {
                    val updatedPlan = plan.copy(
                        triggerTime = nextTriggerTime,
                        updatedAt = currentTime
                    )
                    planRepository.updatePlan(updatedPlan)
                } else {
                    // 如果没有下次触发时间，停用计划
                    planRepository.updatePlanActiveStatus(plan.id, false)
                }
            } else {
                // 非重复计划，触发后停用
                planRepository.updatePlanActiveStatus(plan.id, false)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUpcomingPlans(hoursAhead: Int = 24): List<Plan> {
        val now = Clock.System.now()
        val futureTime = now.plus(hoursAhead, DateTimeUnit.HOUR)

        return planRepository.getPlansInTimeRange(
            now.toEpochMilliseconds(),
            futureTime.toEpochMilliseconds()
        ).first()
    }
}