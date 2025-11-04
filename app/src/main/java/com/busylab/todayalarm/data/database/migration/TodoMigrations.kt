package com.busylab.todayalarm.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object TodoMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.beginTransaction()
            try {
                // Step 0: Clean up any potential leftover temporary tables from previous failed migrations
                database.execSQL("DROP TABLE IF EXISTS `plans_old`")
                database.execSQL("DROP TABLE IF EXISTS `todo_items_old`")

                // Step 1: Rename existing tables to a temporary name
                database.execSQL("ALTER TABLE `plans` RENAME TO `plans_old`")
                database.execSQL("ALTER TABLE `todo_items` RENAME TO `todo_items_old`")

                // Step 2: Create the new tables with the final, correct schema
                database.execSQL("""
                    CREATE TABLE `plans` (
                        `id` TEXT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL,
                        `triggerTime` INTEGER NOT NULL, `isRepeating` INTEGER NOT NULL DEFAULT 0,
                        `repeatType` TEXT NOT NULL DEFAULT 'NONE', `repeatInterval` INTEGER NOT NULL DEFAULT 1,
                        `isActive` INTEGER NOT NULL DEFAULT 1, `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`)
                    )
                """)

                database.execSQL("""
                    CREATE TABLE `todo_items` (
                        `id` TEXT NOT NULL, `planId` TEXT, `title` TEXT NOT NULL, `content` TEXT NOT NULL,
                        `description` TEXT, `triggerTime` INTEGER NOT NULL, `dueTime` INTEGER,
                        `completedAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL,
                        `status` TEXT NOT NULL DEFAULT 'PENDING', `isCompleted` INTEGER NOT NULL DEFAULT 0,
                        `isArchived` INTEGER NOT NULL DEFAULT 0, `priority` TEXT NOT NULL DEFAULT 'NORMAL',
                        `category` TEXT NOT NULL DEFAULT 'GENERAL', `tags` TEXT NOT NULL DEFAULT '',
                        `reminderEnabled` INTEGER NOT NULL DEFAULT 1, `reminderTime` INTEGER,
                        `snoozeCount` INTEGER NOT NULL DEFAULT 0, `maxSnoozeCount` INTEGER NOT NULL DEFAULT 3,
                        `metadata` TEXT NOT NULL DEFAULT '{}', `attachments` TEXT NOT NULL DEFAULT '[]',
                        `enableRepeating` INTEGER NOT NULL DEFAULT 0, `repeatType` TEXT NOT NULL DEFAULT 'NONE',
                        `repeatInterval` INTEGER NOT NULL DEFAULT 1, `isActive` INTEGER NOT NULL DEFAULT 1,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`planId`) REFERENCES `plans`(`id`) ON DELETE CASCADE
                    )
                """)

                // Step 3: Copy data from the old tables to the new tables
                // Migrate Plans
                val plansColumnsCursor = database.query("PRAGMA table_info(plans_old)")
                val existingPlansColumns = mutableSetOf<String>().apply {
                    while (plansColumnsCursor.moveToNext()) { add(plansColumnsCursor.getString(1)) }
                    plansColumnsCursor.close()
                }

                val plansInsertCols = mutableListOf("`id`", "`title`", "`content`", "`triggerTime`")
                val plansSelectCols = mutableListOf("`id`", "`title`", "`content`", "`triggerTime`")

                if (existingPlansColumns.contains("isRepeating")) { plansInsertCols.add("`isRepeating`"); plansSelectCols.add("`isRepeating`") }
                if (existingPlansColumns.contains("repeatType")) { plansInsertCols.add("`repeatType`"); plansSelectCols.add("`repeatType`") }
                if (existingPlansColumns.contains("repeatInterval")) { plansInsertCols.add("`repeatInterval`"); plansSelectCols.add("`repeatInterval`") }
                if (existingPlansColumns.contains("isActive")) { plansInsertCols.add("`isActive`"); plansSelectCols.add("`isActive`") }

                val currentTime = System.currentTimeMillis()
                plansInsertCols.add("`createdAt`")
                plansSelectCols.add(if (existingPlansColumns.contains("createdAt")) "`createdAt`" else "$currentTime")
                plansInsertCols.add("`updatedAt`")
                plansSelectCols.add(if (existingPlansColumns.contains("updatedAt")) "`updatedAt`" else "$currentTime")

                database.execSQL("INSERT INTO `plans` (${plansInsertCols.joinToString(", ")}) SELECT ${plansSelectCols.joinToString(", ")} FROM `plans_old`")

                // Migrate TodoItems
                val todoColumnsCursor = database.query("PRAGMA table_info(todo_items_old)")
                val existingTodoColumns = mutableSetOf<String>().apply {
                    while (todoColumnsCursor.moveToNext()) { add(todoColumnsCursor.getString(1)) }
                    todoColumnsCursor.close()
                }

                val todoInsertCols = mutableListOf("`id`", "`title`", "`content`", "`triggerTime`")
                val todoSelectCols = mutableListOf("`id`", "`title`", "`content`", "`triggerTime`")

                todoInsertCols.add("`createdAt`")
                todoSelectCols.add(if (existingTodoColumns.contains("createdAt")) "`createdAt`" else "$currentTime")
                todoInsertCols.add("`updatedAt`")
                todoSelectCols.add(if (existingTodoColumns.contains("updatedAt")) "`updatedAt`" else "$currentTime")

                if (existingTodoColumns.contains("planId")) { todoInsertCols.add("`planId`"); todoSelectCols.add("`planId`") }
                if (existingTodoColumns.contains("completedAt")) { todoInsertCols.add("`completedAt`"); todoSelectCols.add("`completedAt`") }
                if (existingTodoColumns.contains("isCompleted")) {
                    todoInsertCols.add("`isCompleted`"); todoSelectCols.add("`isCompleted`")
                    todoInsertCols.add("`status`"); todoSelectCols.add("CASE WHEN `isCompleted` = 1 THEN 'COMPLETED' ELSE 'PENDING' END")
                } else {
                    todoInsertCols.add("`isCompleted`"); todoSelectCols.add("0")
                    todoInsertCols.add("`status`"); todoSelectCols.add("'PENDING'")
                }

                database.execSQL("INSERT INTO `todo_items` (${todoInsertCols.joinToString(", ")}) SELECT ${todoSelectCols.joinToString(", ")} FROM `todo_items_old`")

                // Step 4: Drop the old temporary tables
                database.execSQL("DROP TABLE `plans_old`")
                database.execSQL("DROP TABLE `todo_items_old`")

                // Step 5: Create indexes on the new tables
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_planId` ON `todo_items` (`planId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_isCompleted` ON `todo_items` (`isCompleted`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_triggerTime` ON `todo_items` (`triggerTime`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_priority` ON `todo_items` (`priority`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_category` ON `todo_items` (`category`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_enableRepeating` ON `todo_items` (`enableRepeating`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_todo_items_isActive` ON `todo_items` (`isActive`)")

                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
    }
}