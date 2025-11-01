package com.busylab.todayalarm.domain.repository

import com.busylab.todayalarm.domain.model.Plan
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface PlanRepository {
    fun getAllPlans(): Flow<List<Plan>>
    fun getActivePlans(): Flow<List<Plan>>
    suspend fun getPlanById(id: String): Plan?
    fun getPlansInTimeRange(startTime: Long, endTime: Long): Flow<List<Plan>>
    suspend fun insertPlan(plan: Plan)
    suspend fun updatePlan(plan: Plan)
    suspend fun deletePlan(plan: Plan)
    suspend fun deletePlanById(id: String)
    suspend fun updatePlanActiveStatus(id: String, isActive: Boolean)

    // 系统集成层所需方法
    suspend fun getActiveActivePlans(): List<Plan>
    suspend fun getExpiredPlans(currentTime: LocalDateTime): List<Plan>
    suspend fun deleteInactivePlansBefore(cutoffTime: LocalDateTime)
}