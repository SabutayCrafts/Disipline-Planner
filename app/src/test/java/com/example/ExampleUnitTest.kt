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

import com.example.model.DisciplineRankCatalog
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
    // 0 or 1 days streak -> Level 1 (Novice)
    assertEquals(1, DisciplineRankCatalog.getCurrentRank(0).level)
    assertEquals(1, DisciplineRankCatalog.getCurrentRank(1).level)

    // 7 days -> Level 3 (Consistent)
    val rank7 = DisciplineRankCatalog.getCurrentRank(7)
    assertEquals(3, rank7.level)
    assertEquals("🔥", rank7.badgeIcon)

    // 30 days -> Level 7 (Unshakable)
    val rank30 = DisciplineRankCatalog.getCurrentRank(30)
    assertEquals(7, rank30.level)
    assertEquals("⚔️", rank30.badgeIcon)

    // 365 days -> Level 21 (One Year Legend)
    val rank365 = DisciplineRankCatalog.getCurrentRank(365)
    assertEquals(21, rank365.level)
    assertEquals("🌌", rank365.badgeIcon)

    // 1095 days -> Level 25 (Supreme Emperor)
    val rank1095 = DisciplineRankCatalog.getCurrentRank(1095)
    assertEquals(25, rank1095.level)
    assertEquals("🏛️", rank1095.badgeIcon)
  }

  @Test
  fun testGearUnlockRequirements() {
    // Laurel unlocks at 7 days
    assertFalse(DisciplineRankCatalog.isGearUnlocked("laurel", 6))
    assertTrue(DisciplineRankCatalog.isGearUnlocked("laurel", 7))

    // Spartan helmet unlocks at 30 days
    assertFalse(DisciplineRankCatalog.isGearUnlocked("spartan_helmet", 29))
    assertTrue(DisciplineRankCatalog.isGearUnlocked("spartan_helmet", 30))

    // Gold Crown unlocks at 60 days
    assertFalse(DisciplineRankCatalog.isGearUnlocked("gold_crown", 59))
    assertTrue(DisciplineRankCatalog.isGearUnlocked("gold_crown", 60))
  }

  @Test
  fun testNextRankCalculation() {
    val nextAfter1 = DisciplineRankCatalog.getNextRank(1)
    assertNotNull(nextAfter1)
    assertEquals(2, nextAfter1?.level)

    val nextAfter25 = DisciplineRankCatalog.getNextRank(25)
    assertNull(nextAfter25)
  }
}
