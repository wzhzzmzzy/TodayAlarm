package com.busylab.todayalarm.data.database.entities

enum class TodoStatus {
    PENDING,    // 待完成
    COMPLETED,  // 已完成
    OVERDUE,    // 已过期
    CANCELLED,  // 已取消
    SNOOZED     // 已推迟
}

enum class TodoPriority {
    LOW,        // 低优先级
    NORMAL,     // 普通优先级
    HIGH,       // 高优先级
    URGENT      // 紧急
}

enum class TodoCategory {
    GENERAL,    // 一般事项
    WORK,       // 工作相关
    PERSONAL,   // 个人事务
    HEALTH,     // 健康相关
    STUDY,      // 学习相关
    SHOPPING,   // 购物清单
    TRAVEL,     // 旅行计划
    CUSTOM      // 自定义分类
}