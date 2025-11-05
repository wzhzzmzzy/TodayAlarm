package com.busylab.todayalarm.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.busylab.todayalarm.data.database.dao.PlanDao
import com.busylab.todayalarm.data.database.dao.TodoItemDao
import com.busylab.todayalarm.data.database.entities.Plan
import com.busylab.todayalarm.data.database.entities.TodoItem
import com.busylab.todayalarm.data.database.converters.DatabaseConverters

@Database(
    entities = [Plan::class, TodoItem::class],
    version = 2, // 版本升级
    exportSchema = true
)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun planDao(): PlanDao
    abstract fun todoItemDao(): TodoItemDao

    companion object {
        const val DATABASE_NAME = "today_alarm_database"
    }
}