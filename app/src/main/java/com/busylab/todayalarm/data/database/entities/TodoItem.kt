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
    indices = [Index(value = ["planId"])]
)
data class TodoItem(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val title: String,
    val content: String,
    val isCompleted: Boolean = false,
    val triggerTime: Long,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)