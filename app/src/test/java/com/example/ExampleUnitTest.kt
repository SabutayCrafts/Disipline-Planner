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

package com.example

import com.example.alarm.AlarmHelper
import com.example.alarm.AlarmRepeatMode
import com.example.focus.FocusPermissionsStatus
import com.example.focus.FocusStateHolder
import com.example.model.DisciplineRankCatalog
import com.example.model.HabitCategories
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testRankCatalogHas25Tiers() {
    assertEquals(25, DisciplineRankCatalog.ALL_RANKS.size)
    assertEquals(1, DisciplineRankCatalog.ALL_RANKS.first().level)
    assertEquals(25, DisciplineRankCatalog.ALL_RANKS.last().level)
    assertEquals(1095, DisciplineRankCatalog.ALL_RANKS.last().requiredDays)
  }

  @Test
  fun testRankProgression() {
    assertEquals(1, DisciplineRankCatalog.getCurrentRank(0).level)
    assertEquals(1, DisciplineRankCatalog.getCurrentRank(1).level)

    val rank7 = DisciplineRankCatalog.getCurrentRank(7)
    assertEquals(3, rank7.level)
    assertEquals("🔥", rank7.badgeIcon)

    val rank30 = DisciplineRankCatalog.getCurrentRank(30)
    assertEquals(7, rank30.level)
    assertEquals("⚔️", rank30.badgeIcon)

    val rank365 = DisciplineRankCatalog.getCurrentRank(365)
    assertEquals(21, rank365.level)
    assertEquals("🌌", rank365.badgeIcon)

    val rank1095 = DisciplineRankCatalog.getCurrentRank(1095)
    assertEquals(25, rank1095.level)
    assertEquals("🏛️", rank1095.badgeIcon)
  }

  @Test
  fun testNextRankCalculation() {
    val nextAfter1 = DisciplineRankCatalog.getNextRank(1)
    assertNotNull(nextAfter1)
    assertEquals(2, nextAfter1?.level)

    val nextAfter25 = DisciplineRankCatalog.getNextRank(25)
    assertNull(nextAfter25)
  }

  @Test
  fun testAlarmTimeFormattingRepeatModesAndCategories() {
    assertEquals("07:05", AlarmHelper.formatTime(7, 5))
    assertEquals("21:30", AlarmHelper.formatTime(21, 30))
    assertEquals(0, AlarmRepeatMode.ONCE.toCalendarDays().size)
    assertEquals(7, AlarmRepeatMode.EVERY_DAY.toCalendarDays().size)
    assertEquals(5, AlarmRepeatMode.WEEKDAYS.toCalendarDays().size)

    val (nowH, nowM) = AlarmHelper.getCurrentHourMinute()
    assertTrue(nowH in 0..23)
    assertTrue(nowM in 0..59)

    val now = 1_700_000_000_000L
    val nextTrigger = AlarmHelper.computeNextTriggerMillis(nowH, nowM, AlarmRepeatMode.EVERY_DAY, now)
    assertTrue(nextTrigger > now)

    assertEquals(R.string.cat_health, HabitCategories.getLabelRes("health"))
    assertEquals(R.string.cat_mind, HabitCategories.getLabelRes("Zehin & Fokus"))
  }

  @Test
  fun testFocusPermissionsMissingDetection() {
    val noneGranted = FocusPermissionsStatus(
      hasDnd = false,
      hasUsageStats = false,
      hasOverlay = false,
      hasNotifications = false
    )
    assertTrue(noneGranted.hasMissingPermissionsForFocus(dndEnabled = true, hasBlockedApps = false))

    val onlyDndMissing = FocusPermissionsStatus(
      hasDnd = false,
      hasUsageStats = true,
      hasOverlay = true,
      hasNotifications = true
    )
    assertTrue(onlyDndMissing.hasMissingPermissionsForFocus(dndEnabled = true, hasBlockedApps = false))
    assertFalse(onlyDndMissing.hasMissingPermissionsForFocus(dndEnabled = false, hasBlockedApps = true))

    val allGranted = FocusPermissionsStatus(
      hasDnd = true,
      hasUsageStats = true,
      hasOverlay = true,
      hasNotifications = true
    )
    assertFalse(allGranted.hasMissingPermissionsForFocus(dndEnabled = true, hasBlockedApps = true))
  }

  @Test
  fun testFocusStateHolderLifecycle() {
    FocusStateHolder.resetSession()
    FocusStateHolder.selectDuration(45)
    assertEquals(45, FocusStateHolder.state.value.selectedMinutes)
    assertEquals(45 * 60L, FocusStateHolder.state.value.remainingSeconds)

    FocusStateHolder.startSession(dndActive = true)
    assertTrue(FocusStateHolder.state.value.isRunning)
    assertTrue(FocusStateHolder.state.value.dndActive)

    FocusStateHolder.tickSecond()
    assertEquals(45 * 60L - 1, FocusStateHolder.state.value.remainingSeconds)

    FocusStateHolder.pauseSession()
    assertTrue(FocusStateHolder.state.value.isPaused)

    FocusStateHolder.resetSession()
    assertFalse(FocusStateHolder.state.value.isRunning)
    assertFalse(FocusStateHolder.state.value.dndActive)
  }
}
