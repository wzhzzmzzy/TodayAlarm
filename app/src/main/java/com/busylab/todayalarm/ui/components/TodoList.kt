package com.busylab.todayalarm.ui.components

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
import com.busylab.todayalarm.domain.model.TodoItem
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.TodoListViewModel
import com.busylab.todayalarm.ui.viewmodel.TodoListUiState
import kotlinx.datetime.LocalDateTime

/**
 * 待办列表组件
 * 与ViewModel耦合，负责处理数据获取和状态管理
 */
@Composable
fun TodoList(
    modifier: Modifier = Modifier,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TodoListContent(
        modifier = modifier,
        uiState = uiState,
        onRetry = { viewModel.loadTodoItems() },
        onCompleteToggle = { todoId -> viewModel.toggleComplete(todoId) }
    )
}

/**
 * 待办列表内容组件
 * 纯UI组件，不依赖ViewModel，便于测试和复用
 */
@Composable
fun TodoListContent(
    modifier: Modifier = Modifier,
    uiState: TodoListUiState,
    onRetry: () -> Unit,
    onCompleteToggle: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry
                    ) {
                        Text("重试")
                    }
                }
            }

            uiState.todoItems.isEmpty() -> {
                Text(
                    text = "暂无待办事项",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.todoItems,
                        key = { it.id }
                    ) { todoItem ->
                        TodoItemCard(
                            todoItem = todoItem,
                            onCompleteToggle = { onCompleteToggle(todoItem.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 待办事项卡片组件
 */
@Composable
private fun TodoItemCard(
    todoItem: TodoItem,
    onCompleteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 完成状态复选框
            Checkbox(
                checked = todoItem.isCompleted,
                onCheckedChange = { onCompleteToggle() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 待办内容
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 标题
                Text(
                    text = todoItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (todoItem.isCompleted) TextDecoration.LineThrough else null,
                    color = if (todoItem.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    else
                        MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // 内容（如果有）
                if (todoItem.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = todoItem.content,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = if (todoItem.isCompleted) TextDecoration.LineThrough else null,
                        color = if (todoItem.isCompleted)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 时间信息
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 触发时间
                    Text(
                        text = "提醒: ${formatLocalDateTime(todoItem.triggerTime)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // 完成时间
                    if (todoItem.isCompleted && todoItem.completedAt != null) {
                        Text(
                            text = "完成: ${formatLocalDateTime(todoItem.completedAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

private fun formatLocalDateTime(localDateTime: LocalDateTime): String {
    return try {
        val month = localDateTime.monthNumber
        val day = localDateTime.dayOfMonth
        val hour = localDateTime.hour
        val minute = localDateTime.minute
        "${month}-${day} ${hour}:${minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        "无效时间"
    }
}


@Preview(showBackground = true)
@Composable
private fun TodoListContentPreview() {
    TodayAlarmTheme {
        TodoListContent(
            uiState = TodoListUiState(
                todoItems = emptyList()
            ),
            onRetry = {},
            onCompleteToggle = {}
        )
    }
}