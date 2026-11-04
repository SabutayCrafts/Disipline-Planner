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

package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {
  @Query("SELECT * FROM habit_logs WHERE dateString = :dateString")
  fun getLogsForDate(dateString: String): Flow<List<HabitLogEntity>>

  @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString LIMIT 1")
  suspend fun getLog(habitId: Long, dateString: String): HabitLogEntity?

  @Query("SELECT * FROM habit_logs ORDER BY completedAt DESC")
  fun getAllLogs(): Flow<List<HabitLogEntity>>

  @Query("SELECT DISTINCT dateString FROM habit_logs ORDER BY dateString DESC")
  fun getDistinctCompletedDates(): Flow<List<String>>

  @Query("SELECT COUNT(*) FROM habit_logs")
  fun getTotalCompletionsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: HabitLogEntity): Long

  @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateString = :dateString")
  suspend fun deleteLog(habitId: Long, dateString: String)

  @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
  suspend fun deleteLogsForHabit(habitId: Long)
}
