package com.busylab.todayalarm.data.mapper

import com.busylab.todayalarm.data.database.entities.Plan as EntityPlan
import com.busylab.todayalarm.data.database.entities.RepeatType as EntityRepeatType
import com.busylab.todayalarm.domain.model.Plan as DomainPlan
import com.busylab.todayalarm.domain.model.RepeatType as DomainRepeatType
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Plan实体与领域模型转换器
 */
object PlanMapper {

    /**
     * 数据库实体转换为领域模型
     */
    fun EntityPlan.toDomainModel(): DomainPlan {
        val timeZone = TimeZone.currentSystemDefault()
        return DomainPlan(
            id = id,
            title = title,
            content = content,
            triggerTime = Instant.fromEpochMilliseconds(triggerTime).toLocalDateTime(timeZone),
            isRepeating = isRepeating,
            repeatType = when (EntityRepeatType.valueOf(repeatType)) {
                EntityRepeatType.NONE -> DomainRepeatType.NONE
                EntityRepeatType.DAILY -> DomainRepeatType.DAILY
                EntityRepeatType.WEEKLY -> DomainRepeatType.WEEKLY
                EntityRepeatType.MONTHLY -> DomainRepeatType.MONTHLY
                EntityRepeatType.YEARLY -> DomainRepeatType.YEARLY
                EntityRepeatType.CUSTOM -> DomainRepeatType.NONE // 暂时映射为NONE
            },
            repeatInterval = repeatInterval,
            isActive = isActive,
            createdAt = Instant.fromEpochMilliseconds(createdAt).toLocalDateTime(timeZone),
            updatedAt = Instant.fromEpochMilliseconds(updatedAt).toLocalDateTime(timeZone)
        )
    }

    /**
     * 领域模型转换为数据库实体
     */
    fun DomainPlan.toEntityModel(): EntityPlan {
        val timeZone = TimeZone.currentSystemDefault()
        return EntityPlan(
            id = id,
            title = title,
            content = content,
            triggerTime = triggerTime.toInstant(timeZone).toEpochMilliseconds(),
            isRepeating = isRepeating,
            repeatType = when (repeatType) {
                DomainRepeatType.NONE -> EntityRepeatType.NONE.name
                DomainRepeatType.DAILY -> EntityRepeatType.DAILY.name
                DomainRepeatType.WEEKLY -> EntityRepeatType.WEEKLY.name
                DomainRepeatType.MONTHLY -> EntityRepeatType.MONTHLY.name
                DomainRepeatType.YEARLY -> EntityRepeatType.YEARLY.name
            },
            repeatInterval = repeatInterval,
            isActive = isActive,
            createdAt = createdAt.toInstant(timeZone).toEpochMilliseconds(),
            updatedAt = updatedAt.toInstant(timeZone).toEpochMilliseconds()
        )
    }

    /**
     * 实体列表转换为领域模型列表
     */
    fun List<EntityPlan>.toDomainModels(): List<DomainPlan> {
        return map { it.toDomainModel() }
    }

    /**
     * 领域模型列表转换为实体列表
     */
    fun List<DomainPlan>.toEntityModels(): List<EntityPlan> {
        return map { it.toEntityModel() }
    }
}