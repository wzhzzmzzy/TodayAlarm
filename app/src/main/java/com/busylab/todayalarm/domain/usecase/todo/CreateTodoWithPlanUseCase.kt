package com.busylab.todayalarm.domain.usecase.todo

import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.coordinator.TodoPlanCoordinator
import javax.inject.Inject

/**
 * 创建带计划的Todo UseCase
 * 通过TodoPlanCoordinator协调Todo和Plan的创建，避免直接依赖其他UseCase
 */
class CreateTodoWithPlanUseCase @Inject constructor(
    private val todoPlanCoordinator: TodoPlanCoordinator
) {

    data class Params(
        val title: String,
        val content: String,
        val triggerTime: kotlinx.datetime.LocalDateTime,
        val enableRepeating: Boolean = false,
        val repeatType: RepeatType = RepeatType.NONE,
        val repeatInterval: Int = 1
    )

    suspend operator fun invoke(params: Params): Result<TodoItem> {
        return todoPlanCoordinator.createTodoWithPlan(
            title = params.title,
            content = params.content,
            triggerTime = params.triggerTime,
            enableRepeating = params.enableRepeating,
            repeatType = params.repeatType,
            repeatInterval = params.repeatInterval
        )
    }
}