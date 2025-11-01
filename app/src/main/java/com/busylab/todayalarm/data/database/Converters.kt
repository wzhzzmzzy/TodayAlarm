package com.busylab.todayalarm.data.database

import androidx.room.TypeConverter
import com.busylab.todayalarm.data.database.entities.RepeatType

class Converters {

    @TypeConverter
    fun fromRepeatType(repeatType: RepeatType): String {
        return repeatType.name
    }

    @TypeConverter
    fun toRepeatType(repeatType: String): RepeatType {
        return try {
            RepeatType.valueOf(repeatType)
        } catch (e: IllegalArgumentException) {
            RepeatType.NONE
        }
    }
}