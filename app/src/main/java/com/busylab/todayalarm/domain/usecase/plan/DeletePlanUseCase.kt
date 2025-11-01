package com.busylab.todayalarm.domain.usecase.plan

import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletePlanUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val todoItemRepository: TodoItemRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(planId: String): Result<Unit> {
        return try {
            // 取消闹钟
            alarmScheduler.cancelAlarm(planId)
            // 删除相关的待办事项
            todoItemRepository.deleteTodoItemsByPlanId(planId)
            // 删除计划
            planRepository.deletePlanById(planId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}