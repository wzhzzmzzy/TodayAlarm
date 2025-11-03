package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.domain.repository.PlanRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SyncTodoWithPlanUseCase @Inject constructor(
    private val todoRepository: TodoItemRepository,
    private val planRepository: PlanRepository
) {

    data class Params(
        val planId: String,
        val triggerTime: Long
    )

    suspend operator fun invoke(params: Params): Result<TodoItem> {
        return try {
            // 获取计划信息 (这里需要从 data 层获取实体)
            // TODO: 需要添加方法来获取数据实体而不是领域模型
            // val plan = planRepository.getPlanById(params.planId)
            //     ?: throw IllegalArgumentException("计划不存在")

            // 检查是否已存在相同触发时间的待办
            val existingTodos = todoRepository.getTodoItemsByPlanId(params.planId).first()
            val existingTodo = existingTodos.find { it.triggerTime == params.triggerTime }

            if (existingTodo != null) {
                return Result.success(existingTodo)
            }

            // 暂时跳过计划验证，直接创建待办
            throw UnsupportedOperationException("需要实现获取 Plan 实体的方法")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}