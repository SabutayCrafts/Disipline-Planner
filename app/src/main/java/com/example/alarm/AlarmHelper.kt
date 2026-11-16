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

package com.example.alarm

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock

object AlarmHelper {

  /**
   * Launches the device's built-in Clock application pre-filled with the habit's
   * scheduled time and label so it integrates directly with system alarms.
   */
  fun scheduleSystemAlarm(
    context: Context,
    habitTitle: String,
    hour: Int,
    minute: Int
  ): Boolean {
    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
      putExtra(AlarmClock.EXTRA_HOUR, hour.coerceIn(0, 23))
      putExtra(AlarmClock.EXTRA_MINUTES, minute.coerceIn(0, 59))
      putExtra(AlarmClock.EXTRA_MESSAGE, habitTitle)
      putExtra(AlarmClock.EXTRA_SKIP_UI, false)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    return try {
      context.startActivity(intent)
      true
    } catch (_: ActivityNotFoundException) {
      false
    } catch (_: Exception) {
      false
    }
  }

  fun formatTime(hour: Int, minute: Int): String {
    return String.format("%02d:%02d", hour, minute)
  }
}
