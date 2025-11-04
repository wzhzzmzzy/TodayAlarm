package com.busylab.todayalarm.system.notification

import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.model.TodoItem

/**
 * 通知管理器接口
 */
interface NotificationManager {
    /**
     * 显示闹钟通知
     */
    fun showAlarmNotification(todoItem: TodoItem)

    /**
     * 显示闹钟通知（使用计划信息）
     */
    fun showAlarmNotification(title: String, content: String, planId: String)

    /**
     * 显示提醒通知
     */
    fun showReminderNotification(plan: Plan)

    /**
     * 取消通知
     */
    fun cancelNotification(notificationId: Int)

    /**
     * 更新通知进度
     */
    fun updateNotificationProgress(notificationId: Int, progress: Int)
}