package com.busylab.todayalarm

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
import androidx.navigation.compose.rememberNavController
import com.busylab.todayalarm.system.permission.PermissionManager
import com.busylab.todayalarm.system.permission.PermissionState
import com.busylab.todayalarm.ui.navigation.TodayAlarmNavigation
import com.busylab.todayalarm.ui.permission.PermissionRequestScreen
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    private var permissionState by mutableStateOf(PermissionState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionState = permissionManager.checkAllRequiredPermissions()

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