package com.busylab.todayalarm.domain.coordinator

import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.repository.TodoRepository
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Todo和Plan协调器
 * 负责协调Todo和Plan的创建逻辑，避免UseCase之间的直接依赖
 */
@Singleton
class TodoPlanCoordinator @Inject constructor(
    private val todoRepository: TodoRepository,
    private val planRepository: PlanRepository,
    private val alarmScheduler: AlarmScheduler
) {

    /**
     * 创建带计划的Todo
     * 同时创建TodoItem和对应的Plan，并建立关联
     */
    suspend fun createTodoWithPlan(
        title: String,
        content: String,
        triggerTime: kotlinx.datetime.LocalDateTime,
        enableRepeating: Boolean = false,
        repeatType: RepeatType = RepeatType.NONE,
        repeatInterval: Int = 1
    ): Result<TodoItem> {
        return try {
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            // 创建 TodoItem
            val todoItem = TodoItem(
                id = UUID.randomUUID().toString(),
                planId = null, // 初始为 null
                title = title,
                content = content,
                isCompleted = false,
                triggerTime = triggerTime,
                completedAt = null,
                createdAt = currentTime,
                enableRepeating = enableRepeating,
                repeatType = repeatType,
                repeatInterval = repeatInterval,
                isActive = true
            )

            // 保存 TodoItem
            todoRepository.insertTodoItem(todoItem)

            // 如果启用重复，创建对应的 Plan
            if (enableRepeating) {
                createPlanForTodo(todoItem)
                    .onSuccess { plan ->
                        // 更新 TodoItem 的 planId
                        val updatedTodoItem = todoItem.copy(planId = plan.id)
                        todoRepository.updateTodoItem(updatedTodoItem)
                    }
                    .onFailure { e ->
                        // 记录错误但不阻止 TodoItem 创建
                        e.printStackTrace()
                    }
            }

            Result.success(todoItem)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 为Todo创建对应的Plan
     */
    private suspend fun createPlanForTodo(todoItem: TodoItem): Result<Plan> {
        return try {
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            // 创建对应的 Plan
            val plan = Plan(
                id = UUID.randomUUID().toString(),
                title = todoItem.title,
                content = todoItem.content,
                triggerTime = todoItem.triggerTime,
                isRepeating = todoItem.enableRepeating,
                repeatType = todoItem.repeatType,
                repeatInterval = todoItem.repeatInterval,
                isActive = todoItem.isActive,
                createdAt = currentTime,
                updatedAt = currentTime
            )

            // 保存 Plan
            planRepository.insertPlan(plan)

            // 调度提醒
            alarmScheduler.scheduleAlarm(plan)

            Result.success(plan)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 从Todo创建Plan
     * 独立方法，用于将现有Todo转换为Plan
     */
    suspend fun createPlanFromTodo(todoItem: TodoItem): Result<Plan> {
        return createPlanForTodo(todoItem)
    }
}