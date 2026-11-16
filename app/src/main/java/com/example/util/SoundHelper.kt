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

package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SoundHelper {
  private val scope = CoroutineScope(Dispatchers.Default)

  fun playCompletionChime() {
    playTone(ToneGenerator.TONE_PROP_ACK, 160)
  }

  fun playFocusFinishedChime() {
    playTone(ToneGenerator.TONE_PROP_BEEP2, 450)
  }

  private fun playTone(toneType: Int, durationMs: Int) {
    scope.launch {
      var generator: ToneGenerator? = null
      try {
        generator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
        generator.startTone(toneType, durationMs)
        delay(durationMs + 100L)
      } catch (_: Exception) {
        // Audio hardware unavailable or muted in test environment
      } finally {
        try {
          generator?.release()
        } catch (_: Exception) {
        }
      }
    }
  }
}
