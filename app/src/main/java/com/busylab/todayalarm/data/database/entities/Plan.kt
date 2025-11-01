package com.busylab.todayalarm.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "plans")
data class Plan(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val triggerTime: Long, // 使用时间戳存储
    val isRepeating: Boolean = false,
    val repeatType: String = RepeatType.NONE.name,
    val repeatInterval: Int = 1, // 重复间隔
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)