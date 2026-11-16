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

package com.example.focus

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FocusTimerState(
  val selectedMinutes: Int = 25,
  val totalSeconds: Long = 25L * 60L,
  val remainingSeconds: Long = 25L * 60L,
  val isRunning: Boolean = false,
  val isPaused: Boolean = false,
  val dndActive: Boolean = false,
  val interceptedAppName: String? = null,
  val completedSessionMinutes: Int? = null
)

object FocusStateHolder {
  private val _state = MutableStateFlow(FocusTimerState())
  val state: StateFlow<FocusTimerState> = _state.asStateFlow()

  fun selectDuration(minutes: Int) {
    val clamped = minutes.coerceIn(1, 180)
    _state.update { current ->
      if (current.isRunning) current
      else current.copy(
        selectedMinutes = clamped,
        totalSeconds = clamped * 60L,
        remainingSeconds = clamped * 60L,
        isPaused = false
      )
    }
  }

  fun startSession(dndActive: Boolean) {
    _state.update {
      it.copy(
        isRunning = true,
        isPaused = false,
        dndActive = dndActive,
        interceptedAppName = null,
        completedSessionMinutes = null
      )
    }
  }

  fun pauseSession() {
    _state.update { it.copy(isPaused = true) }
  }

  fun resumeSession() {
    _state.update { it.copy(isPaused = false) }
  }

  fun tickSecond() {
    _state.update {
      if (it.isRunning && !it.isPaused && it.remainingSeconds > 0) {
        it.copy(remainingSeconds = it.remainingSeconds - 1)
      } else it
    }
  }

  fun markInterceptedApp(appName: String?) {
    _state.update { it.copy(interceptedAppName = appName) }
  }

  fun completeSession() {
    _state.update {
      it.copy(
        isRunning = false,
        isPaused = false,
        dndActive = false,
        interceptedAppName = null,
        completedSessionMinutes = it.selectedMinutes,
        remainingSeconds = it.totalSeconds
      )
    }
  }

  fun resetSession() {
    _state.update {
      it.copy(
        isRunning = false,
        isPaused = false,
        dndActive = false,
        interceptedAppName = null,
        remainingSeconds = it.totalSeconds
      )
    }
  }

  fun acknowledgeCompletion() {
    _state.update { it.copy(completedSessionMinutes = null) }
  }
}
