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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
  @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY id ASC")
  fun getAllActiveHabits(): Flow<List<HabitEntity>>

  @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
  suspend fun getHabitById(id: Long): HabitEntity?

  @Query("SELECT COUNT(*) FROM habits WHERE isArchived = 0")
  fun getActiveHabitsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabit(habit: HabitEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHabits(habits: List<HabitEntity>)

  @Update
  suspend fun updateHabit(habit: HabitEntity)

  @Delete
  suspend fun deleteHabit(habit: HabitEntity)

  @Query("DELETE FROM habits WHERE id = :id")
  suspend fun deleteHabitById(id: Long)
}
