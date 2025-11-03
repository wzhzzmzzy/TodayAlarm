package com.busylab.todayalarm.data.database.entities

import kotlinx.serialization.json.Json

// 状态检查扩展
fun TodoItem.isPending(): Boolean = status == TodoStatus.PENDING.name && !isCompleted
fun TodoItem.isCompleted(): Boolean = isCompleted && status == TodoStatus.COMPLETED.name
fun TodoItem.isOverdue(): Boolean = status == TodoStatus.OVERDUE.name ||
    (triggerTime < System.currentTimeMillis() && !isCompleted && status != TodoStatus.CANCELLED.name)
fun TodoItem.isSnoozed(): Boolean = status == TodoStatus.SNOOZED.name
fun TodoItem.isCancelled(): Boolean = status == TodoStatus.CANCELLED.name

// 时间相关扩展
fun TodoItem.isToday(): Boolean {
    val today = TodoTimeRange.today()
    return triggerTime in today.startTime..today.endTime
}

fun TodoItem.isThisWeek(): Boolean {
    val thisWeek = TodoTimeRange.thisWeek()
    return triggerTime in thisWeek.startTime..thisWeek.endTime
}

fun TodoItem.getDaysUntilDue(): Int {
    val now = System.currentTimeMillis()
    val diffInMillis = triggerTime - now
    return (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
}

// 优先级相关扩展
fun TodoItem.getPriorityEnum(): TodoPriority =
    try {
        TodoPriority.valueOf(priority)
    } catch (e: IllegalArgumentException) {
        TodoPriority.NORMAL
    }

fun TodoItem.getCategoryEnum(): TodoCategory =
    try {
        TodoCategory.valueOf(category)
    } catch (e: IllegalArgumentException) {
        TodoCategory.GENERAL
    }

// 标签相关扩展
fun TodoItem.getTagList(): List<TodoTag> =
    try {
        Json.decodeFromString(tags)
    } catch (e: Exception) {
        emptyList()
    }

fun TodoItem.hasTag(tagName: String): Boolean =
    getTagList().any { it.name.equals(tagName, ignoreCase = true) }

// 附件相关扩展
fun TodoItem.getAttachmentList(): List<TodoAttachment> =
    try {
        Json.decodeFromString(attachments)
    } catch (e: Exception) {
        emptyList()
    }

fun TodoItem.hasAttachments(): Boolean = getAttachmentList().isNotEmpty()

// 元数据相关扩展
fun TodoItem.getMetadataObject(): TodoMetadata =
    try {
        Json.decodeFromString(metadata)
    } catch (e: Exception) {
        TodoMetadata()
    }

// 推迟相关扩展
fun TodoItem.canSnooze(): Boolean = snoozeCount < maxSnoozeCount && !isCompleted()

fun TodoItem.getNextSnoozeTime(minutes: Int): Long =
    System.currentTimeMillis() + (minutes * 60 * 1000)