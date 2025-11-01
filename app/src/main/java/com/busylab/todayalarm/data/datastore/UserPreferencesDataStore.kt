package com.busylab.todayalarm.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                isFirstLaunch = preferences[PreferencesKeys.IS_FIRST_LAUNCH] ?: true,
                notificationEnabled = preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: true,
                vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
                soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
                defaultReminderMinutes = preferences[PreferencesKeys.DEFAULT_REMINDER_MINUTES] ?: 15,
                themeMode = preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM",
                weekStartDay = preferences[PreferencesKeys.WEEK_START_DAY] ?: 1,
                timeFormat24Hour = preferences[PreferencesKeys.TIME_FORMAT_24_HOUR] ?: true
            )
        }

    suspend fun updateFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_FIRST_LAUNCH] = isFirstLaunch
        }
    }

    suspend fun updateNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun updateSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun updateDefaultReminderMinutes(minutes: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_REMINDER_MINUTES] = minutes
        }
    }

    suspend fun updateThemeMode(themeMode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = themeMode
        }
    }

    suspend fun updateWeekStartDay(day: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WEEK_START_DAY] = day
        }
    }

    suspend fun updateTimeFormat24Hour(is24Hour: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.TIME_FORMAT_24_HOUR] = is24Hour
        }
    }

    private object PreferencesKeys {
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val DEFAULT_REMINDER_MINUTES = intPreferencesKey("default_reminder_minutes")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val WEEK_START_DAY = intPreferencesKey("week_start_day")
        val TIME_FORMAT_24_HOUR = booleanPreferencesKey("time_format_24_hour")
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")