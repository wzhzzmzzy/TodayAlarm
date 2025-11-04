package com.busylab.todayalarm.data.database

import android.content.Context
import androidx.room.Room
import com.busylab.todayalarm.data.database.dao.PlanDao
import com.busylab.todayalarm.data.database.dao.TodoItemDao
import com.busylab.todayalarm.data.database.migration.TodoMigrations
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
            .addMigrations(TodoMigrations.MIGRATION_1_2)
            .fallbackToDestructiveMigration(true) // 开发阶段允许销毁重建
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