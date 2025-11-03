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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.busylab.todayalarm.ui.components.plan.PlanCard
import com.busylab.todayalarm.ui.state.PlanFilterType
import com.busylab.todayalarm.ui.state.PlanListUiEvent
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import com.busylab.todayalarm.ui.viewmodel.PlanListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddPlan: () -> Unit,
    onNavigateToEditPlan: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 处理UI事件
    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PlanListUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is PlanListUiEvent.ShowError -> {
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
                        text = "计划列表",
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddPlan,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "添加计划"
                    )
                },
                text = { Text("添加计划") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        PlanListContent(
            uiState = uiState,
            onFilterChanged = { filterType ->
                viewModel.onEvent(PlanListUiEvent.FilterChanged(filterType))
            },
            onPlanToggled = { planId, isActive ->
                viewModel.onEvent(PlanListUiEvent.PlanToggled(planId, isActive))
            },
            onPlanDeleted = { planId ->
                viewModel.onEvent(PlanListUiEvent.PlanDeleted(planId))
            },
            onPlanEdit = onNavigateToEditPlan,
            onRefresh = {
                viewModel.onEvent(PlanListUiEvent.RefreshPlans)
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun PlanListContent(
    uiState: com.busylab.todayalarm.ui.state.PlanListUiState,
    onFilterChanged: (PlanFilterType) -> Unit,
    onPlanToggled: (String, Boolean) -> Unit,
    onPlanDeleted: (String) -> Unit,
    onPlanEdit: (String) -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 过滤器
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.filterType == PlanFilterType.ALL,
                            onClick = { onFilterChanged(PlanFilterType.ALL) },
                            label = { Text("全部 (${uiState.plans.size})") }
                        )

                        FilterChip(
                            selected = uiState.filterType == PlanFilterType.ACTIVE,
                            onClick = { onFilterChanged(PlanFilterType.ACTIVE) },
                            label = { Text("活跃 (${uiState.activePlans.size})") }
                        )

                        FilterChip(
                            selected = uiState.filterType == PlanFilterType.INACTIVE,
                            onClick = { onFilterChanged(PlanFilterType.INACTIVE) },
                            label = { Text("停用 (${uiState.inactivePlans.size})") }
                        )
                    }
                }

                // 计划列表
                val filteredPlans = when (uiState.filterType) {
                    PlanFilterType.ALL -> uiState.plans
                    PlanFilterType.ACTIVE -> uiState.activePlans
                    PlanFilterType.INACTIVE -> uiState.inactivePlans
                }

                if (filteredPlans.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (uiState.filterType) {
                                    PlanFilterType.ALL -> "暂无计划\n点击右下角按钮添加第一个计划"
                                    PlanFilterType.ACTIVE -> "暂无活跃计划"
                                    PlanFilterType.INACTIVE -> "暂无停用计划"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(
                        items = filteredPlans,
                        key = { it.id }
                    ) { plan ->
                        PlanCard(
                            plan = plan,
                            onEdit = { onPlanEdit(plan.id) },
                            onDelete = { onPlanDeleted(plan.id) },
                            onToggle = { isActive -> onPlanToggled(plan.id, isActive) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // FAB padding
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanListScreenPreview() {
    TodayAlarmTheme {
        PlanListScreen(
            onNavigateBack = {},
            onNavigateToAddPlan = {},
            onNavigateToEditPlan = {}
        )
    }
}