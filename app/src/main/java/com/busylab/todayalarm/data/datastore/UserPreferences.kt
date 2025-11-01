package com.busylab.todayalarm.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferences(
    val isFirstLaunch: Boolean = true,
    val notificationEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val defaultReminderMinutes: Int = 15,
    val themeMode: String = "SYSTEM", // LIGHT, DARK, SYSTEM
    val weekStartDay: Int = 1, // 1=Monday, 7=Sunday
    val timeFormat24Hour: Boolean = true
)