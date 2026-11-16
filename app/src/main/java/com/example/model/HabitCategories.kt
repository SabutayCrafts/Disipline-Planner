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

data class HabitCategoryOption(
  val key: String,
  @StringRes val labelResId: Int
)

object HabitCategories {
  val ALL = listOf(
    HabitCategoryOption("health", R.string.cat_health),
    HabitCategoryOption("mind", R.string.cat_mind),
    HabitCategoryOption("productivity", R.string.cat_productivity),
    HabitCategoryOption("discipline", R.string.cat_discipline),
    HabitCategoryOption("fitness", R.string.cat_fitness),
    HabitCategoryOption("learning", R.string.cat_learning)
  )

  @StringRes
  fun getLabelRes(keyOrLegacy: String): Int {
    return when (keyOrLegacy.lowercase()) {
      "health", "sağlamlıq" -> R.string.cat_health
      "mind", "zehin & fokus" -> R.string.cat_mind
      "productivity", "məhsuldarlıq" -> R.string.cat_productivity
      "discipline", "intizam" -> R.string.cat_discipline
      "fitness", "idman" -> R.string.cat_fitness
      "learning", "öyrənmə" -> R.string.cat_learning
      else -> R.string.cat_discipline
    }
  }
}
