package com.busylab.todayalarm.domain.usecase.plan

import com.busylab.todayalarm.data.datastore.UserPreferencesDataStore
import com.busylab.todayalarm.domain.model.ModelMapper.toUiModel
import com.busylab.todayalarm.domain.model.PlanUiModel
import com.busylab.todayalarm.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetPlansUseCase @Inject constructor(
    private val planRepository: PlanRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    operator fun invoke(): Flow<List<PlanUiModel>> {
        return combine(
            planRepository.getAllPlans(),
            userPreferencesDataStore.userPreferences
        ) { plans, preferences ->
            plans.map { it.toUiModel(preferences) }
        }
    }

    fun getActivePlans(): Flow<List<PlanUiModel>> {
        return combine(
            planRepository.getActivePlans(),
            userPreferencesDataStore.userPreferences
        ) { plans, preferences ->
            plans.map { it.toUiModel(preferences) }
        }
    }

    fun getPlansForWeek(weekOffset: Int = 0): Flow<List<PlanUiModel>> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val startOfWeekInstant = now.toInstant(TimeZone.currentSystemDefault())
            .plus(weekOffset * 7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        val startOfWeek = startOfWeekInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            .let { date ->
                val instant = date.toInstant(TimeZone.currentSystemDefault())
                    .plus(-date.dayOfWeek.ordinal, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
                instant.toLocalDateTime(TimeZone.currentSystemDefault())
            }
        val endOfWeekInstant = startOfWeek.toInstant(TimeZone.currentSystemDefault())
            .plus(6, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        val endOfWeek = endOfWeekInstant.toLocalDateTime(TimeZone.currentSystemDefault())

        val startTime = startOfWeek.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endTime = endOfWeek.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()

        return combine(
            planRepository.getPlansInTimeRange(startTime, endTime),
            userPreferencesDataStore.userPreferences
        ) { plans, preferences ->
            plans.map { it.toUiModel(preferences) }
        }
    }
}