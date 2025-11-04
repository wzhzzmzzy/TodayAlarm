package com.busylab.todayalarm.domain.usecase.plan

import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.UUID
import javax.inject.Inject

class CreatePlanFromTodoUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val alarmScheduler: AlarmScheduler
) {

    data class Params(
        val todoItem: TodoItem
    )

    suspend operator fun invoke(params: Params): Result<Plan> {
        return try {
            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

            // 创建对应的 Plan
            val plan = Plan(
                id = UUID.randomUUID().toString(),
                title = params.todoItem.title,
                content = params.todoItem.content,
                triggerTime = params.todoItem.triggerTime,
                isRepeating = params.todoItem.enableRepeating,
                repeatType = params.todoItem.repeatType,
                repeatInterval = params.todoItem.repeatInterval,
                isActive = params.todoItem.isActive,
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
}