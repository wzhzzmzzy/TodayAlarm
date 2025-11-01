package com.busylab.todayalarm.system.permission

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 权限管理器
 */
@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * 检查通知权限
     */
    fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * 检查精确闹钟权限
     */
    fun checkExactAlarmPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService<AlarmManager>()
            alarmManager?.canScheduleExactAlarms() ?: false
        } else {
            true
        }
    }

    /**
     * 请求精确闹钟权限
     */
    fun requestExactAlarmPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${activity.packageName}")
            }
            activity.startActivity(intent)
        }
    }

    /**
     * 检查电池优化白名单
     */
    fun checkBatteryOptimization(): Boolean {
        val powerManager = context.getSystemService<PowerManager>()
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: false
    }

    /**
     * 请求忽略电池优化
     */
    fun requestIgnoreBatteryOptimization(activity: Activity) {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:${activity.packageName}")
        }
        activity.startActivity(intent)
    }

    /**
     * 检查开机启动权限
     */
    fun checkBootPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECEIVE_BOOT_COMPLETED
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 检查所有必需权限
     */
    fun checkAllRequiredPermissions(): PermissionState {
        return PermissionState(
            hasNotificationPermission = checkNotificationPermission(),
            hasExactAlarmPermission = checkExactAlarmPermission(),
            isBatteryOptimizationIgnored = checkBatteryOptimization(),
            hasBootPermission = checkBootPermission()
        )
    }

    /**
     * 打开应用设置页面
     */
    fun openAppSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${activity.packageName}")
        }
        activity.startActivity(intent)
    }
}

/**
 * 权限状态数据类
 */
data class PermissionState(
    val hasNotificationPermission: Boolean = false,
    val hasExactAlarmPermission: Boolean = false,
    val isBatteryOptimizationIgnored: Boolean = false,
    val hasBootPermission: Boolean = false
) {
    /**
     * 是否所有权限都已授予
     */
    val allPermissionsGranted: Boolean
        get() = hasNotificationPermission &&
                hasExactAlarmPermission &&
                hasBootPermission

    /**
     * 获取缺失的权限列表
     */
    val missingPermissions: List<String>
        get() = buildList {
            if (!hasNotificationPermission) add("通知权限")
            if (!hasExactAlarmPermission) add("精确闹钟权限")
            if (!hasBootPermission) add("开机启动权限")
        }
}