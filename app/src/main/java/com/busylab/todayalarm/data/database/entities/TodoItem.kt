package com.busylab.todayalarm.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = Plan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["planId"]),
        Index(value = ["isCompleted"]),
        Index(value = ["triggerTime"]),
        Index(value = ["priority"]),
        Index(value = ["category"])
    ]
)
data class TodoItem(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // 关联信息
    val planId: String,

    // 基础信息
    val title: String,
    val content: String,
    val description: String? = null,

    // 时间信息
    val triggerTime: Long,
    val dueTime: Long? = null,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // 状态信息
    val status: String = TodoStatus.PENDING.name,
    val isCompleted: Boolean = false,
    val isArchived: Boolean = false,

    // 分类和优先级
    val priority: String = TodoPriority.NORMAL.name,
    val category: String = TodoCategory.GENERAL.name,
    val tags: String = "", // JSON格式存储标签列表

    // 提醒设置
    val reminderEnabled: Boolean = true,
    val reminderTime: Long? = null,
    val snoozeCount: Int = 0,
    val maxSnoozeCount: Int = 3,

    // 扩展信息
    val metadata: String = "{}", // JSON格式存储扩展数据
    val attachments: String = "[]" // JSON格式存储附件信息
)