package com.busylab.todayalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.busylab.todayalarm.ui.navigation.TodayAlarmNavigation
import com.busylab.todayalarm.ui.theme.TodayAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayAlarmTheme {
                TodayAlarmApp()
            }
        }
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