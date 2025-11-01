package com.busylab.todayalarm.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * TodayAlarm Material 3 Shapes
 * 定义应用中使用的形状系统
 */
val Shapes = Shapes(
    // 小型组件形状 (按钮、芯片等)
    small = RoundedCornerShape(4.dp),

    // 中型组件形状 (卡片、对话框等)
    medium = RoundedCornerShape(12.dp),

    // 大型组件形状 (底部表单、导航抽屉等)
    large = RoundedCornerShape(16.dp)
)

/**
 * 自定义形状定义
 */
object CustomShapes {
    val extraSmall = RoundedCornerShape(2.dp)
    val small = RoundedCornerShape(4.dp)
    val medium = RoundedCornerShape(8.dp)
    val large = RoundedCornerShape(12.dp)
    val extraLarge = RoundedCornerShape(16.dp)
    val xxLarge = RoundedCornerShape(24.dp)

    // 特殊形状
    val bottomSheet = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    val topSheet = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )

    val leftSheet = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 16.dp
    )

    val rightSheet = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 0.dp
    )
}