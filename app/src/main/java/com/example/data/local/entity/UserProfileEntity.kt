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

package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey
  val id: Int = 1,
  val name: String = "İntizam Döyüşçüsü",
  val purpose: String = "Hər gün daha güclü və fokuslu olmaq",
  val statusTag: String = "⚡ Dərin Fokus",
  val hairStyle: String = "quiff",        // quiff, afro, topknot, side_part, fade, clean
  val facialHair: String = "clean",      // clean, stubble, full_beard, mustache, goatee
  val eyewear: String = "none",          // none, round_specs, bold_frames, aviator, cyber_visor
  val outfit: String = "armor",          // armor, hoodie, tee, blazer, martial_gi
  val skinToneHex: String = "#F5D0A9",   // 8 presets
  val auraTheme: String = "royal_navy",  // royal_navy, spartan_fire, cosmic_violet, emerald, gold_dawn
  val equippedHeadwear: String = "none"  // none, laurel, knight_helm, spartan_helmet, gold_crown, mystic_halo, emperor_crown
)
