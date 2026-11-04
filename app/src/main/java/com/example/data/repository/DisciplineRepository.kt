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

package com.example.data.repository

import com.example.data.local.dao.FocusSessionDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.HabitLogDao
import com.example.data.local.dao.UserProfileDao
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DisciplineRepository(
  private val habitDao: HabitDao,
  private val habitLogDao: HabitLogDao,
  private val focusSessionDao: FocusSessionDao,
  private val userProfileDao: UserProfileDao
) {
  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

  fun getTodayDateString(): String {
    return dateFormat.format(Date())
  }

  fun getAllActiveHabits(): Flow<List<HabitEntity>> = habitDao.getAllActiveHabits()

  fun getLogsForDate(dateString: String): Flow<List<HabitLogEntity>> = habitLogDao.getLogsForDate(dateString)

  fun getAllLogs(): Flow<List<HabitLogEntity>> = habitLogDao.getAllLogs()

  fun getDistinctCompletedDates(): Flow<List<String>> = habitLogDao.getDistinctCompletedDates()

  fun getTotalCompletionsCount(): Flow<Int> = habitLogDao.getTotalCompletionsCount()

  fun getUserProfile(): Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

  fun getAllFocusSessions(): Flow<List<FocusSessionEntity>> = focusSessionDao.getAllFocusSessions()

  fun getTotalFocusMinutes(): Flow<Int> = focusSessionDao.getTotalFocusMinutes()

  suspend fun toggleHabitCompletion(habitId: Long, dateString: String = getTodayDateString()): Boolean {
    val existing = habitLogDao.getLog(habitId, dateString)
    return if (existing != null) {
      habitLogDao.deleteLog(habitId, dateString)
      false
    } else {
      habitLogDao.insertLog(HabitLogEntity(habitId = habitId, dateString = dateString))
      true
    }
  }

  suspend fun insertHabit(habit: HabitEntity): Long {
    return habitDao.insertHabit(habit)
  }

  suspend fun updateHabit(habit: HabitEntity) {
    habitDao.updateHabit(habit)
  }

  suspend fun deleteHabit(habitId: Long) {
    habitLogDao.deleteLogsForHabit(habitId)
    habitDao.deleteHabitById(habitId)
  }

  suspend fun insertFocusSession(durationMinutes: Int, sessionType: String = "Fokus"): Long {
    val session = FocusSessionEntity(
      durationMinutes = durationMinutes,
      sessionType = sessionType,
      dateString = getTodayDateString()
    )
    return focusSessionDao.insertSession(session)
  }

  suspend fun saveUserProfile(profile: UserProfileEntity) {
    userProfileDao.insertOrUpdateProfile(profile)
  }

  suspend fun ensureProfileAndStarterData() {
    val profile = userProfileDao.getUserProfileOnce()
    if (profile == null) {
      userProfileDao.insertOrUpdateProfile(
        UserProfileEntity(
          name = "İntizam Döyüşçüsü",
          purpose = "Hər gün daha güclü, fokuslu və sarsılmaz olmaq",
          statusTag = "⚡ Dərin Fokus",
          hairStyle = "quiff",
          facialHair = "clean",
          eyewear = "none",
          outfit = "armor",
          skinToneHex = "#F5D0A9",
          auraTheme = "royal_navy",
          equippedHeadwear = "none"
        )
      )
    }
  }

  /**
   * Calculates consecutive days streak up to today or yesterday.
   */
  fun calculateStreak(distinctDates: List<String>): Int {
    if (distinctDates.isEmpty()) return 0
    val sortedDates = distinctDates.toSortedSet().reversed().toList()

    val calendar = Calendar.getInstance()
    val today = dateFormat.format(calendar.time)

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = dateFormat.format(calendar.time)

    var streak = 0
    val targetCal = Calendar.getInstance()

    // If today is in list, start counting from today. Else if yesterday is, start from yesterday.
    var currentCheckDate: String
    if (sortedDates.contains(today)) {
      currentCheckDate = today
    } else if (sortedDates.contains(yesterday)) {
      currentCheckDate = yesterday
      targetCal.add(Calendar.DAY_OF_YEAR, -1)
    } else {
      return 0
    }

    while (sortedDates.contains(currentCheckDate)) {
      streak++
      targetCal.add(Calendar.DAY_OF_YEAR, -1)
      currentCheckDate = dateFormat.format(targetCal.time)
    }

    return streak
  }
}
