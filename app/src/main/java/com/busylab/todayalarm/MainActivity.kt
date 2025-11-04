package com.busylab.todayalarm

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.busylab.todayalarm.domain.usecase.todo.CreateTodoFromNotificationUseCase
import com.busylab.todayalarm.system.permission.PermissionManager
import com.busylab.todayalarm.system.permission.PermissionState
import com.busylab.todayalarm.ui.navigation.TodayAlarmNavigation
import com.busylab.todayalarm.ui.permission.PermissionRequestScreen
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var createTodoFromNotificationUseCase: CreateTodoFromNotificationUseCase

    private var permissionState by mutableStateOf(PermissionState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionState = permissionManager.checkAllRequiredPermissions()

        // 处理通知点击
        handleNotificationIntent(intent)

        setContent {
            TodayAlarmTheme {
                if (permissionState.allPermissionsGranted) {
                    TodayAlarmApp()
                } else {
                    PermissionRequestScreen(
                        permissionState = permissionState,
                        onGoToNotificationSettings = {
                            permissionManager.openAppSettings(this)
                        },
                        onGoToExactAlarmSettings = {
                            permissionManager.requestExactAlarmPermission(this)
                        },
                        onGoToBatteryOptimizationSettings = {
                            permissionManager.requestIgnoreBatteryOptimization(this)
                        }
                    )
                }
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val action = intent?.getStringExtra("action")
        val planId = intent?.getStringExtra("plan_id")

        if (action == "create_todo_from_notification" && !planId.isNullOrEmpty()) {
            // 使用 UseCase 创建 TodoItem
            lifecycleScope.launch {
                createTodoFromNotificationUseCase(CreateTodoFromNotificationUseCase.Params(planId))
                    .onSuccess { todoItem ->
                        // 可以在这里添加成功提示，比如 Snackbar
                        // 目前暂时不添加UI提示，只是创建待办事项
                    }
                    .onFailure { e ->
                        // 可以在这里添加错误提示
                        // 目前暂时不添加UI提示，只是记录错误
                        e.printStackTrace()
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 当用户从设置返回应用时，重新检查权限
        permissionState = permissionManager.checkAllRequiredPermissions()
    }
}

@Composable
fun TodayAlarmApp() {
    val navController = rememberNavController()

    TodayAlarmNavigation(
        navController = navController,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun TodayAlarmAppPreview() {
    TodayAlarmTheme {
        TodayAlarmApp()
    }
}