package com.busylab.todayalarm.di

import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoRepository
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.domain.usecase.plan.CreatePlanFromTodoUseCase
import com.busylab.todayalarm.domain.coordinator.TodoPlanCoordinator
import com.busylab.todayalarm.domain.usecase.todo.CreateTodoFromNotificationUseCase
import com.busylab.todayalarm.domain.usecase.todo.CreateTodoWithPlanUseCase
import com.busylab.todayalarm.domain.usecase.todo.GenerateDailyTodosUseCase
import com.busylab.todayalarm.domain.usecase.todo.GetTodoItemByIdUseCase
import com.busylab.todayalarm.domain.usecase.todo.UpdateTodoItemUseCase
import com.busylab.todayalarm.domain.usecase.sync.SyncTodoPlanUseCase
import com.busylab.todayalarm.domain.manager.SyncStatusManager
import com.busylab.todayalarm.domain.handler.ErrorHandler
import com.busylab.todayalarm.system.log.AppLogger
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * UseCase 依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideCreateTodoFromNotificationUseCase(
        todoRepository: TodoRepository,
        planRepository: PlanRepository
    ): CreateTodoFromNotificationUseCase {
        return CreateTodoFromNotificationUseCase(todoRepository, planRepository)
    }

    @Provides
    @Singleton
    fun provideCreatePlanFromTodoUseCase(
        planRepository: PlanRepository,
        alarmScheduler: AlarmScheduler
    ): CreatePlanFromTodoUseCase {
        return CreatePlanFromTodoUseCase(planRepository, alarmScheduler)
    }

    @Provides
    @Singleton
    fun provideTodoPlanCoordinator(
        todoRepository: TodoRepository,
        planRepository: PlanRepository,
        alarmScheduler: AlarmScheduler
    ): TodoPlanCoordinator {
        return TodoPlanCoordinator(todoRepository, planRepository, alarmScheduler)
    }

    @Provides
    @Singleton
    fun provideCreateTodoWithPlanUseCase(
        todoPlanCoordinator: TodoPlanCoordinator
    ): CreateTodoWithPlanUseCase {
        return CreateTodoWithPlanUseCase(todoPlanCoordinator)
    }

    @Provides
    @Singleton
    fun provideGenerateDailyTodosUseCase(
        planRepository: PlanRepository,
        todoItemRepository: TodoItemRepository
    ): GenerateDailyTodosUseCase {
        return GenerateDailyTodosUseCase(planRepository, todoItemRepository)
    }

    @Provides
    @Singleton
    fun provideSyncTodoPlanUseCase(
        syncStatusManager: SyncStatusManager,
        generateDailyTodosUseCase: GenerateDailyTodosUseCase,
        planRepository: PlanRepository,
        todoItemRepository: TodoItemRepository
    ): SyncTodoPlanUseCase {
        return SyncTodoPlanUseCase(
            syncStatusManager,
            generateDailyTodosUseCase,
            planRepository,
            todoItemRepository
        )
    }

    @Provides
    @Singleton
    fun provideGetTodoItemByIdUseCase(
        todoItemRepository: TodoItemRepository
    ): GetTodoItemByIdUseCase {
        return GetTodoItemByIdUseCase(todoItemRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateTodoItemUseCase(
        todoItemRepository: TodoItemRepository
    ): UpdateTodoItemUseCase {
        return UpdateTodoItemUseCase(todoItemRepository)
    }
}