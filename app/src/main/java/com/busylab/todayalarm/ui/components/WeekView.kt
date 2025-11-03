package com.busylab.todayalarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.busylab.todayalarm.ui.components.calendar.WeekCalendarView
import com.busylab.todayalarm.ui.state.WeekViewUiState
import com.busylab.todayalarm.ui.state.WeekViewUiEvent
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.WeekViewViewModel
import com.busylab.todayalarm.domain.model.TodoItemUiModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * 周视图组件
 * 与WeekViewViewModel耦合，负责处理周历数据获取和状态管理
 */
@Composable
fun WeekView(
    modifier: Modifier = Modifier,
    viewModel: WeekViewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WeekViewContent(
        modifier = modifier,
        uiState = uiState,
        onDateSelected = { date ->
            viewModel.onEvent(WeekViewUiEvent.DateSelected(date))
        },
        onWeekChanged = { offset ->
            viewModel.onEvent(WeekViewUiEvent.WeekChanged(offset))
        },
        onRefresh = {
            viewModel.onEvent(WeekViewUiEvent.RefreshData)
        }
    )
}

/**
 * 周视图内容组件
 * 纯UI组件，不依赖ViewModel，便于测试和复用
 */
@Composable
fun WeekViewContent(
    modifier: Modifier = Modifier,
    uiState: WeekViewUiState,
    onDateSelected: (kotlinx.datetime.LocalDate) -> Unit,
    onWeekChanged: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(onClick = onRefresh) {
                        Text("重试")
                    }
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 周历视图
                item {
                    uiState.weekCalendar?.let { calendar ->
                        WeekCalendarView(
                            weekCalendar = calendar,
                            selectedDate = uiState.selectedDate,
                            onDateSelected = onDateSelected,
                            onWeekChanged = onWeekChanged,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // 选中日期的待办事项
                item {
                    if (uiState.selectedDateTodos.isNotEmpty()) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                            val dateText = if (uiState.selectedDate == today) {
                                "今日待办"
                            } else {
                                "选中日期待办"
                            }

                            Text(
                                text = "$dateText (${uiState.selectedDateTodos.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            uiState.selectedDateTodos.forEach { todo ->
                                TodoItemSummary(
                                    todoItem = todo,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
                }
            }
        }
    }
}

/**
 * 待办事项摘要组件
 * 在周视图中显示简化的待办事项信息
 */
@Composable
private fun TodoItemSummary(
    todoItem: TodoItemUiModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态指示器
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (todoItem.isCompleted)
                            MaterialTheme.colorScheme.outline
                        else
                            MaterialTheme.colorScheme.primary,
                        androidx.compose.foundation.shape.CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 待办内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = todoItem.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (todoItem.isCompleted) TextDecoration.LineThrough else null,
                    color = if (todoItem.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (todoItem.content.isNotBlank()) {
                    Text(
                        text = todoItem.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 时间信息
            Text(
                text = formatTodoTime(todoItem.triggerTime),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun formatTodoTime(timestamp: LocalDateTime): String {
    return try {
        String.format("%02d:%02d", timestamp.hour, timestamp.minute)
    } catch (e: Exception) {
        "00:00"
    }
}

@Preview(showBackground = true)
@Composable
private fun WeekViewContentPreview() {
    TodayAlarmTheme {
        WeekViewContent(
            uiState = WeekViewUiState(),
            onDateSelected = {},
            onWeekChanged = {},
            onRefresh = {}
        )
    }
}