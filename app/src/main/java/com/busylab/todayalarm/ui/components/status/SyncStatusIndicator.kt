package com.busylab.todayalarm.ui.components.status

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.busylab.todayalarm.domain.manager.SyncStatus
import com.busylab.todayalarm.domain.manager.SyncStatusManager

@Composable
fun SyncStatusIndicator(
    syncStatusManager: SyncStatusManager,
    modifier: Modifier = Modifier
) {
    val syncStatus by syncStatusManager.syncStatus.collectAsState()

    if (syncStatus != SyncStatus.Idle) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (syncStatus) {
                    is SyncStatus.Syncing -> MaterialTheme.colorScheme.secondaryContainer
                    is SyncStatus.Success -> MaterialTheme.colorScheme.primaryContainer
                    is SyncStatus.Error -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 图标
                Icon(
                    imageVector = when (syncStatus) {
                        is SyncStatus.Syncing -> Icons.Default.Refresh
                        is SyncStatus.Success -> Icons.Default.CheckCircle
                        is SyncStatus.Error -> Icons.Default.Error
                        else -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = when (syncStatus) {
                        is SyncStatus.Syncing -> MaterialTheme.colorScheme.secondary
                        is SyncStatus.Success -> MaterialTheme.colorScheme.primary
                        is SyncStatus.Error -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // 文本
                Text(
                    text = when (syncStatus) {
                        is SyncStatus.Syncing -> "正在同步..."
                        is SyncStatus.Success -> (syncStatus as SyncStatus.Success).message ?: "同步完成"
                        is SyncStatus.Error -> (syncStatus as SyncStatus.Error).error
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (syncStatus) {
                        is SyncStatus.Syncing -> MaterialTheme.colorScheme.onSecondaryContainer
                        is SyncStatus.Success -> MaterialTheme.colorScheme.onPrimaryContainer
                        is SyncStatus.Error -> MaterialTheme.colorScheme.onErrorContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                // 进度指示器（仅在同步中显示）
                if (syncStatus is SyncStatus.Syncing) {
                    Spacer(modifier = Modifier.weight(1f))
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}