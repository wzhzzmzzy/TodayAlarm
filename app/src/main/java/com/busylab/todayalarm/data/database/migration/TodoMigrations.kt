package com.busylab.todayalarm.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object TodoMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // 创建临时表，包含所有新字段
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `todo_items_new` (
                    `id` TEXT NOT NULL,
                    `planId` TEXT NOT NULL,
                    `title` TEXT NOT NULL,
                    `content` TEXT NOT NULL,
                    `description` TEXT,
                    `triggerTime` INTEGER NOT NULL,
                    `dueTime` INTEGER,
                    `completedAt` INTEGER,
                    `createdAt` INTEGER NOT NULL,
                    `updatedAt` INTEGER NOT NULL,
                    `status` TEXT NOT NULL DEFAULT 'PENDING',
                    `isCompleted` INTEGER NOT NULL DEFAULT 0,
                    `isArchived` INTEGER NOT NULL DEFAULT 0,
                    `priority` TEXT NOT NULL DEFAULT 'NORMAL',
                    `category` TEXT NOT NULL DEFAULT 'GENERAL',
                    `tags` TEXT NOT NULL DEFAULT '',
                    `reminderEnabled` INTEGER NOT NULL DEFAULT 1,
                    `reminderTime` INTEGER,
                    `snoozeCount` INTEGER NOT NULL DEFAULT 0,
                    `maxSnoozeCount` INTEGER NOT NULL DEFAULT 3,
                    `metadata` TEXT NOT NULL DEFAULT '{}',
                    `attachments` TEXT NOT NULL DEFAULT '[]',
                    PRIMARY KEY(`id`),
                    FOREIGN KEY(`planId`) REFERENCES `plans`(`id`) ON DELETE CASCADE
                )
            """.trimIndent())

            // 从旧表迁移数据
            database.execSQL("""
                INSERT INTO `todo_items_new` (
                    `id`, `planId`, `title`, `content`, `triggerTime`, `completedAt`,
                    `createdAt`, `updatedAt`, `isCompleted`, `status`
                )
                SELECT
                    `id`, `planId`, `title`, `content`, `triggerTime`, `completedAt`,
                    `createdAt`, `createdAt` as `updatedAt`, `isCompleted`,
                    CASE WHEN `isCompleted` = 1 THEN 'COMPLETED' ELSE 'PENDING' END as `status`
                FROM `todo_items`
            """.trimIndent())

            // 删除旧表
            database.execSQL("DROP TABLE `todo_items`")

            // 重命名新表
            database.execSQL("ALTER TABLE `todo_items_new` RENAME TO `todo_items`")

            // 创建索引
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_planId` ON `todo_items` (`planId`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_isCompleted` ON `todo_items` (`isCompleted`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_triggerTime` ON `todo_items` (`triggerTime`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_priority` ON `todo_items` (`priority`)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_category` ON `todo_items` (`category`)")
        }
    }
}