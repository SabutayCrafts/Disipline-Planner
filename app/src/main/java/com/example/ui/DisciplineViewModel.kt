/*
 * Discipline Planner - Daily Habit & Self-Mastery Platform
 * Copyright (C) 2026 Discipline Planner Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.repository.DisciplineRepository
import com.example.model.DisciplineRank
import com.example.model.DisciplineRankCatalog
import com.example.model.HabitItemUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DisciplineViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val repository = DisciplineRepository(
    database.habitDao(),
    database.habitLogDao(),
    database.focusSessionDao(),
    database.userProfileDao()
  )

  val todayDateString = repository.getTodayDateString()

  init {
    viewModelScope.launch {
      repository.ensureProfileAndStarterData()
    }
  }

  // User Profile
  val userProfile: StateFlow<UserProfileEntity> = repository.getUserProfile()
    .filterNotNull()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = UserProfileEntity()
    )

  // Distinct completed dates for streak calculation
  val completedDates: StateFlow<List<String>> = repository.getDistinctCompletedDates()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Overall active streak
  val activeStreak: StateFlow<Int> = completedDates
    .combine(MutableStateFlow(Unit)) { dates, _ ->
      repository.calculateStreak(dates)
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  // Current Rank and Next Rank in 25-tier catalog
  val currentRank: StateFlow<DisciplineRank> = activeStreak
    .combine(MutableStateFlow(Unit)) { streak, _ ->
      DisciplineRankCatalog.getCurrentRank(streak)
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = DisciplineRankCatalog.ALL_RANKS.first()
    )

  val nextRank: StateFlow<DisciplineRank?> = currentRank
    .combine(MutableStateFlow(Unit)) { rank, _ ->
      DisciplineRankCatalog.getNextRank(rank.level)
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = DisciplineRankCatalog.ALL_RANKS[1]
    )

  // Active habits list combined with today's completion logs and individual streaks
  val habitsUiState: StateFlow<List<HabitItemUiState>> = combine(
    repository.getAllActiveHabits(),
    repository.getLogsForDate(todayDateString),
    repository.getAllLogs()
  ) { habits, todayLogs, allLogs ->
    val completedHabitIdsToday = todayLogs.map { it.habitId }.toSet()

    habits.map { habit ->
      val habitLogs = allLogs.filter { it.habitId == habit.id }
      val habitDates = habitLogs.map { it.dateString }
      val streak = repository.calculateStreak(habitDates)
      val isCompleted = completedHabitIdsToday.contains(habit.id)

      HabitItemUiState(
        habit = habit,
        isCompletedToday = isCompleted,
        currentStreak = streak,
        totalCompletions = habitLogs.size
      )
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Focus Sessions
  val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.getAllFocusSessions()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val totalFocusMinutes: StateFlow<Int> = repository.getTotalFocusMinutes()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  val totalCompletions: StateFlow<Int> = repository.getTotalCompletionsCount()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  fun toggleHabit(habitId: Long) {
    viewModelScope.launch {
      repository.toggleHabitCompletion(habitId, todayDateString)
    }
  }

  fun addHabit(title: String, description: String, category: String, targetDays: Int) {
    viewModelScope.launch {
      repository.insertHabit(
        HabitEntity(
          title = title,
          description = description,
          category = category,
          targetDaysPerWeek = targetDays
        )
      )
    }
  }

  fun updateHabit(habit: HabitEntity) {
    viewModelScope.launch {
      repository.updateHabit(habit)
    }
  }

  fun deleteHabit(habitId: Long) {
    viewModelScope.launch {
      repository.deleteHabit(habitId)
    }
  }

  fun saveFocusSession(durationMinutes: Int) {
    viewModelScope.launch {
      repository.insertFocusSession(durationMinutes)
    }
  }

  fun saveUserProfile(profile: UserProfileEntity) {
    viewModelScope.launch {
      repository.saveUserProfile(profile)
    }
  }
}
