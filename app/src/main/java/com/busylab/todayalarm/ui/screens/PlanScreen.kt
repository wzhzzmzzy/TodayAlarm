package com.busylab.todayalarm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.busylab.todayalarm.ui.components.input.DateTimePicker
import com.busylab.todayalarm.ui.components.input.RepeatSettings
import com.busylab.todayalarm.ui.state.PlanUiEvent
import com.busylab.todayalarm.ui.state.PlanUiState
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.PlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanScreen(
    planId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isEditMode = planId != null

    // 处理UI事件
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PlanUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is PlanUiEvent.ShowError -> snackbarHostState.showSnackbar("错误: ${event.error}")
                is PlanUiEvent.NavigateBack -> onNavigateBack()
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
                        text = if (isEditMode) "编辑计划" else "添加计划",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(
                            onClick = { viewModel.onEvent(PlanUiEvent.DeletePlan) },
                            enabled = !uiState.isLoading
                        ) {
                            Icon(Icons.Default.Delete, "删除计划", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PlanContent(
            uiState = uiState,
            isEditMode = isEditMode,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun PlanContent(
    uiState: PlanUiState,
    isEditMode: Boolean,
    onEvent: (PlanUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading && uiState.planId == null && isEditMode -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.planNotFound -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("计划未找到", color = MaterialTheme.colorScheme.error)
            }
        }
        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 计划状态开关 (仅编辑模式)
                if (isEditMode) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("计划状态", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = uiState.isActive,
                                onCheckedChange = { onEvent(PlanUiEvent.ActiveToggled(it)) }
                            )
                        }
                    }
                }

                // 计划标题
                item {
                    OutlinedTextField(
                        value = uiState.title,
                        onValueChange = { onEvent(PlanUiEvent.TitleChanged(it)) },
                        label = { Text("计划标题") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.validationErrors.containsKey("title"),
                        supportingText = { uiState.validationErrors["title"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }

                // 计划内容
                item {
                    OutlinedTextField(
                        value = uiState.content,
                        onValueChange = { onEvent(PlanUiEvent.ContentChanged(it)) },
                        label = { Text("计划内容") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        isError = uiState.validationErrors.containsKey("content"),
                        supportingText = { uiState.validationErrors["content"]?.let { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )
                }

                // 提醒时间
                item {
                    DateTimePicker(
                        selectedDateTime = uiState.triggerDateTime,
                        onDateTimeSelected = { onEvent(PlanUiEvent.DateTimeChanged(it)) },
                        label = "提醒时间"
                    )
                    uiState.validationErrors["triggerDateTime"]?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                // 重复设置
                item {
                    RepeatSettings(
                        isRepeating = uiState.isRepeating,
                        repeatType = uiState.repeatType,
                        repeatInterval = uiState.repeatInterval,
                        onRepeatToggled = { onEvent(PlanUiEvent.RepeatToggled(it)) },
                        onRepeatTypeChanged = { onEvent(PlanUiEvent.RepeatTypeChanged(it)) },
                        onRepeatIntervalChanged = { onEvent(PlanUiEvent.RepeatIntervalChanged(it)) },
                        intervalError = uiState.validationErrors["repeatInterval"]
                    )
                }

                // 保存/更新/删除按钮
                item {
                    if (isEditMode) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { onEvent(PlanUiEvent.DeletePlan) },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isLoading,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("删除")
                            }
                            Button(
                                onClick = { onEvent(PlanUiEvent.SaveOrUpdatePlan) },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isLoading
                            ) {
                                if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("更新计划")
                            }
                        }
                    } else {
                        Button(
                            onClick = { onEvent(PlanUiEvent.SaveOrUpdatePlan) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("保存计划")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanScreenAddPreview() {
    TodayAlarmTheme {
        PlanScreen(planId = null, onNavigateBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanScreenEditPreview() {
    TodayAlarmTheme {
        PlanScreen(planId = "1", onNavigateBack = {})
    }
}
