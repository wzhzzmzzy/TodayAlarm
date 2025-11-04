package com.busylab.todayalarm.system.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.busylab.todayalarm.MainActivity
import com.busylab.todayalarm.R
import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.system.receiver.NotificationActionReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 通知管理器实现类
 */
@Singleton
class NotificationManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationManager {

    private val notificationManager = context.getSystemService<android.app.NotificationManager>()

    companion object {
        const val ALARM_CHANNEL_ID = "alarm_channel"
        const val REMINDER_CHANNEL_ID = "reminder_channel"
        const val EXTRA_TODO_ITEM_ID = "extra_todo_item_id"
        const val EXTRA_SHOW_TODO_DIALOG = "extra_show_todo_dialog"
        const val ACTION_COMPLETE_TODO = "action_complete_todo"
        const val ACTION_SNOOZE_TODO = "action_snooze_todo"
    }

    override fun showAlarmNotification(todoItem: TodoItem) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TODO_ITEM_ID, todoItem.id)
            putExtra(EXTRA_SHOW_TODO_DIALOG, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            todoItem.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val completeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_COMPLETE_TODO
            putExtra(EXTRA_TODO_ITEM_ID, todoItem.id)
        }

        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            todoItem.id.hashCode(),
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE_TODO
            putExtra(EXTRA_TODO_ITEM_ID, todoItem.id)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            todoItem.id.hashCode() + 1,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setContentTitle(todoItem.title)
            .setContentText(todoItem.content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "完成",
                completePendingIntent
            )
            .addAction(
                R.drawable.ic_launcher_foreground,
                "稍后提醒",
                snoozePendingIntent
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(todoItem.content)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager?.notify(todoItem.id.hashCode(), notification)
    }

    override fun showAlarmNotification(title: String, content: String, planId: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("action", "create_todo_from_notification")
            putExtra("plan_id", planId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            planId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager?.notify(planId.hashCode(), notification)
    }

    override fun showReminderNotification(plan: Plan) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            plan.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setContentTitle(plan.title)
            .setContentText(plan.content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(plan.content)
            )
            .build()

        notificationManager?.notify(plan.id.hashCode(), notification)
    }

    override fun cancelNotification(notificationId: Int) {
        notificationManager?.cancel(notificationId)
    }

    override fun updateNotificationProgress(notificationId: Int, progress: Int) {
        // 可以用于显示进度条类型的通知
        val notification = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
            .setContentTitle("任务进度")
            .setContentText("进度: $progress%")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setProgress(100, progress, false)
            .build()

        notificationManager?.notify(notificationId, notification)
    }
}