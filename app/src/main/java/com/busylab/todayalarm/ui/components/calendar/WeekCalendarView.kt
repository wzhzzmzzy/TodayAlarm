package com.busylab.todayalarm.ui.components.calendar

import androidx.compose.foundation.background
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.busylab.todayalarm.domain.model.DayModel
import com.busylab.todayalarm.domain.model.PlanUiModel
import com.busylab.todayalarm.domain.model.TodoItemUiModel
import com.busylab.todayalarm.domain.model.WeekCalendarModel
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Composable
fun WeekCalendarView(
    weekCalendar: WeekCalendarModel,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onWeekChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 周范围标题和导航
            WeekHeader(
                weekRange = weekCalendar.weekRange,
                currentWeekOffset = weekCalendar.currentWeekOffset,
                onWeekChanged = onWeekChanged
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 日历内容 - 使用 Column 而不是 LazyColumn，因为一周只有7天
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weekCalendar.days.forEach { day ->
                    DayRow(
                        day = day,
                        isSelected = selectedDate == day.date,
                        onClick = { onDateSelected(day.date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekHeader(
    weekRange: String,
    currentWeekOffset: Int,
    onWeekChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onWeekChanged(currentWeekOffset - 1) }
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "上一周",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = weekRange,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { onWeekChanged(currentWeekOffset + 1) }
        ) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "下一周",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DayRow(
    day: DayModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        day.isToday -> MaterialTheme.colorScheme.secondaryContainer
        else -> Color.Transparent
    }

    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        day.isToday -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 日期信息
        Column(
            modifier = Modifier.width(80.dp)
        ) {
            Text(
                text = day.dayOfWeek,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = day.dayOfMonth.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 事件信息
        Column(
            modifier = Modifier.weight(1f)
        ) {
            if (day.hasEvents) {
                // 显示计划
                day.plans.forEach { plan ->
                    EventItem(
                        title = plan.title,
                        time = plan.formattedTime,
                        isCompleted = false,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // 显示待办事项
                day.todoItems.forEach { todo ->
                    EventItem(
                        title = todo.title,
                        time = formatTodoTime(todo.triggerTime),
                        isCompleted = todo.isCompleted,
                        color = if (todo.isCompleted)
                            MaterialTheme.colorScheme.outline
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                Text(
                    text = "无事件",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 事件数量指示器
        if (day.hasEvents) {
            val eventCount = day.plans.size + day.todoItems.size
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = eventCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EventItem(
    title: String,
    time: String,
    isCompleted: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isCompleted)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(4.dp))
}

@Preview(showBackground = true)
@Composable
private fun WeekCalendarViewPreview() {
    TodayAlarmTheme {
        WeekCalendarView(
            weekCalendar = WeekCalendarModel(
                days = emptyList(),
                weekRange = "2024年11月1日 - 11月7日",
                currentWeekOffset = 0
            ),
            selectedDate = null,
            onDateSelected = {},
            onWeekChanged = {}
        )
    }
}

private fun formatTodoTime(timestamp: LocalDateTime): String {
    return try {
        String.format("%02d:%02d", timestamp.hour, timestamp.minute)
    } catch (e: Exception) {
        "00:00"
    }
}