package com.busylab.todayalarm.system.log

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLogger @Inject constructor() {

    private val tag = "TodayAlarm"

    fun debug(message: String) {
        Log.d(tag, message)
    }

    fun info(message: String) {
        Log.i(tag, message)
    }

    fun warn(message: String) {
        Log.w(tag, message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
    }

    fun logSyncOperation(operation: String, result: String) {
        info("SyncOperation: $operation - $result")
    }

    fun logTodoPlanRelation(todoId: String, planId: String?, action: String) {
        debug("TodoPlanRelation: $action - Todo:$todoId Plan:$planId")
    }
}
