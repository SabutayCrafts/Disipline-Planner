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

import com.example.data.local.dao.AlarmDao
import com.example.data.local.dao.FocusSessionDao
import com.example.data.local.dao.HabitDao
import com.example.data.local.dao.HabitLogDao
import com.example.data.local.entity.AlarmEntity
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DisciplineRepository(
  private val habitDao: HabitDao,
  private val habitLogDao: HabitLogDao,
  private val focusSessionDao: FocusSessionDao,
  private val alarmDao: AlarmDao
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

  fun getAllFocusSessions(): Flow<List<FocusSessionEntity>> = focusSessionDao.getAllFocusSessions()

  fun getTotalFocusMinutes(): Flow<Int> = focusSessionDao.getTotalFocusMinutes()

  fun getAllAlarms(): Flow<List<AlarmEntity>> = alarmDao.getAllAlarms()

  suspend fun getAlarmById(alarmId: Long): AlarmEntity? = alarmDao.getAlarmById(alarmId)

  suspend fun getAlarmByHabitId(habitId: Long): AlarmEntity? = alarmDao.getAlarmByHabitId(habitId)

  suspend fun insertOrUpdateAlarm(alarm: AlarmEntity): Long = alarmDao.insertAlarm(alarm)

  suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)

  suspend fun deleteAlarm(alarmId: Long) = alarmDao.deleteAlarmById(alarmId)

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
    alarmDao.deleteByHabitId(habitId)
    habitDao.deleteHabitById(habitId)
  }

  suspend fun insertFocusSession(durationMinutes: Int, sessionType: String = "Focus"): Long {
    val session = FocusSessionEntity(
      durationMinutes = durationMinutes,
      sessionType = sessionType,
      dateString = getTodayDateString()
    )
    return focusSessionDao.insertSession(session)
  }

  /**
   * Calculates consecutive days streak up to today or yesterday.
   */
  fun calculateStreak(distinctDates: List<String>): Int {
    if (distinctDates.isEmpty()) return 0
    val sortedDates = distinctDates.distinct().sortedDescending()

    val calendar = Calendar.getInstance()
    val today = dateFormat.format(calendar.time)

    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = dateFormat.format(calendar.time)

    var streak = 0
    val targetCal = Calendar.getInstance()

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
