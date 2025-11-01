package com.busylab.todayalarm.ui.components.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.domain.model.PlanUiModel
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun PlanCard(
    plan: PlanUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isActive)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 标题和开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (plan.isActive)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = plan.isActive,
                    onCheckedChange = onToggle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 内容
            Text(
                text = plan.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (plan.isActive)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 时间信息
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${plan.formattedDate} ${plan.formattedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (plan.isRepeating) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = "重复提醒",
                        tint = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = getRepeatTypeText(plan.repeatType, plan.repeatInterval),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 状态信息
            if (plan.isOverdue && plan.isActive) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "已过期",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun getRepeatTypeText(repeatType: RepeatType, interval: Int): String {
    return when (repeatType) {
        RepeatType.NONE -> ""
        RepeatType.DAILY -> if (interval == 1) "每天" else "每${interval}天"
        RepeatType.WEEKLY -> if (interval == 1) "每周" else "每${interval}周"
        RepeatType.MONTHLY -> if (interval == 1) "每月" else "每${interval}月"
        RepeatType.YEARLY -> if (interval == 1) "每年" else "每${interval}年"
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanCardPreview() {
    TodayAlarmTheme {
        PlanCard(
            plan = PlanUiModel(
                id = "1",
                title = "晨间锻炼",
                content = "每天早上7点进行30分钟的有氧运动，保持身体健康",
                triggerTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                isRepeating = true,
                repeatType = RepeatType.DAILY,
                repeatInterval = 1,
                isActive = true,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                formattedTime = "07:00",
                formattedDate = "今天",
                isOverdue = false
            ),
            onEdit = {},
            onDelete = {},
            onToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanCardInactivePreview() {
    TodayAlarmTheme {
        PlanCard(
            plan = PlanUiModel(
                id = "1",
                title = "已停用的计划",
                content = "这是一个已经停用的计划",
                triggerTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                isRepeating = false,
                repeatType = RepeatType.NONE,
                repeatInterval = 1,
                isActive = false,
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                updatedAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                formattedTime = "10:00",
                formattedDate = "明天",
                isOverdue = false
            ),
            onEdit = {},
            onDelete = {},
            onToggle = {}
        )
    }
}