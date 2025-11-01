package com.busylab.todayalarm.di

import com.busylab.todayalarm.system.alarm.AlarmScheduler
import com.busylab.todayalarm.system.alarm.AlarmSchedulerImpl
import com.busylab.todayalarm.system.notification.NotificationManager
import com.busylab.todayalarm.system.notification.NotificationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 系统集成模块
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SystemModule {

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        alarmSchedulerImpl: AlarmSchedulerImpl
    ): AlarmScheduler

    @Binds
    @Singleton
    abstract fun bindNotificationManager(
        notificationManagerImpl: NotificationManagerImpl
    ): NotificationManager
}