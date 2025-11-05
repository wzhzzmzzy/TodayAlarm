package com.busylab.todayalarm.data.database.converters

import androidx.room.TypeConverter
import com.busylab.todayalarm.data.database.entities.RepeatType
import com.busylab.todayalarm.data.database.entities.TodoStatus
import com.busylab.todayalarm.data.database.entities.TodoPriority
import com.busylab.todayalarm.data.database.entities.TodoCategory
import com.busylab.todayalarm.data.database.entities.TodoTag
import com.busylab.todayalarm.data.database.entities.TodoAttachment
import com.busylab.todayalarm.data.database.entities.TodoMetadata
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/**
 * 统一的数据库类型转换器
 * 合并了原来的 Converters 和 TodoConverters 功能
 */
class DatabaseConverters {

    private val json = Json { ignoreUnknownKeys = true }

    // ==================== RepeatType转换 ====================

    @TypeConverter
    fun fromRepeatType(repeatType: RepeatType): String {
        return repeatType.name
    }

    @TypeConverter
    fun toRepeatType(repeatType: String): RepeatType {
        return try {
            RepeatType.valueOf(repeatType)
        } catch (e: IllegalArgumentException) {
            RepeatType.NONE
        }
    }

    // ==================== TodoStatus转换 ====================

    @TypeConverter
    fun fromTodoStatus(status: TodoStatus): String = status.name

    @TypeConverter
    fun toTodoStatus(status: String): TodoStatus =
        try {
            TodoStatus.valueOf(status)
        } catch (e: IllegalArgumentException) {
            TodoStatus.PENDING
        }

    // ==================== TodoPriority转换 ====================

    @TypeConverter
    fun fromTodoPriority(priority: TodoPriority): String = priority.name

    @TypeConverter
    fun toTodoPriority(priority: String): TodoPriority =
        try {
            TodoPriority.valueOf(priority)
        } catch (e: IllegalArgumentException) {
            TodoPriority.NORMAL
        }

    // ==================== TodoCategory转换 ====================

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