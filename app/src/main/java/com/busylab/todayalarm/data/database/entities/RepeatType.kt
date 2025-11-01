package com.busylab.todayalarm.data.database.entities

enum class RepeatType {
    NONE,       // 不重复
    DAILY,      // 每日
    WEEKLY,     // 每周
    MONTHLY,    // 每月
    YEARLY,     // 每年
    CUSTOM      // 自定义
}