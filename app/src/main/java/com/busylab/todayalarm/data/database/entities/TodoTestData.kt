package com.busylab.todayalarm.data.database.entities

object TodoTestData {

    fun createSampleTodoItems(): List<TodoItem> {
        val now = System.currentTimeMillis()
        val oneHour = 60 * 60 * 1000L
        val oneDay = 24 * oneHour

        return listOf(
            // 今天的待办
            TodoItem(
                planId = "plan1",
                title = "晨练",
                content = "去公园跑步30分钟",
                triggerTime = now + oneHour,
                priority = TodoPriority.HIGH.name,
                category = TodoCategory.HEALTH.name,
                tags = """[{"id":"1","name":"运动","color":"#FF5722"}]""",
                metadata = """{"estimatedDuration":30}"""
            ),

            // 已完成的待办
            TodoItem(
                planId = "plan2",
                title = "项目会议",
                content = "讨论新功能开发计划",
                triggerTime = now - oneHour,
                isCompleted = true,
                completedAt = now - 30 * 60 * 1000,
                priority = TodoPriority.URGENT.name,
                category = TodoCategory.WORK.name,
                status = TodoStatus.COMPLETED.name
            ),

            // 过期的待办
            TodoItem(
                planId = "plan3",
                title = "买菜",
                content = "购买周末聚餐的食材",
                triggerTime = now - oneDay,
                priority = TodoPriority.NORMAL.name,
                category = TodoCategory.SHOPPING.name,
                status = TodoStatus.OVERDUE.name
            ),

            // 推迟的待办
            TodoItem(
                planId = "plan4",
                title = "学习新技术",
                content = "阅读Kotlin协程相关文档",
                triggerTime = now + 2 * oneHour,
                priority = TodoPriority.LOW.name,
                category = TodoCategory.STUDY.name,
                status = TodoStatus.SNOOZED.name,
                snoozeCount = 1,
                reminderTime = now + 3 * oneHour
            )
        )
    }

    fun createMockStatistics(): TodoStatistics {
        return TodoStatistics(
            totalCount = 50,
            completedCount = 35,
            pendingCount = 10,
            overdueCount = 5,
            completionRate = 0.7f,
            averageCompletionTime = 2 * 60 * 60 * 1000L, // 2小时
            categoryDistribution = mapOf(
                TodoCategory.WORK to 20,
                TodoCategory.PERSONAL to 15,
                TodoCategory.HEALTH to 8,
                TodoCategory.STUDY to 5,
                TodoCategory.SHOPPING to 2
            ),
            priorityDistribution = mapOf(
                TodoPriority.LOW to 15,
                TodoPriority.NORMAL to 25,
                TodoPriority.HIGH to 8,
                TodoPriority.URGENT to 2
            )
        )
    }
}