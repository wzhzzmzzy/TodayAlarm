package com.busylab.todayalarm.di

import com.busylab.todayalarm.domain.handler.ErrorHandler
import com.busylab.todayalarm.domain.manager.SyncStatusManager
import com.busylab.todayalarm.system.log.AppLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule {

    @Provides
    @Singleton
    fun provideSyncStatusManager(): SyncStatusManager {
        return SyncStatusManager()
    }

    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }

    @Provides
    @Singleton
    fun provideAppLogger(): AppLogger {
        return AppLogger()
    }
}