package com.busylab.todayalarm.domain.exception

/**
 * 业务异常基类
 */
sealed class BusinessException(message: String) : Exception(message) {

    /**
     * 计划不存在异常
     */
    class PlanNotFoundException(planId: String) : BusinessException("计划不存在: $planId")

    /**
     * 待办不存在异常
     */
    class TodoItemNotFoundException(todoId: String) : BusinessException("待办不存在: $todoId")

    /**
     * 数据验证异常
     */
    class ValidationException(message: String) : BusinessException("数据验证失败: $message")

    /**
     * 权限异常
     */
    class PermissionException(message: String) : BusinessException("权限不足: $message")

    /**
     * 同步异常
     */
    class SyncException(message: String) : BusinessException("同步失败: $message")

    /**
     * 重复操作异常
     */
    class DuplicateOperationException(message: String) : BusinessException("重复操作: $message")

    /**
     * 无效输入异常
     */
    class InvalidInputException(message: String) : BusinessException("无效输入: $message")

    /**
     * 数据库异常
     */
    class DatabaseException(message: String, cause: Throwable? = null) : BusinessException("数据库错误: $message") {
        init {
            if (cause != null) {
                initCause(cause)
            }
        }
    }
}
