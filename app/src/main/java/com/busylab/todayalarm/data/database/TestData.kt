package com.busylab.todayalarm.data.database

import com.busylab.todayalarm.data.database.entities.Plan
import com.busylab.todayalarm.data.database.entities.RepeatType
import com.busylab.todayalarm.data.database.entities.TodoItem

object TestData {

    fun createSamplePlans(): List<Plan> {
        val now = System.currentTimeMillis()
        return listOf(
            Plan(
                title = "晨练提醒",
                content = "记得去公园跑步",
                triggerTime = now + 3600000, // 1小时后
                isRepeating = true,
                repeatType = RepeatType.DAILY.name
            ),
            Plan(
                title = "会议提醒",
                content = "下午2点项目会议",
                triggerTime = now + 7200000, // 2小时后
                isRepeating = false
            ),
            Plan(
                title = "买菜提醒",
                content = "下班路上买菜",
                triggerTime = now + 28800000, // 8小时后
                isRepeating = true,
                repeatType = RepeatType.WEEKLY.name
            )
        )
    }

    fun createSampleTodoItems(planIds: List<String>): List<TodoItem> {
        val now = System.currentTimeMillis()
        return listOf(
            TodoItem(
                planId = planIds[0],
                title = "晨练提醒",
                content = "记得去公园跑步",
                triggerTime = now - 3600000, // 1小时前
                isCompleted = true,
                completedAt = now - 1800000 // 30分钟前完成
            ),
            TodoItem(
                planId = planIds[1],
                title = "会议提醒",
                content = "下午2点项目会议",
                triggerTime = now + 3600000 // 1小时后
            )
        )
    }
}