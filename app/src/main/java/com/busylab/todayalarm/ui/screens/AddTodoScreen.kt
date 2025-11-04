package com.busylab.todayalarm.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.ui.components.input.DateTimePicker
import com.busylab.todayalarm.ui.state.AddTodoUiEvent
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.AddTodoViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddTodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 处理UI事件
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AddTodoUiEvent.TodoCreated -> {
                    snackbarHostState.showSnackbar("待办事项创建成功")
                    onNavigateBack()
                }
                is AddTodoUiEvent.Error -> {
                    snackbarHostState.showSnackbar("错误: ${event.message}")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "添加待办事项",
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
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        AddTodoContent(
            uiState = uiState,
            onTitleChanged = viewModel::updateTitle,
            onContentChanged = viewModel::updateContent,
            onTriggerTimeChanged = viewModel::updateTriggerTime,
            onEnableRepeatingChanged = viewModel::updateEnableRepeating,
            onRepeatTypeChanged = viewModel::updateRepeatType,
            onRepeatIntervalChanged = viewModel::updateRepeatInterval,
            onSave = viewModel::createTodo,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun AddTodoContent(
    uiState: com.busylab.todayalarm.ui.state.AddTodoUiState,
    onTitleChanged: (String) -> Unit,
    onContentChanged: (String) -> Unit,
    onTriggerTimeChanged: (kotlinx.datetime.LocalDateTime) -> Unit,
    onEnableRepeatingChanged: (Boolean) -> Unit,
    onRepeatTypeChanged: (RepeatType) -> Unit,
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
        // 标题输入
        item {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChanged,
                label = { Text("标题") },
                placeholder = { Text("请输入待办事项标题") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
        }

        // 内容输入
        item {
            OutlinedTextField(
                value = uiState.content,
                onValueChange = onContentChanged,
                label = { Text("内容") },
                placeholder = { Text("请输入待办事项详细内容") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )
        }

        // 时间选择
        item {
            DateTimePicker(
                selectedDateTime = uiState.triggerTime,
                onDateTimeSelected = onTriggerTimeChanged,
                label = "提醒时间"
            )
        }

        // 重复设置
        item {
            RepeatSettingsSection(
                enableRepeating = uiState.enableRepeating,
                repeatType = uiState.repeatType,
                repeatInterval = uiState.repeatInterval,
                onEnableRepeatingChanged = onEnableRepeatingChanged,
                onRepeatTypeChanged = onRepeatTypeChanged,
                onRepeatIntervalChanged = onRepeatIntervalChanged
            )
        }

        // 保存按钮
        item {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.title.isNotBlank() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("保存")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RepeatSettingsSection(
    enableRepeating: Boolean,
    repeatType: RepeatType,
    repeatInterval: Int,
    onEnableRepeatingChanged: (Boolean) -> Unit,
    onRepeatTypeChanged: (RepeatType) -> Unit,
    onRepeatIntervalChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 重复开关
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = enableRepeating,
                onCheckedChange = onEnableRepeatingChanged
            )
            Text("重复提醒")
        }

        if (enableRepeating) {
            Spacer(modifier = Modifier.height(8.dp))

            // 重复类型选择
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    value = getRepeatTypeText(repeatType),
                    onValueChange = {},
                    label = { Text("重复类型") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    RepeatType.values().filter { it != RepeatType.NONE }.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(getRepeatTypeText(type)) },
                            onClick = {
                                onRepeatTypeChanged(type)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 重复间隔
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("间隔:", modifier = Modifier.weight(1f))
                OutlinedTextField(
                    value = repeatInterval.toString(),
                    onValueChange = { value ->
                        value.toIntOrNull()?.let { onRepeatIntervalChanged(it) }
                    },
                    modifier = Modifier.width(80.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text(" ${getRepeatUnitText(repeatType)}")
            }
        }
    }
}

private fun getRepeatTypeText(repeatType: RepeatType): String {
    return when (repeatType) {
        RepeatType.DAILY -> "每日"
        RepeatType.WEEKLY -> "每周"
        RepeatType.MONTHLY -> "每月"
        RepeatType.YEARLY -> "每年"
        RepeatType.NONE -> "不重复"
    }
}

private fun getRepeatUnitText(repeatType: RepeatType): String {
    return when (repeatType) {
        RepeatType.DAILY -> "天"
        RepeatType.WEEKLY -> "周"
        RepeatType.MONTHLY -> "月"
        RepeatType.YEARLY -> "年"
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTodoScreenPreview() {
    TodayAlarmTheme {
        AddTodoScreen(
            onNavigateBack = {}
        )
    }
}