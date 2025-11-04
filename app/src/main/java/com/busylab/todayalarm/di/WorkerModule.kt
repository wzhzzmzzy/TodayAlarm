package com.busylab.todayalarm.di

import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import com.busylab.todayalarm.system.worker.AppWorkerFactory
import com.busylab.todayalarm.system.worker.DailyTodoWorker
import com.busylab.todayalarm.system.worker.DailyTodoWorkerFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    @Binds
    abstract fun bindAppWorkerFactory(factory: AppWorkerFactory): WorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(DailyTodoWorker::class)
    abstract fun bindDailyTodoWorkerFactory(factory: DailyTodoWorkerFactory): WorkerFactory
}