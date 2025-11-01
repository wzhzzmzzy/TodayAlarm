package com.busylab.todayalarm.domain.usecase.plan

import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletePlanUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val todoItemRepository: TodoItemRepository
) {
    suspend operator fun invoke(planId: String): Result<Unit> {
        return try {
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