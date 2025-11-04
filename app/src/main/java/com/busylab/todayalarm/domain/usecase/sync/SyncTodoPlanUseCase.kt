package com.busylab.todayalarm.domain.usecase.sync

import com.busylab.todayalarm.domain.manager.SyncStatusManager
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.domain.usecase.todo.GenerateDailyTodosUseCase
import kotlinx.coroutines.flow.first
import kotlinx.datetime.*
import javax.inject.Inject

class SyncTodoPlanUseCase @Inject constructor(
    private val syncStatusManager: SyncStatusManager,
    private val generateDailyTodosUseCase: GenerateDailyTodosUseCase,
    private val planRepository: PlanRepository,
    private val todoRepository: TodoItemRepository
) {

    suspend operator fun invoke(): Result<SyncResult> {
        return try {
            syncStatusManager.setSyncing()

            // 1. 生成今日待办
            val generateResult = generateDailyTodosUseCase(GenerateDailyTodosUseCase.Params())
                .getOrElse { throwable ->
                    syncStatusManager.setError("生成待办失败: ${throwable.message}")
                    return Result.failure(throwable)
                }

            // 2. 清理过期的数据
            val cleanupResult = cleanupExpiredData()

            // 3. 统计数据
            val stats = getSyncStats()

            val result = SyncResult(
                generatedTodos = generateResult.createdCount,
                cleanedUpItems = cleanupResult,
                totalPlans = stats.totalPlans,
                totalTodos = stats.totalTodos,
                todayTodos = stats.todayTodos
            )

            syncStatusManager.setSuccess("同步完成: 生成 ${result.generatedTodos} 个待办")
            Result.success(result)
        } catch (e: Exception) {
            syncStatusManager.setError("同步失败: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun cleanupExpiredData(): Int {
        val timeZone = TimeZone.currentSystemDefault()
        val thirtyDaysAgo = Clock.System.now()
            .minus(30, DateTimeUnit.DAY, timeZone)

        // 清理30天前已完成的待办
        val completedTodos = todoRepository.getCompletedTodoItems().first()
        val expiredTodos = completedTodos.filter { todo ->
            todo.completedAt != null &&
            Instant.fromEpochMilliseconds(todo.completedAt!!) < thirtyDaysAgo
        }

        expiredTodos.forEach { todo ->
            todoRepository.deleteTodoItem(todo)
        }

        return expiredTodos.size
    }

    private suspend fun getSyncStats(): SyncStats {
        val timeZone = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(timeZone).date

        val totalPlans = planRepository.getAllPlans().first().size
        val totalTodos = todoRepository.getAllTodoItems().first().size
        val todayTodos = todoRepository.getTodoItemsByDate(
            today.atStartOfDayIn(timeZone).toEpochMilliseconds()
        ).first().size

        return SyncStats(totalPlans, totalTodos, todayTodos)
    }
}

data class SyncResult(
    val generatedTodos: Int,
    val cleanedUpItems: Int,
    val totalPlans: Int,
    val totalTodos: Int,
    val todayTodos: Int
)

data class SyncStats(
    val totalPlans: Int,
    val totalTodos: Int,
    val todayTodos: Int
)