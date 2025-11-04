package com.busylab.todayalarm.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.busylab.todayalarm.ui.screens.AddPlanScreen
import com.busylab.todayalarm.ui.screens.AddTodoScreen
import com.busylab.todayalarm.ui.screens.EditPlanScreen
import com.busylab.todayalarm.ui.screens.HomeScreen
import com.busylab.todayalarm.ui.screens.PlanListScreen
import com.busylab.todayalarm.ui.screens.WeekViewScreen

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
                onNavigateToAddTodo = {
                    navController.navigate(Screen.AddTodo.route)
                },
                onNavigateToPlanList = {
                    navController.navigate(Screen.PlanList.route)
                },
                onNavigateToWeekView = {
                    navController.navigate(Screen.WeekView.route)
                },
                onNavigateToEditTodo = { todoId ->
                    navController.navigate(Screen.EditTodo.createRoute(todoId))
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

        // 周视图页面
        composable(Screen.WeekView.route) {
            WeekViewScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 添加待办页面
        composable(Screen.AddTodo.route) {
            AddTodoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 编辑待办页面
        composable(
            route = Screen.EditTodo.route,
            arguments = listOf(
                navArgument(NavigationArgs.TODO_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString(NavigationArgs.TODO_ID) ?: ""
            AddTodoScreen(
                todoId = todoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
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

fun NavHostController.navigateToAddTodo() {
    navigate(Screen.AddTodo.route)
}

fun NavHostController.navigateToEditTodo(todoId: String) {
    navigate(Screen.EditTodo.createRoute(todoId))
}

fun NavHostController.navigateToWeekView() {
    navigate(Screen.WeekView.route)
}

fun NavHostController.navigateBack() {
    val currentRoute = currentBackStackEntry?.destination?.route
    Log.i("Navigation", "popBackStack from $currentRoute")
    popBackStack()
}