package com.busylab.todayalarm.system.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.busylab.todayalarm.domain.usecase.todo.GenerateDailyTodosUseCase
import javax.inject.Inject

class DailyTodoWorkerFactory @Inject constructor(
    private val generateDailyTodosUseCase: GenerateDailyTodosUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            DailyTodoWorker::class.java.name ->
                DailyTodoWorker(appContext, workerParameters, generateDailyTodosUseCase)
            else -> null
        }
    }
}