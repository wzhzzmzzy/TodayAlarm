package com.busylab.todayalarm.system.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.busylab.todayalarm.domain.usecase.todo.GenerateDailyTodosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DailyTodoWorker constructor(
    context: Context,
    params: WorkerParameters,
    private val generateDailyTodosUseCase: GenerateDailyTodosUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val result = generateDailyTodosUseCase(GenerateDailyTodosUseCase.Params())

            result.fold(
                onSuccess = { generateResult ->
                    // 记录生成结果
                    android.util.Log.d(
                        "DailyTodoWorker",
                        "生成了 ${generateResult.createdCount} 个待办，跳过了 ${generateResult.skippedCount} 个"
                    )
                    Result.success()
                },
                onFailure = { e ->
                    android.util.Log.e(
                        "DailyTodoWorker",
                        "生成待办失败: ${e.message}",
                        e
                    )
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            android.util.Log.e(
                "DailyTodoWorker",
                "生成待办异常: ${e.message}",
                e
            )
            Result.failure()
        }
    }
}