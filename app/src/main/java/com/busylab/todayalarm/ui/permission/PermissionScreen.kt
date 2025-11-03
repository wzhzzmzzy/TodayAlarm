package com.busylab.todayalarm.ui.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.busylab.todayalarm.system.permission.PermissionState

/**
 * 权限请求界面
 *
 * @param permissionState 当前的权限状态
 * @param onGoToNotificationSettings 点击开启通知权限按钮的回调
 * @param onGoToExactAlarmSettings 点击开启精确闹钟权限按钮的回调
 * @param onGoToBatteryOptimizationSettings 点击忽略电池优化按钮的回调
 */
@Composable
fun PermissionRequestScreen(
    permissionState: PermissionState,
    onGoToNotificationSettings: () -> Unit,
    onGoToExactAlarmSettings: () -> Unit,
    onGoToBatteryOptimizationSettings: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "权限说明",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "为了确保闹钟和通知能够准时工作，应用需要以下权限：",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 通知权限
            if (!permissionState.hasNotificationPermission) {
                PermissionItem(
                    title = "通知权限",
                    description = "用于在计划时间到达时向您发送提醒通知。",
                    buttonText = "开启通知",
                    onClick = onGoToNotificationSettings
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 精确闹钟权限
            if (!permissionState.hasExactAlarmPermission) {
                PermissionItem(
                    title = "精确闹钟权限",
                    description = "用于确保应用即使在后台或设备休眠时，也能准时触发闹钟。",
                    buttonText = "设置闹钟权限",
                    onClick = onGoToExactAlarmSettings
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 电池优化（可选）
            if (!permissionState.isBatteryOptimizationIgnored) {
                PermissionItem(
                    title = "电池优化白名单（推荐）",
                    description = "将应用加入电池优化白名单，可以进一步提高闹钟的准时率，防止系统为了省电而延迟提醒。",
                    buttonText = "设置电池优化",
                    onClick = onGoToBatteryOptimizationSettings
                )
            }
        }
    }
}

@Composable
private fun PermissionItem(
    title: String,
    description: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onClick) {
            Text(buttonText)
        }
    }
}
