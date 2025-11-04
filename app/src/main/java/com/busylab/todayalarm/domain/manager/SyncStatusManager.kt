package com.busylab.todayalarm.domain.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncStatusManager @Inject constructor() {

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    fun setSyncing() {
        _syncStatus.value = SyncStatus.Syncing
    }

    fun setSuccess(message: String? = null) {
        _syncStatus.value = SyncStatus.Success(message)
    }

    fun setError(error: String) {
        _syncStatus.value = SyncStatus.Error(error)
    }

    fun setIdle() {
        _syncStatus.value = SyncStatus.Idle
    }
}

sealed class SyncStatus {
    object Idle : SyncStatus()
    object Syncing : SyncStatus()
    data class Success(val message: String?) : SyncStatus()
    data class Error(val error: String) : SyncStatus()
}