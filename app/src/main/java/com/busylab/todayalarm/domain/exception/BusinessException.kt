package com.busylab.todayalarm.domain.exception

sealed class BusinessException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class PlanNotFoundException(planId: String) : BusinessException("计划不存在: $planId")

    class TodoItemNotFoundException(todoItemId: String) : BusinessException("待办事项不存在: $todoItemId")

    class InvalidInputException(message: String) : BusinessException("输入无效: $message")

    class AlarmPermissionException : BusinessException("缺少闹钟权限")

    class NotificationPermissionException : BusinessException("缺少通知权限")

    class DatabaseException(message: String, cause: Throwable) : BusinessException("数据库错误: $message", cause)

    class NetworkException(message: String, cause: Throwable) : BusinessException("网络错误: $message", cause)
}