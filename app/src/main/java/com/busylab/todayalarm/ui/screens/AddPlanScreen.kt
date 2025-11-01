package com.busylab.todayalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.busylab.todayalarm.ui.components.input.DateTimePicker
import com.busylab.todayalarm.ui.components.input.RepeatSettings
import com.busylab.todayalarm.ui.state.AddPlanUiEvent
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.AddPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlanScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 处理UI事件
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddPlanUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is AddPlanUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar("错误: ${event.error}")
                }
                is AddPlanUiEvent.NavigateBack -> {
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
                        text = "添加计划",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AddPlanContent(
            uiState = uiState,
            onTitleChanged = { title ->
                viewModel.onEvent(AddPlanUiEvent.TitleChanged(title))
            },
            onContentChanged = { content ->
                viewModel.onEvent(AddPlanUiEvent.ContentChanged(content))
            },
            onDateTimeChanged = { dateTime ->
                viewModel.onEvent(AddPlanUiEvent.DateTimeChanged(dateTime))
            },
            onRepeatToggled = { isRepeating ->
                viewModel.onEvent(AddPlanUiEvent.RepeatToggled(isRepeating))
            },
            onRepeatTypeChanged = { repeatType ->
                viewModel.onEvent(AddPlanUiEvent.RepeatTypeChanged(repeatType))
            },
            onRepeatIntervalChanged = { interval ->
                viewModel.onEvent(AddPlanUiEvent.RepeatIntervalChanged(interval))
            },
            onSave = {
                viewModel.onEvent(AddPlanUiEvent.SavePlan)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun AddPlanContent(
    uiState: com.busylab.todayalarm.ui.state.AddPlanUiState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onDateTimeChanged: (kotlinx.datetime.LocalDateTime) -> Unit,
    onRepeatToggled: (Boolean) -> Unit,
    onRepeatTypeChanged: (com.busylab.todayalarm.domain.model.RepeatType) -> Unit,
    onRepeatIntervalChanged: (Int) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        // 保存按钮
        item {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = "保存计划",
                        style = MaterialTheme.typography.labelLarge
                    )
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

@Preview(showBackground = true)
@Composable
private fun AddPlanScreenPreview() {
    TodayAlarmTheme {
        AddPlanScreen(
            onNavigateBack = {}
        )
    }
}