package com.busylab.todayalarm.data.database.entities

object TodoItemFactory {

    fun createFromPlan(plan: Plan, triggerTime: Long = plan.triggerTime): TodoItem {
        return TodoItem(
            planId = plan.id,
            title = plan.title,
            content = plan.content,
            triggerTime = triggerTime,
            priority = inferPriorityFromContent(plan.content),
            category = inferCategoryFromContent(plan.content),
            reminderEnabled = true,
            reminderTime = triggerTime
        )
    }

    fun createQuickTodo(
        title: String,
        content: String = "",
        triggerTime: Long = System.currentTimeMillis(),
        priority: TodoPriority = TodoPriority.NORMAL,
        category: TodoCategory = TodoCategory.GENERAL
    ): TodoItem {
        return TodoItem(
            planId = "", // 快速创建的待办不关联计划
            title = title,
            content = content,
            triggerTime = triggerTime,
            priority = priority.name,
            category = category.name
        )
    }

    private fun inferPriorityFromContent(content: String): String {
        val urgentKeywords = listOf("紧急", "urgent", "asap", "立即", "马上")
        val highKeywords = listOf("重要", "important", "关键", "key")

        return when {
            urgentKeywords.any { content.contains(it, ignoreCase = true) } -> TodoPriority.URGENT.name
            highKeywords.any { content.contains(it, ignoreCase = true) } -> TodoPriority.HIGH.name
            else -> TodoPriority.NORMAL.name
        }
    }

    private fun inferCategoryFromContent(content: String): String {
        val workKeywords = listOf("工作", "会议", "项目", "work", "meeting", "project")
        val healthKeywords = listOf("运动", "健身", "医院", "体检", "health", "exercise")
        val studyKeywords = listOf("学习", "考试", "作业", "study", "exam", "homework")
        val shoppingKeywords = listOf("购物", "买", "shopping", "buy")

        return when {
            workKeywords.any { content.contains(it, ignoreCase = true) } -> TodoCategory.WORK.name
            healthKeywords.any { content.contains(it, ignoreCase = true) } -> TodoCategory.HEALTH.name
            studyKeywords.any { content.contains(it, ignoreCase = true) } -> TodoCategory.STUDY.name
            shoppingKeywords.any { content.contains(it, ignoreCase = true) } -> TodoCategory.SHOPPING.name
            else -> TodoCategory.GENERAL.name
        }
    }
}