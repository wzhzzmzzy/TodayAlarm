package com.busylab.todayalarm.system.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

/**
 * 数据同步和清理工作器
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val todoRepository: TodoRepository,
    private val planRepository: PlanRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 清理过期的已完成待办事项
            cleanupCompletedTodos()

            // 检查并更新过期的计划
            updateExpiredPlans()

            // 清理过期的非重复计划
            cleanupExpiredPlans()

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    /**
     * 清理30天前的已完成待办事项
     */
    private suspend fun cleanupCompletedTodos() {
        val cutoffTime = Clock.System.now()
            .minus(30, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())

        todoRepository.deleteCompletedTodosBefore(cutoffTime)
    }

    /**
     * 更新过期的计划状态
     */
    private suspend fun updateExpiredPlans() {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val expiredPlans = planRepository.getExpiredPlans(currentTime)

        expiredPlans.forEach { plan ->
            if (!plan.isRepeating) {
                // 非重复计划标记为非活跃
                val inactivePlan = plan.copy(isActive = false)
                planRepository.updatePlan(inactivePlan)
            }
        }
    }

    /**
     * 清理过期的非重复计划
     */
    private suspend fun cleanupExpiredPlans() {
        val cutoffTime = Clock.System.now()
            .minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
            .toLocalDateTime(TimeZone.currentSystemDefault())

        planRepository.deleteInactivePlansBefore(cutoffTime)
    }
}