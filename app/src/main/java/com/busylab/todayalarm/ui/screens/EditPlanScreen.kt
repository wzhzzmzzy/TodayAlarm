package com.busylab.todayalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.busylab.todayalarm.ui.components.input.DateTimePicker
import com.busylab.todayalarm.ui.components.input.RepeatSettings
import com.busylab.todayalarm.ui.state.EditPlanUiEvent
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.EditPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanScreen(
    planId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 加载计划数据
    LaunchedEffect(planId) {
        viewModel.onEvent(EditPlanUiEvent.LoadPlan(planId))
    }

    // 处理UI事件
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is EditPlanUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is EditPlanUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar("错误: ${event.error}")
                }
                is EditPlanUiEvent.NavigateBack -> {
                    onNavigateBack()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "编辑计划",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(EditPlanUiEvent.DeletePlan) },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "删除计划",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        EditPlanContent(
            uiState = uiState,
            onTitleChanged = { title ->
                viewModel.onEvent(EditPlanUiEvent.TitleChanged(title))
            },
            onContentChanged = { content ->
                viewModel.onEvent(EditPlanUiEvent.ContentChanged(content))
            },
            onDateTimeChanged = { dateTime ->
                viewModel.onEvent(EditPlanUiEvent.DateTimeChanged(dateTime))
            },
            onRepeatToggled = { isRepeating ->
                viewModel.onEvent(EditPlanUiEvent.RepeatToggled(isRepeating))
            },
            onRepeatTypeChanged = { repeatType ->
                viewModel.onEvent(EditPlanUiEvent.RepeatTypeChanged(repeatType))
            },
            onRepeatIntervalChanged = { interval ->
                viewModel.onEvent(EditPlanUiEvent.RepeatIntervalChanged(interval))
            },
            onActiveToggled = { isActive ->
                viewModel.onEvent(EditPlanUiEvent.ActiveToggled(isActive))
            },
            onUpdate = {
                viewModel.onEvent(EditPlanUiEvent.UpdatePlan)
            },
            onDelete = {
                viewModel.onEvent(EditPlanUiEvent.DeletePlan)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun EditPlanContent(
    uiState: com.busylab.todayalarm.ui.state.EditPlanUiState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onDateTimeChanged: (kotlinx.datetime.LocalDateTime) -> Unit,
    onRepeatToggled: (Boolean) -> Unit,
    onRepeatTypeChanged: (com.busylab.todayalarm.domain.model.RepeatType) -> Unit,
    onRepeatIntervalChanged: (Int) -> Unit,
    onActiveToggled: (Boolean) -> Unit,
    onUpdate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading && uiState.planId.isEmpty() -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.planNotFound -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "计划不存在",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FilledTonalButton(onClick = { /* Navigate back */ }) {
                        Text("返回")
                    }
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 计划状态开关
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "计划状态",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (uiState.isActive) "已启用" else "已停用",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (uiState.isActive)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Switch(
                                checked = uiState.isActive,
                                onCheckedChange = onActiveToggled
                            )
                        }
                    }
                }

                // 计划标题
                item {
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = onTitleChanged,
                        label = { Text("计划标题") },
                        placeholder = { Text("请输入计划标题") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        isError = uiState.validationErrors.containsKey("title"),
                        supportingText = {
                            uiState.validationErrors["title"]?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }

                // 计划内容
                item {
                    OutlinedTextField(
                        value = uiState.content,
                        onValueChange = onContentChanged,
                        label = { Text("计划内容") },
                        placeholder = { Text("请输入计划详细内容") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences
                        ),
                        isError = uiState.validationErrors.containsKey("content"),
                        supportingText = {
                            uiState.validationErrors["content"]?.let { error ->
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                }

                // 提醒时间
                item {
                    Column {
                        DateTimePicker(
                            selectedDateTime = uiState.triggerDateTime,
                            onDateTimeSelected = onDateTimeChanged,
                            label = "提醒时间"
                        )

                        uiState.validationErrors["triggerDateTime"]?.let { error ->
                            Text(
                                text = error,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // 重复设置
                item {
                    RepeatSettings(
                        isRepeating = uiState.isRepeating,
                        repeatType = uiState.repeatType,
                        repeatInterval = uiState.repeatInterval,
                        onRepeatToggled = onRepeatToggled,
                        onRepeatTypeChanged = onRepeatTypeChanged,
                        onRepeatIntervalChanged = onRepeatIntervalChanged,
                        intervalError = uiState.validationErrors["repeatInterval"]
                    )
                }

                // 操作按钮
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Text(
                                    text = "删除",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                        Button(
                            onClick = onUpdate,
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator()
                            } else {
                                Text(
                                    text = "更新计划",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }

                // 错误信息
                item {
                    uiState.error?.let { error ->
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EditPlanScreenPreview() {
    TodayAlarmTheme {
        EditPlanScreen(
            planId = "1",
            onNavigateBack = {}
        )
    }
}