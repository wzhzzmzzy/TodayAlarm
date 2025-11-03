package com.busylab.todayalarm.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.busylab.todayalarm.ui.screens.AddPlanScreen
import com.busylab.todayalarm.ui.screens.EditPlanScreen
import com.busylab.todayalarm.ui.screens.HomeScreen
import com.busylab.todayalarm.ui.screens.PlanListScreen
import com.busylab.todayalarm.ui.screens.SimpleTodoListScreen

@Composable
fun TodayAlarmNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // 主页
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddPlan = {
                    navController.navigate(Screen.AddPlan.route)
                },
                onNavigateToPlanList = {
                    navController.navigate(Screen.PlanList.route)
                },
                onNavigateToTodoList = {
                    navController.navigate(Screen.TodoList.route)
                }
            )
        }

        // 添加计划页面
        composable(Screen.AddPlan.route) {
            AddPlanScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 计划列表页面
        composable(Screen.PlanList.route) {
            PlanListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAddPlan = {
                    navController.navigate(Screen.AddPlan.route)
                },
                onNavigateToEditPlan = { planId ->
                    navController.navigate(Screen.EditPlan.createRoute(planId))
                }
            )
        }

        // 编辑计划页面
        composable(
            route = Screen.EditPlan.route,
            arguments = listOf(
                navArgument(NavigationArgs.PLAN_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString(NavigationArgs.PLAN_ID) ?: ""
            EditPlanScreen(
                planId = planId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 待办列表页面
        composable(Screen.TodoList.route) {
            SimpleTodoListScreen(
            )
        }
    }
}

/**
 * 导航扩展函数
 */
fun NavHostController.navigateToAddPlan() {
    navigate(Screen.AddPlan.route)
}

fun NavHostController.navigateToPlanList() {
    navigate(Screen.PlanList.route)
}

fun NavHostController.navigateToEditPlan(planId: String) {
    navigate(Screen.EditPlan.createRoute(planId))
}

fun NavHostController.navigateToTodoList() {
    navigate(Screen.TodoList.route)
}

fun NavHostController.navigateBack() {
    popBackStack()
}