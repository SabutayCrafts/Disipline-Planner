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

package com.example.model

import androidx.annotation.StringRes
import com.example.R

data class DisciplineRank(
  val level: Int,
  val requiredDays: Int,
  @StringRes val titleResId: Int,
  val badgeIcon: String
)

object DisciplineRankCatalog {
  val ALL_RANKS = listOf(
    DisciplineRank(1, 1, R.string.rank_1, "🌱"),
    DisciplineRank(2, 3, R.string.rank_2, "🌿"),
    DisciplineRank(3, 7, R.string.rank_3, "🔥"),
    DisciplineRank(4, 10, R.string.rank_4, "⚡"),
    DisciplineRank(5, 14, R.string.rank_5, "🛡️"),
    DisciplineRank(6, 21, R.string.rank_6, "🥉"),
    DisciplineRank(7, 30, R.string.rank_7, "⚔️"),
    DisciplineRank(8, 45, R.string.rank_8, "🥈"),
    DisciplineRank(9, 60, R.string.rank_9, "🥇"),
    DisciplineRank(10, 75, R.string.rank_10, "⚜️"),
    DisciplineRank(11, 90, R.string.rank_11, "🏆"),
    DisciplineRank(12, 115, R.string.rank_12, "🔱"),
    DisciplineRank(13, 135, R.string.rank_13, "👑"),
    DisciplineRank(14, 160, R.string.rank_14, "💎"),
    DisciplineRank(15, 180, R.string.rank_15, "🔮"),
    DisciplineRank(16, 210, R.string.rank_16, "🌟"),
    DisciplineRank(17, 240, R.string.rank_17, "💫"),
    DisciplineRank(18, 270, R.string.rank_18, "🌠"),
    DisciplineRank(19, 300, R.string.rank_19, "🔱"),
    DisciplineRank(20, 330, R.string.rank_20, "🚀"),
    DisciplineRank(21, 365, R.string.rank_21, "🌌"),
    DisciplineRank(22, 500, R.string.rank_22, "🎖️"),
    DisciplineRank(23, 730, R.string.rank_23, "🪐"),
    DisciplineRank(24, 900, R.string.rank_24, "☀️"),
    DisciplineRank(25, 1095, R.string.rank_25, "🏛️")
  )

  fun getCurrentRank(streakDays: Int): DisciplineRank {
    return ALL_RANKS.lastOrNull { streakDays >= it.requiredDays } ?: ALL_RANKS.first()
  }

  fun getNextRank(currentLevel: Int): DisciplineRank? {
    return ALL_RANKS.firstOrNull { it.level > currentLevel }
  }
}
