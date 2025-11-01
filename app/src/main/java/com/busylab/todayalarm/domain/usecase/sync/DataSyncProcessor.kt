package com.busylab.todayalarm.domain.usecase.sync

import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSyncProcessor @Inject constructor(
    private val planRepository: PlanRepository,
    private val todoRepository: TodoRepository
) {
    suspend fun cleanupOldData(): Result<Unit> {
        return try {
            val thirtyDaysAgo = Clock.System.now()
                .plus(-30, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                .toLocalDateTime(TimeZone.currentSystemDefault())

            // 清理30天前的已完成待办事项
            val oldCompletedTodos = todoRepository.getCompletedTodoItems().first()
                .filter { it.completedAt != null && it.completedAt!! < thirtyDaysAgo }

            oldCompletedTodos.forEach { todoItem ->
                todoRepository.deleteTodoItem(todoItem)
            }

            // 清理已停用且无关联待办事项的计划
            val inactivePlans = planRepository.getAllPlans().first()
                .filter { !it.isActive }

            inactivePlans.forEach { plan ->
                val relatedTodos = todoRepository.getTodoItemsByPlanId(plan.id).first()
                if (relatedTodos.isEmpty()) {
                    planRepository.deletePlan(plan)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun validateDataIntegrity(): Result<List<String>> {
        return try {
            val issues = mutableListOf<String>()

            // 检查孤立的待办事项
            val allTodos = todoRepository.getAllTodoItems().first()
            val allPlans = planRepository.getAllPlans().first()
            val planIds = allPlans.map { it.id }.toSet()

            val orphanedTodos = allTodos.filter { it.planId !in planIds }
            if (orphanedTodos.isNotEmpty()) {
                issues.add("发现 ${orphanedTodos.size} 个孤立的待办事项")
                // 清理孤立的待办事项
                orphanedTodos.forEach { todoRepository.deleteTodoItem(it) }
            }

            // 检查过期的活跃计划
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val expiredActivePlans = allPlans.filter {
                it.isActive && !it.isRepeating && it.triggerTime < now
            }
            if (expiredActivePlans.isNotEmpty()) {
                issues.add("发现 ${expiredActivePlans.size} 个已过期但仍活跃的计划")
                // 停用过期的非重复计划
                expiredActivePlans.forEach { plan ->
                    planRepository.updatePlanActiveStatus(plan.id, false)
                }
            }

            Result.success(issues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}