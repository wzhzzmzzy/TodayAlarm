package com.busylab.todayalarm.domain.model

import com.busylab.todayalarm.data.database.entities.RepeatType as EntityRepeatType
import com.busylab.todayalarm.domain.model.RepeatType as DomainRepeatType

/**
 * 重复类型转换器
 */
object RepeatTypeConverter {

    /**
     * 数据库实体RepeatType转换为领域模型RepeatType
     */
    fun EntityRepeatType.toDomainModel(): DomainRepeatType {
        return when (this) {
            EntityRepeatType.NONE -> DomainRepeatType.NONE
            EntityRepeatType.DAILY -> DomainRepeatType.DAILY
            EntityRepeatType.WEEKLY -> DomainRepeatType.WEEKLY
            EntityRepeatType.MONTHLY -> DomainRepeatType.MONTHLY
            EntityRepeatType.YEARLY -> DomainRepeatType.YEARLY
            EntityRepeatType.CUSTOM -> DomainRepeatType.NONE // 暂时映射为NONE
        }
    }

    /**
     * 领域模型RepeatType转换为数据库实体RepeatType
     */
    fun DomainRepeatType.toEntityModel(): EntityRepeatType {
        return when (this) {
            DomainRepeatType.NONE -> EntityRepeatType.NONE
            DomainRepeatType.DAILY -> EntityRepeatType.DAILY
            DomainRepeatType.WEEKLY -> EntityRepeatType.WEEKLY
            DomainRepeatType.MONTHLY -> EntityRepeatType.MONTHLY
            DomainRepeatType.YEARLY -> EntityRepeatType.YEARLY
        }
    }
}