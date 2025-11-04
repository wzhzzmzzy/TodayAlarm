package com.busylab.todayalarm.domain.handler

import com.busylab.todayalarm.domain.exception.BusinessException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor() {

    fun handleError(throwable: Throwable): String {
        return when (throwable) {
            is BusinessException.PlanNotFoundException -> "计划不存在"
            is BusinessException.TodoItemNotFoundException -> "待办不存在"
            is BusinessException.ValidationException -> throwable.message ?: "数据验证失败"
            is BusinessException.PermissionException -> throwable.message ?: "权限不足"
            is BusinessException.SyncException -> throwable.message ?: "同步失败"
            is BusinessException.DuplicateOperationException -> throwable.message ?: "重复操作"
            is BusinessException.InvalidInputException -> throwable.message ?: "输入无效"
            is BusinessException.DatabaseException -> throwable.message ?: "数据库错误"
            is IllegalArgumentException -> "参数错误: ${throwable.message}"
            is UnsupportedOperationException -> "功能暂不支持"
            else -> "操作失败: ${throwable.message ?: "未知错误"}"
        }
    }

    fun shouldRetry(throwable: Throwable): Boolean {
        return when (throwable) {
            is BusinessException.PlanNotFoundException -> false
            is BusinessException.TodoItemNotFoundException -> false
            is BusinessException.ValidationException -> false
            is BusinessException.PermissionException -> false
            is BusinessException.DuplicateOperationException -> false
            is BusinessException.InvalidInputException -> false
            is BusinessException.DatabaseException -> true // 数据库错误可以重试
            is IllegalArgumentException -> false
            is UnsupportedOperationException -> false
            else -> true // 网络错误等可以重试
        }
    }
}
