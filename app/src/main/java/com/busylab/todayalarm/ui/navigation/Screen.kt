package com.busylab.todayalarm.ui.navigation

/**
 * 应用导航路由定义
 */
sealed class Screen(val route: String) {
    /**
     * 主页
     */
    object Home : Screen("home")

    /**
     * 添加计划
     */
    object AddPlan : Screen("add_plan")

    /**
     * 计划列表
     */
    object PlanList : Screen("plan_list")

    /**
     * 编辑计划
     */
    object EditPlan : Screen("edit_plan/{planId}") {
        fun createRoute(planId: String) = "edit_plan/$planId"
    }

    /**
     * 待办列表
     */
    object TodoList : Screen("todo_list")

    /**
     * 周视图
     */
    object WeekView : Screen("week_view")

    companion object {
        /**
         * 获取所有路由
         */
        fun getAllRoutes(): List<String> = listOf(
            Home.route,
            AddPlan.route,
            PlanList.route,
            EditPlan.route,
            TodoList.route,
            WeekView.route
        )
    }
}

/**
 * 导航参数键
 */
object NavigationArgs {
    const val PLAN_ID = "planId"
}