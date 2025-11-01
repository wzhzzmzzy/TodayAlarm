package com.busylab.todayalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import com.busylab.todayalarm.system.work.WorkScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TodayAlarmApplication : Application() {

    @Inject
    lateinit var workScheduler: WorkScheduler

    companion object {
        const val ALARM_CHANNEL_ID = "alarm_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val SYSTEM_CHANNEL_ID = "system_channel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        initializeWorkManager()
    }

    private fun initializeWorkManager() {
        // 调度定期同步任务
        workScheduler.schedulePeriodicSync()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // 闹钟通知渠道
            val alarmChannel = NotificationChannel(
                ALARM_CHANNEL_ID,
                "定时提醒",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "定时提醒通知"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setShowBadge(true)
            }

            // 一般提醒通知渠道
            val reminderChannel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                "一般提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "一般提醒通知"
                enableVibration(true)
                setShowBadge(true)
            }

            // 系统通知渠道
            val systemChannel = NotificationChannel(
                SYSTEM_CHANNEL_ID,
                "系统通知",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "系统状态通知"
                setShowBadge(false)
            }

            notificationManager.createNotificationChannels(
                listOf(alarmChannel, reminderChannel, systemChannel)
            )
        }
    }
}