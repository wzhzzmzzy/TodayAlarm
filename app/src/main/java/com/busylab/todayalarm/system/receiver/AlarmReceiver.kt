package com.busylab.todayalarm.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.system.alarm.AlarmSchedulerImpl
import com.busylab.todayalarm.system.notification.NotificationManager
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
 * 定时任务广播接收器
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var planRepository: PlanRepository

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var alarmScheduler: com.busylab.todayalarm.system.alarm.AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val planId = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_PLAN_ID) ?: return
        val planTitle = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_PLAN_TITLE) ?: return
        val planContent = intent.getStringExtra(AlarmSchedulerImpl.EXTRA_PLAN_CONTENT) ?: return
        val isRepeating = intent.getBooleanExtra(AlarmSchedulerImpl.EXTRA_IS_REPEATING, false)

        // 使用 goAsync() 处理异步操作
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                handleAlarmTrigger(planId, planTitle, planContent, isRepeating)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleAlarmTrigger(
        planId: String,
        title: String,
        content: String,
        isRepeating: Boolean
    ) {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        // 只显示通知，不自动创建 TodoItem
        notificationManager.showAlarmNotification(title, content, planId)

        // 处理重复提醒（保持现有逻辑）
        if (isRepeating) {
            val plan = planRepository.getPlanById(planId)
            plan?.let { planData ->
                val nextTriggerTime = calculateNextTriggerTime(planData)
                val updatedPlan = planData.copy(triggerTime = nextTriggerTime)

                // 更新数据库中的下次触发时间
                planRepository.updatePlan(updatedPlan)

                // 调度下次提醒
                alarmScheduler.scheduleAlarm(updatedPlan)
            }
        }
    }

    private fun calculateNextTriggerTime(plan: com.busylab.todayalarm.domain.model.Plan): kotlinx.datetime.LocalDateTime {
        val timeZone = TimeZone.currentSystemDefault()
        val instant = plan.triggerTime.toInstant(timeZone)

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
                instant // 不重复
        }

        return nextInstant.toLocalDateTime(timeZone)
    }
}