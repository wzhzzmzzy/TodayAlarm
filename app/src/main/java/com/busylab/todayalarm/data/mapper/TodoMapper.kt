package com.busylab.todayalarm.data.mapper

import com.busylab.todayalarm.data.database.entities.TodoItem as EntityTodoItem
import com.busylab.todayalarm.domain.model.TodoItem as DomainTodoItem
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * TodoItem实体与领域模型转换器
 */
object TodoMapper {

    /**
     * 数据库实体转换为领域模型
     */
    fun EntityTodoItem.toDomainModel(): DomainTodoItem {
        val timeZone = TimeZone.currentSystemDefault()
        return DomainTodoItem(
            id = id,
            planId = planId,
            title = title,
            content = content,
            isCompleted = isCompleted,
            triggerTime = Instant.fromEpochMilliseconds(triggerTime).toLocalDateTime(timeZone),
            completedAt = completedAt?.let { Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone) },
            createdAt = Instant.fromEpochMilliseconds(createdAt).toLocalDateTime(timeZone)
        )
    }

    /**
     * 领域模型转换为数据库实体
     */
    fun DomainTodoItem.toEntityModel(): EntityTodoItem {
        val timeZone = TimeZone.currentSystemDefault()
        return EntityTodoItem(
            id = id,
            planId = planId,
            title = title,
            content = content,
            isCompleted = isCompleted,
            triggerTime = triggerTime.toInstant(timeZone).toEpochMilliseconds(),
            completedAt = completedAt?.toInstant(timeZone)?.toEpochMilliseconds(),
            createdAt = createdAt.toInstant(timeZone).toEpochMilliseconds()
        )
    }

    /**
     * 实体列表转换为领域模型列表
     */
    fun List<EntityTodoItem>.toDomainModels(): List<DomainTodoItem> {
        return map { it.toDomainModel() }
    }

    /**
     * 领域模型列表转换为实体列表
     */
    fun List<DomainTodoItem>.toEntityModels(): List<EntityTodoItem> {
        return map { it.toEntityModel() }
    }
}