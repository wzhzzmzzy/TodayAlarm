package com.busylab.todayalarm.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class OperationResult<out T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Error(val exception: Throwable) : OperationResult<Nothing>()
    object Loading : OperationResult<Nothing>()

    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading

    fun getOrNull(): T? = if (this is Success) data else null
    fun getErrorOrNull(): Throwable? = if (this is Error) exception else null

    inline fun onSuccess(action: (T) -> Unit): OperationResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable) -> Unit): OperationResult<T> {
        if (this is Error) action(exception)
        return this
    }

    inline fun onLoading(action: () -> Unit): OperationResult<T> {
        if (this is Loading) action()
        return this
    }
}

// 扩展函数
fun <T> Flow<T>.asOperationResult(): Flow<OperationResult<T>> = flow {
    emit(OperationResult.Loading)
    try {
        collect { value ->
            emit(OperationResult.Success(value))
        }
    } catch (e: Throwable) {
        emit(OperationResult.Error(e))
    }
}