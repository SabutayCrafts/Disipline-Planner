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
import com.example.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
  @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
  fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

  @Query("SELECT COALESCE(SUM(durationMinutes), 0) FROM focus_sessions")
  fun getTotalFocusMinutes(): Flow<Int>

  @Query("SELECT COUNT(*) FROM focus_sessions")
  fun getTotalFocusSessionsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSession(session: FocusSessionEntity): Long
}
