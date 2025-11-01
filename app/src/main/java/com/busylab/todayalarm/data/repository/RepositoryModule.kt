package com.busylab.todayalarm.data.repository

import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.domain.repository.TodoItemRepository
import com.busylab.todayalarm.domain.repository.TodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPlanRepository(
        planRepositoryImpl: PlanRepositoryImpl
    ): PlanRepository

    @Binds
    abstract fun bindTodoItemRepository(
        todoItemRepositoryImpl: TodoItemRepositoryImpl
    ): TodoItemRepository

    @Binds
    abstract fun bindTodoRepository(
        todoRepositoryImpl: TodoRepositoryImpl
    ): TodoRepository
}