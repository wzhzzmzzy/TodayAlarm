package com.busylab.todayalarm.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.busylab.todayalarm.data.database.entities.Plan
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @Query("SELECT * FROM plans ORDER BY triggerTime ASC")
    fun getAllPlans(): Flow<List<Plan>>

    @Query("SELECT * FROM plans WHERE isActive = 1 ORDER BY triggerTime ASC")
    fun getActivePlans(): Flow<List<Plan>>

    @Query("SELECT * FROM plans WHERE id = :id")
    suspend fun getPlanById(id: String): Plan?

    @Query("SELECT * FROM plans WHERE triggerTime BETWEEN :startTime AND :endTime")
    fun getPlansInTimeRange(startTime: Long, endTime: Long): Flow<List<Plan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: Plan)

    @Update
    suspend fun updatePlan(plan: Plan)

    @Delete
    suspend fun deletePlan(plan: Plan)

    @Query("DELETE FROM plans WHERE id = :id")
    suspend fun deletePlanById(id: String)

    @Query("UPDATE plans SET isActive = :isActive WHERE id = :id")
    suspend fun updatePlanActiveStatus(id: String, isActive: Boolean)

    @Query("UPDATE plans SET updatedAt = :updatedAt WHERE id = :id")
    suspend fun updatePlanTimestamp(id: String, updatedAt: Long = System.currentTimeMillis())

    // 系统集成层所需方法
    @Query("SELECT * FROM plans WHERE isActive = 1 ORDER BY triggerTime ASC")
    suspend fun getActivePlansSync(): List<Plan>

    @Query("SELECT * FROM plans WHERE triggerTime < :currentTime AND isActive = 1")
    suspend fun getExpiredPlans(currentTime: Long): List<Plan>

    @Query("DELETE FROM plans WHERE isActive = 0 AND updatedAt < :cutoffTime")
    suspend fun deleteInactivePlansBefore(cutoffTime: Long)
}