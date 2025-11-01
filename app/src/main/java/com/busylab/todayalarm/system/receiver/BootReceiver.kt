package com.busylab.todayalarm.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

/**
 * 系统启动广播接收器
 * 处理系统重启后的闹钟恢复
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var planRepository: PlanRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_PACKAGE_REPLACED -> {
                // 检查是否是本应用的包替换
                if (intent.action == Intent.ACTION_PACKAGE_REPLACED) {
                    val packageName = intent.dataString
                    if (packageName != "package:${context.packageName}") {
                        return
                    }
                }

                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        restoreAlarms()
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }

    private suspend fun restoreAlarms() {
        // 获取所有活跃的计划
        val activePlans = planRepository.getActiveActivePlans()
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        activePlans.forEach { plan ->
            when {
                // 未来的提醒，直接恢复
                plan.triggerTime > currentTime -> {
                    alarmScheduler.scheduleAlarm(plan)
                }
                // 过期的重复提醒，计算下次触发时间
                plan.isRepeating && plan.triggerTime <= currentTime -> {
                    val nextTriggerTime = calculateNextValidTriggerTime(plan, currentTime)
                    val updatedPlan = plan.copy(triggerTime = nextTriggerTime)
                    planRepository.updatePlan(updatedPlan)
                    alarmScheduler.scheduleAlarm(updatedPlan)
                }
                // 过期的单次提醒，标记为非活跃
                !plan.isRepeating && plan.triggerTime <= currentTime -> {
                    val inactivePlan = plan.copy(isActive = false)
                    planRepository.updatePlan(inactivePlan)
                }
            }
        }
    }

    private fun calculateNextValidTriggerTime(
        plan: com.busylab.todayalarm.domain.model.Plan,
        currentTime: kotlinx.datetime.LocalDateTime
    ): kotlinx.datetime.LocalDateTime {
        val timeZone = TimeZone.currentSystemDefault()
        var nextTime = plan.triggerTime

        while (nextTime <= currentTime) {
            val instant = nextTime.toInstant(timeZone)
            val nextInstant = when (plan.repeatType) {
                com.busylab.todayalarm.domain.model.RepeatType.DAILY ->
                    instant.plus(1, DateTimeUnit.DAY, timeZone)
                com.busylab.todayalarm.domain.model.RepeatType.WEEKLY ->
                    instant.plus(7, DateTimeUnit.DAY, timeZone)
                com.busylab.todayalarm.domain.model.RepeatType.MONTHLY ->
                    instant.plus(1, DateTimeUnit.MONTH, timeZone)
                com.busylab.todayalarm.domain.model.RepeatType.YEARLY ->
                    instant.plus(1, DateTimeUnit.YEAR, timeZone)
                com.busylab.todayalarm.domain.model.RepeatType.NONE ->
                    break // 不重复，跳出循环
            }
            nextTime = nextInstant.toLocalDateTime(timeZone)
        }

        return nextTime
    }
}