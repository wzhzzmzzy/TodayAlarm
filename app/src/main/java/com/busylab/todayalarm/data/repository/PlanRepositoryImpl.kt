package com.busylab.todayalarm.data.repository

import com.busylab.todayalarm.data.database.dao.PlanDao
import com.busylab.todayalarm.data.mapper.PlanMapper.toDomainModel
import com.busylab.todayalarm.data.mapper.PlanMapper.toDomainModels
import com.busylab.todayalarm.data.mapper.PlanMapper.toEntityModel
import com.busylab.todayalarm.domain.model.Plan
import com.busylab.todayalarm.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanRepositoryImpl @Inject constructor(
    private val planDao: PlanDao
) : PlanRepository {

    override fun getAllPlans(): Flow<List<Plan>> =
        planDao.getAllPlans().map { it.toDomainModels() }

    override fun getActivePlans(): Flow<List<Plan>> =
        planDao.getActivePlans().map { it.toDomainModels() }

    override suspend fun getPlanById(id: String): Plan? =
        planDao.getPlanById(id)?.toDomainModel()

    override fun getPlansInTimeRange(startTime: Long, endTime: Long): Flow<List<Plan>> =
        planDao.getPlansInTimeRange(startTime, endTime).map { it.toDomainModels() }

    override suspend fun insertPlan(plan: Plan) =
        planDao.insertPlan(plan.toEntityModel())

    override suspend fun updatePlan(plan: Plan) {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val updatedPlan = plan.copy(updatedAt = currentTime)
        planDao.updatePlan(updatedPlan.toEntityModel())
    }

    override suspend fun deletePlan(plan: Plan) =
        planDao.deletePlan(plan.toEntityModel())

    override suspend fun deletePlanById(id: String) =
        planDao.deletePlanById(id)

    override suspend fun updatePlanActiveStatus(id: String, isActive: Boolean) {
        planDao.updatePlanActiveStatus(id, isActive)
        planDao.updatePlanTimestamp(id)
    }

    // 系统集成层所需方法
    override suspend fun getActiveActivePlans(): List<Plan> {
        return planDao.getActivePlansSync().toDomainModels()
    }

    override suspend fun getExpiredPlans(currentTime: LocalDateTime): List<Plan> {
        val timeZone = TimeZone.currentSystemDefault()
        val timestamp = currentTime.toInstant(timeZone).toEpochMilliseconds()
        return planDao.getExpiredPlans(timestamp).toDomainModels()
    }

    override suspend fun deleteInactivePlansBefore(cutoffTime: LocalDateTime) {
        val timeZone = TimeZone.currentSystemDefault()
        val timestamp = cutoffTime.toInstant(timeZone).toEpochMilliseconds()
        planDao.deleteInactivePlansBefore(timestamp)
    }
}