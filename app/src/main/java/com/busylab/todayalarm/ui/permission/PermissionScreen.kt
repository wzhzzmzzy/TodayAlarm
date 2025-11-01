package com.busylab.todayalarm.ui.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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

/**
 * 权限请求界面
 *
 * @param missingPermissions 缺失的权限列表
 * @param onGrantPermissionClick 点击授权按钮的回调
 */
@Composable
fun PermissionRequestScreen(
    missingPermissions: List<String>,
    onGrantPermissionClick: () -> Unit
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
                text = "权限请求",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "为了确保闹钟和通知能够正常工作，应用需要以下权限：",
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            missingPermissions.forEach { permission ->
                Text(
                    text = "• $permission",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onGrantPermissionClick) {
                Text("前往设置授权")
            }
        }
    }
}
