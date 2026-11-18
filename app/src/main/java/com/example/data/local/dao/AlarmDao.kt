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
import androidx.room.Update
import com.example.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

  @Query("SELECT * FROM scheduled_alarms ORDER BY hour ASC, minute ASC, id DESC")
  fun getAllAlarms(): Flow<List<AlarmEntity>>

  @Query("SELECT * FROM scheduled_alarms WHERE id = :alarmId LIMIT 1")
  suspend fun getAlarmById(alarmId: Long): AlarmEntity?

  @Query("SELECT * FROM scheduled_alarms WHERE linkedHabitId = :habitId LIMIT 1")
  suspend fun getAlarmByHabitId(habitId: Long): AlarmEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAlarm(alarm: AlarmEntity): Long

  @Update
  suspend fun updateAlarm(alarm: AlarmEntity)

  @Query("DELETE FROM scheduled_alarms WHERE id = :alarmId")
  suspend fun deleteAlarmById(alarmId: Long)

  @Query("DELETE FROM scheduled_alarms WHERE linkedHabitId = :habitId")
  suspend fun deleteByHabitId(habitId: Long)
}
