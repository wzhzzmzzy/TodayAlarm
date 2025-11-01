package com.busylab.todayalarm.data.database

import android.content.Context
import androidx.room.Room
import com.busylab.todayalarm.data.database.dao.PlanDao
import com.busylab.todayalarm.data.database.dao.TodoItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // 开发阶段使用，生产环境需要提供迁移策略
            .build()
    }

    @Provides
    fun providePlanDao(database: AppDatabase): PlanDao {
        return database.planDao()
    }

    @Provides
    fun provideTodoItemDao(database: AppDatabase): TodoItemDao {
        return database.todoItemDao()
    }
}