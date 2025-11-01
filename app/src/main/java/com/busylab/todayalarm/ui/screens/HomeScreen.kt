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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.busylab.todayalarm.ui.components.calendar.WeekCalendarView
import com.busylab.todayalarm.ui.components.todo.TodoItemCard
import com.busylab.todayalarm.ui.components.todo.TodoItemSection
import com.busylab.todayalarm.ui.state.HomeUiEvent
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.HomeViewModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddPlan: () -> Unit,
    onNavigateToPlanList: () -> Unit,
    onNavigateToTodoList: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 处理UI事件
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is HomeUiEvent.ShowError -> {
                    snackbarHostState.showSnackbar("错误: ${event.error}")
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
                        text = "今日",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "计划列表"
                        )
                    },
                    label = { Text("计划") },
                    selected = false,
                    onClick = onNavigateToPlanList
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "待办列表"
                        )
                    },
                    label = { Text("待办") },
                    selected = false,
                    onClick = onNavigateToTodoList
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddPlan,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加计划"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        HomeContent(
            uiState = uiState,
            onDateSelected = { date ->
                viewModel.onEvent(HomeUiEvent.DateSelected(date))
            },
            onWeekChanged = { offset ->
                viewModel.onEvent(HomeUiEvent.WeekChanged(offset))
            },
            onRefresh = {
                viewModel.onEvent(HomeUiEvent.RefreshData)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun HomeContent(
    uiState: com.busylab.todayalarm.ui.state.HomeUiState,
    onDateSelected: (kotlinx.datetime.LocalDate) -> Unit,
    onWeekChanged: (Int) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
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

                // 今日待办事项
                item {
                    if (uiState.todayTodos.isNotEmpty()) {
                        TodoItemSection(
                            title = "今日待办",
                            todoItems = uiState.todayTodos,
                            onToggleComplete = { /* TODO: 实现待办事项完成切换 */ }
                        )
                    }
                }

                // 选中日期的待办事项（如果不是今天）
                item {
                    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                    if (uiState.selectedDate != null &&
                        uiState.selectedDate != today &&
                        uiState.selectedDateTodos.isNotEmpty()) {
                        TodoItemSection(
                            title = "选中日期待办",
                            todoItems = uiState.selectedDateTodos,
                            onToggleComplete = { /* TODO: 实现待办事项完成切换 */ }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp)) // Bottom padding
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    TodayAlarmTheme {
        HomeScreen(
            onNavigateToAddPlan = {},
            onNavigateToPlanList = {},
            onNavigateToTodoList = {}
        )
    }
}