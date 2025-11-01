package com.busylab.todayalarm.system.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService
import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.system.receiver.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 定时任务调度器实现类
 */
@Singleton
class AlarmSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService<AlarmManager>()

    companion object {
        const val EXTRA_PLAN_ID = "extra_plan_id"
        const val EXTRA_PLAN_TITLE = "extra_plan_title"
        const val EXTRA_PLAN_CONTENT = "extra_plan_content"
        const val EXTRA_IS_REPEATING = "extra_is_repeating"
    }

    override fun scheduleAlarm(plan: Plan) {
        val intent = createAlarmIntent(plan)
        val pendingIntent = createPendingIntent(plan.id, intent)

        val triggerTime = plan.triggerTime.toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (alarmManager?.canScheduleExactAlarms() == true) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    // 如果没有精确闹钟权限，使用普通闹钟
                    alarmManager?.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
            else -> {
                alarmManager?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    }

    override fun cancelAlarm(planId: String) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            planId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            alarmManager?.cancel(it)
            it.cancel()
        }
    }

    override fun rescheduleAlarm(plan: Plan) {
        cancelAlarm(plan.id)
        scheduleAlarm(plan)
    }

    override fun scheduleRepeatingAlarm(plan: Plan) {
        // 重复提醒通过单次提醒 + 重新调度实现
        scheduleAlarm(plan)
    }

    private fun createAlarmIntent(plan: Plan): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_PLAN_ID, plan.id)
            putExtra(EXTRA_PLAN_TITLE, plan.title)
            putExtra(EXTRA_PLAN_CONTENT, plan.content)
            putExtra(EXTRA_IS_REPEATING, plan.isRepeating)
        }
    }

    private fun createPendingIntent(planId: String, intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            planId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}