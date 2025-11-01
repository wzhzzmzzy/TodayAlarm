package com.busylab.todayalarm.ui.components.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.busylab.todayalarm.domain.model.RepeatType
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme

@Composable
fun RepeatSettings(
    isRepeating: Boolean,
    repeatType: RepeatType,
    repeatInterval: Int,
    onRepeatToggled: (Boolean) -> Unit,
    onRepeatTypeChanged: (RepeatType) -> Unit,
    onRepeatIntervalChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    intervalError: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 重复开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "重复提醒",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Switch(
                    checked = isRepeating,
                    onCheckedChange = onRepeatToggled
                )
            }

            if (isRepeating) {
                Spacer(modifier = Modifier.height(16.dp))

                // 重复类型选择
                Text(
                    text = "重复类型",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val repeatOptions = listOf(
                    RepeatType.DAILY to "每天",
                    RepeatType.WEEKLY to "每周",
                    RepeatType.MONTHLY to "每月",
                    RepeatType.YEARLY to "每年"
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeatOptions.forEach { (type, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = repeatType == type,
                                    onClick = { onRepeatTypeChanged(type) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = repeatType == type,
                                onClick = { onRepeatTypeChanged(type) }
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 重复间隔设置
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "每",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = repeatInterval.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { interval ->
                                if (interval > 0) {
                                    onRepeatIntervalChanged(interval)
                                }
                            }
                        },
                        modifier = Modifier.width(80.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = intervalError != null
                    )

                    Text(
                        text = getIntervalUnit(repeatType),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (intervalError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = intervalError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 重复说明
                Text(
                    text = getRepeatDescription(repeatType, repeatInterval),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getIntervalUnit(repeatType: RepeatType): String {
    return when (repeatType) {
        RepeatType.DAILY -> "天"
        RepeatType.WEEKLY -> "周"
        RepeatType.MONTHLY -> "月"
        RepeatType.YEARLY -> "年"
        else -> ""
    }
}

private fun getRepeatDescription(repeatType: RepeatType, interval: Int): String {
    return when (repeatType) {
        RepeatType.DAILY -> if (interval == 1) "每天重复" else "每${interval}天重复"
        RepeatType.WEEKLY -> if (interval == 1) "每周重复" else "每${interval}周重复"
        RepeatType.MONTHLY -> if (interval == 1) "每月重复" else "每${interval}月重复"
        RepeatType.YEARLY -> if (interval == 1) "每年重复" else "每${interval}年重复"
        else -> ""
    }
}

@Preview(showBackground = true)
@Composable
private fun RepeatSettingsPreview() {
    TodayAlarmTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RepeatSettings(
                isRepeating = false,
                repeatType = RepeatType.NONE,
                repeatInterval = 1,
                onRepeatToggled = {},
                onRepeatTypeChanged = {},
                onRepeatIntervalChanged = {}
            )

            RepeatSettings(
                isRepeating = true,
                repeatType = RepeatType.DAILY,
                repeatInterval = 2,
                onRepeatToggled = {},
                onRepeatTypeChanged = {},
                onRepeatIntervalChanged = {}
            )

            RepeatSettings(
                isRepeating = true,
                repeatType = RepeatType.WEEKLY,
                repeatInterval = 1,
                onRepeatToggled = {},
                onRepeatTypeChanged = {},
                onRepeatIntervalChanged = {},
                intervalError = "间隔必须大于0"
            )
        }
    }
}