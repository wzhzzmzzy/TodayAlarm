package com.busylab.todayalarm.ui.screens

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
import com.busylab.todayalarm.ui.viewmodel.SimpleTodoListViewModel
import kotlinx.datetime.LocalDateTime

/**
 * 简化的待办列表页面
 * 只展示所有待办事项和提供勾选完成功能
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTodoListScreen(
    modifier: Modifier = Modifier,
    viewModel: SimpleTodoListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "待办事项",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                            text = uiState.error ?: "未知错误",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadTodoItems() }
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
                            SimpleTodoItemCard(
                                todoItem = todoItem,
                                onCompleteToggle = { viewModel.toggleComplete(todoItem.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleTodoItemCard(
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
private fun SimpleTodoListScreenPreview() {
    TodayAlarmTheme {
        SimpleTodoListScreen()
    }
}