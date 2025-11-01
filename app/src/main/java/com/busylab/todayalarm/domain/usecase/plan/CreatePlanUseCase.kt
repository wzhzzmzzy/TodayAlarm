package com.busylab.todayalarm.domain.usecase.plan

import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import kotlinx.datetime.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatePlanUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(
        title: String,
        content: String,
        triggerTime: LocalDateTime,
        isRepeating: Boolean = false,
        repeatType: RepeatType = RepeatType.NONE,
        repeatInterval: Int = 1
    ): Result<String> {
        return try {
            // 验证输入
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("标题不能为空"))
            }
            if (content.isBlank()) {
                return Result.failure(IllegalArgumentException("内容不能为空"))
            }
            if (triggerTime <= Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) {
                return Result.failure(IllegalArgumentException("提醒时间必须在未来"))
            }

            val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val plan = Plan(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                content = content.trim(),
                triggerTime = triggerTime,
                isRepeating = isRepeating,
                repeatType = repeatType,
                repeatInterval = if (isRepeating) repeatInterval else 1,
                isActive = true,
                createdAt = currentTime,
                updatedAt = currentTime
            )

            // 保存并调度
            planRepository.insertPlan(plan)
            alarmScheduler.scheduleAlarm(plan)

            Result.success(plan.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}