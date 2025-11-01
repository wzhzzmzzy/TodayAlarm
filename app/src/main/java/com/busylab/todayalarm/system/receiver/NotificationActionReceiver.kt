package com.busylab.todayalarm.system.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.busylab.todayalarm.domain.repository.TodoRepository
import com.busylab.todayalarm.system.notification.NotificationManagerImpl
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
 * 通知操作广播接收器
 */
@AndroidEntryPoint
class NotificationActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var todoRepository: TodoRepository

    @Inject
    lateinit var notificationManager: com.busylab.todayalarm.system.notification.NotificationManager

    @Inject
    lateinit var alarmScheduler: com.busylab.todayalarm.system.alarm.AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val todoItemId = intent.getStringExtra(NotificationManagerImpl.EXTRA_TODO_ITEM_ID) ?: return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    NotificationManagerImpl.ACTION_COMPLETE_TODO -> {
                        handleCompleteTodo(todoItemId)
                    }
                    NotificationManagerImpl.ACTION_SNOOZE_TODO -> {
                        handleSnoozeTodo(todoItemId)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private suspend fun handleCompleteTodo(todoItemId: String) {
        val todoItem = todoRepository.getTodoItemById(todoItemId)
        todoItem?.let { todo ->
            val completedTodo = todo.copy(
                isCompleted = true,
                completedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            )
            todoRepository.updateTodoItem(completedTodo)

            // 取消通知
            notificationManager.cancelNotification(todoItemId.hashCode())
        }
    }

    private suspend fun handleSnoozeTodo(todoItemId: String) {
        val todoItem = todoRepository.getTodoItemById(todoItemId)
        todoItem?.let { todo ->
            // 延迟5分钟后再次提醒
            val timeZone = TimeZone.currentSystemDefault()
            val instant = todo.triggerTime.toInstant(timeZone)
            val snoozeInstant = instant.plus(5, DateTimeUnit.MINUTE)
            val snoozeTime = snoozeInstant.toLocalDateTime(timeZone)

            val snoozedTodo = todo.copy(triggerTime = snoozeTime)
            todoRepository.updateTodoItem(snoozedTodo)

            // 取消当前通知
            notificationManager.cancelNotification(todoItemId.hashCode())

            // 重新调度提醒（这里需要创建一个临时的Plan对象）
            // 注意：这是一个简化实现，实际项目中可能需要更复杂的逻辑
        }
    }
}