package com.busylab.todayalarm.data.database.converters

import androidx.room.TypeConverter
import com.busylab.todayalarm.data.database.entities.TodoStatus
import com.busylab.todayalarm.data.database.entities.TodoPriority
import com.busylab.todayalarm.data.database.entities.TodoCategory
import com.busylab.todayalarm.data.database.entities.TodoTag
import com.busylab.todayalarm.data.database.entities.TodoAttachment
import com.busylab.todayalarm.data.database.entities.TodoMetadata
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class TodoConverters {

    private val json = Json { ignoreUnknownKeys = true }

    // ==================== 枚举转换 ====================

    @TypeConverter
    fun fromTodoStatus(status: TodoStatus): String = status.name

    @TypeConverter
    fun toTodoStatus(status: String): TodoStatus =
        try {
            TodoStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            TodoStatus.PENDING
        }

    @TypeConverter
    fun fromTodoPriority(priority: TodoPriority): String = priority.name

    @TypeConverter
    fun toTodoPriority(priority: String): TodoPriority =
        try {
            TodoPriority.valueOf(priority)
        } catch (e: IllegalArgumentException) {
            TodoPriority.NORMAL
        }

    @TypeConverter
    fun fromTodoCategory(category: TodoCategory): String = category.name

    @TypeConverter
    fun toTodoCategory(category: String): TodoCategory =
        try {
            TodoCategory.valueOf(category)
        } catch (e: IllegalArgumentException) {
            TodoCategory.GENERAL
        }

    // ==================== JSON转换 ====================

    @TypeConverter
    fun fromTodoTagList(tags: List<TodoTag>): String =
        json.encodeToString(tags)

    @TypeConverter
    fun toTodoTagList(tagsJson: String): List<TodoTag> =
        try {
            json.decodeFromString(tagsJson)
        } catch (e: Exception) {
            emptyList()
        }

    @TypeConverter
    fun fromTodoAttachmentList(attachments: List<TodoAttachment>): String =
        json.encodeToString(attachments)

    @TypeConverter
    fun toTodoAttachmentList(attachmentsJson: String): List<TodoAttachment> =
        try {
            json.decodeFromString(attachmentsJson)
        } catch (e: Exception) {
            emptyList()
        }

    @TypeConverter
    fun fromTodoMetadata(metadata: TodoMetadata): String =
        json.encodeToString(metadata)

    @TypeConverter
    fun toTodoMetadata(metadataJson: String): TodoMetadata =
        try {
            json.decodeFromString(metadataJson)
        } catch (e: Exception) {
            TodoMetadata()
        }
}