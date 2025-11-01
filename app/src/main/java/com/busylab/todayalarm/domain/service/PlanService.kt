package com.busylab.todayalarm.domain.service

import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.repository.PlanRepository
import com.busylab.todayalarm.system.alarm.AlarmScheduler
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 计划服务
 * 负责协调计划的业务逻辑和系统集成
 */
@Singleton
class PlanService @Inject constructor(
    private val planRepository: PlanRepository,
    private val alarmScheduler: AlarmScheduler
) {

    /**
     * 创建计划并调度提醒
     */
    suspend fun createPlan(plan: Plan) {
        // 保存到数据库
        planRepository.insertPlan(plan)

        // 调度系统提醒
        if (plan.isActive) {
            alarmScheduler.scheduleAlarm(plan)
        }
    }

    /**
     * 更新计划并重新调度提醒
     */
    suspend fun updatePlan(plan: Plan) {
        // 更新数据库
        planRepository.updatePlan(plan)

        // 重新调度提醒
        if (plan.isActive) {
            alarmScheduler.rescheduleAlarm(plan)
        } else {
            alarmScheduler.cancelAlarm(plan.id)
        }
    }

    /**
     * 删除计划并取消提醒
     */
    suspend fun deletePlan(plan: Plan) {
        // 取消系统提醒
        alarmScheduler.cancelAlarm(plan.id)

        // 从数据库删除
        planRepository.deletePlan(plan)
    }

    /**
     * 根据ID删除计划
     */
    suspend fun deletePlanById(planId: String) {
        // 取消系统提醒
        alarmScheduler.cancelAlarm(planId)

        // 从数据库删除
        planRepository.deletePlanById(planId)
    }

    /**
     * 激活或停用计划
     */
    suspend fun togglePlanActiveStatus(planId: String, isActive: Boolean) {
        planRepository.updatePlanActiveStatus(planId, isActive)

        if (isActive) {
            val plan = planRepository.getPlanById(planId)
            plan?.let { alarmScheduler.scheduleAlarm(it) }
        } else {
            alarmScheduler.cancelAlarm(planId)
        }
    }
}