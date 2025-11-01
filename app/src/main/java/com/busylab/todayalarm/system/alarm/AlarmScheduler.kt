package com.busylab.todayalarm.system.alarm

import com.busylab.todayalarm.domain.model.Plan

/**
 * 定时任务调度器接口
 */
interface AlarmScheduler {
    /**
     * 调度单次提醒
     */
    fun scheduleAlarm(plan: Plan)

    /**
     * 取消提醒
     */
    fun cancelAlarm(planId: String)

    /**
     * 重新调度提醒
     */
    fun rescheduleAlarm(plan: Plan)

    /**
     * 调度重复提醒
     */
    fun scheduleRepeatingAlarm(plan: Plan)
}