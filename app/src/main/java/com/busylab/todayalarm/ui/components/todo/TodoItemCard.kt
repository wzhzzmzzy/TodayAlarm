package com.busylab.todayalarm.ui.components.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.busylab.todayalarm.domain.model.TodoItemUiModel
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TodoItemCard(
    todoItem: TodoItemUiModel,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (todoItem.isCompleted)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 完成状态按钮
            IconButton(
                onClick = onToggleComplete,
                modifier = Modifier.padding(0.dp)
            ) {
                Icon(
                    imageVector = if (todoItem.isCompleted)
                        Icons.Default.CheckCircle
                    else
                        Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (todoItem.isCompleted) "已完成" else "未完成",
                    tint = if (todoItem.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 内容区域
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 标题
                Text(
                    text = todoItem.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (todoItem.isCompleted)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (todoItem.isCompleted)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 内容
                if (todoItem.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = todoItem.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (todoItem.isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = if (todoItem.isCompleted)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 时间信息
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = if (todoItem.isOverdue && !todoItem.isCompleted)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "${todoItem.formattedDate} ${todoItem.formattedTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (todoItem.isOverdue && !todoItem.isCompleted)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 状态标签
                    if (todoItem.isOverdue && !todoItem.isCompleted) {
                        Text(
                            text = "已过期",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (todoItem.isCompleted && todoItem.completedAt != null) {
                        Text(
                            text = "• 已完成",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItemSection(
    title: String,
    todoItems: List<TodoItemUiModel>,
    onToggleComplete: (TodoItemUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (todoItems.isNotEmpty()) {
        Column(modifier = modifier) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                todoItems.forEach { todoItem ->
                    TodoItemCard(
                        todoItem = todoItem,
                        onToggleComplete = { onToggleComplete(todoItem) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoItemCardPreview() {
    TodayAlarmTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            TodoItemCard(
                todoItem = TodoItemUiModel(
                    id = "1",
                    planId = "plan1",
                    title = "晨间锻炼",
                    content = "进行30分钟的有氧运动",
                    isCompleted = false,
                    triggerTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    completedAt = null,
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    formattedTime = "07:00",
                    formattedDate = "今天",
                    timeAgo = "2小时前",
                    isOverdue = false
                ),
                onToggleComplete = {}
            )

            TodoItemCard(
                todoItem = TodoItemUiModel(
                    id = "2",
                    planId = "plan2",
                    title = "已完成的任务",
                    content = "这是一个已完成的任务",
                    isCompleted = true,
                    triggerTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    completedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    formattedTime = "09:00",
                    formattedDate = "今天",
                    timeAgo = "1小时前",
                    isOverdue = false
                ),
                onToggleComplete = {}
            )

            TodoItemCard(
                todoItem = TodoItemUiModel(
                    id = "3",
                    planId = "plan3",
                    title = "过期的任务",
                    content = "这是一个已过期的任务",
                    isCompleted = false,
                    triggerTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    completedAt = null,
                    createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                    formattedTime = "昨天 15:00",
                    formattedDate = "昨天",
                    timeAgo = "1天前",
                    isOverdue = true
                ),
                onToggleComplete = {}
            )
        }
    }
}