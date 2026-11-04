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

data class DisciplineRank(
  val level: Int,
  val requiredDays: Int,
  val titleAz: String,
  val titleEn: String,
  val badgeIcon: String,
  val unlockedRewardName: String? = null,
  val rewardGearId: String? = null
)

object DisciplineRankCatalog {
  val ALL_RANKS = listOf(
    DisciplineRank(1, 1, "Yeni Başlayan", "Novice", "🌱"),
    DisciplineRank(2, 3, "İlk Qığılcım", "First Spark", "🌿"),
    DisciplineRank(3, 7, "Ardıcıl (1 Həftə)", "Consistent", "🔥", "Dəfnə Çələngi (Laurel)", "laurel"),
    DisciplineRank(4, 10, "Qüvvə Yığıcısı", "Momentum Builder", "⚡"),
    DisciplineRank(5, 14, "Dəmir Bünövrə (2 Həftə)", "Iron Foundation", "🛡️", "Cəngavər Dəbilqəsi", "knight_helm"),
    DisciplineRank(6, 21, "Vərdiş Memarı (Bürünc)", "Habit Architect", "🥉"),
    DisciplineRank(7, 30, "Sarsılmaz (1 Ay)", "Unshakable", "⚔️", "Spartalı Müharibə Dəbilqəsi", "spartan_helmet"),
    DisciplineRank(8, 45, "Fokus Ustası (Gümüş)", "Focus Master", "🥈"),
    DisciplineRank(9, 60, "Dözümlülük Titanı (Qızıl)", "Resilience Titan", "🥇", "Hökmdar Qızıl Tacı", "gold_crown"),
    DisciplineRank(10, 75, "Ağıl Heykəltəraşı", "Mind Sculptor", "⚜️", "Mistik Hələqə (Halo)", "mystic_halo"),
    DisciplineRank(11, 90, "Rüb Komandiri (1 Rüb)", "Quarter Master", "🏆"),
    DisciplineRank(12, 115, "Nəfsin Ağası", "Master of Self", "🔱"),
    DisciplineRank(13, 135, "Polad İradə Hökmranı", "Iron Will Sovereign", "👑"),
    DisciplineRank(14, 160, "Almaz Zəka", "Diamond Mind", "💎"),
    DisciplineRank(15, 180, "Yarım-İl Fatehi (6 Ay)", "Half-Year Overlord", "🔮"),
    DisciplineRank(16, 210, "Dayandırıla Bilməz", "Unstoppable Force", "🌟"),
    DisciplineRank(17, 240, "Ucalmış Nümunə", "Ascended Paragon", "💫"),
    DisciplineRank(18, 270, "Zirvə İntizamçısı (9 Ay)", "Apex Disciplinarian", "🌠"),
    DisciplineRank(19, 300, "Qrossmeyster", "Grandmaster", "🔱"),
    DisciplineRank(20, 330, "Sərhədsiz", "Limitless", "🚀"),
    DisciplineRank(21, 365, "Bir İllik Əfsanə (1 İl)", "One Year Legend", "🌌", "Ali İmperator Tacı", "emperor_crown"),
    DisciplineRank(22, 500, "Əbədi Cəngavər", "Eternal Warrior", "🎖️"),
    DisciplineRank(23, 730, "İki İllik Fatehi (2 İl)", "Two Year Conqueror", "🪐"),
    DisciplineRank(24, 900, "Ölməz Titan", "Immortal Titan", "☀️"),
    DisciplineRank(25, 1095, "ALİ İMPERATOR (3+ İl)", "THE SUPREME EMPEROR", "🏛️")
  )

  fun getCurrentRank(streakDays: Int): DisciplineRank {
    return ALL_RANKS.lastOrNull { streakDays >= it.requiredDays } ?: ALL_RANKS.first()
  }

  fun getNextRank(currentLevel: Int): DisciplineRank? {
    return ALL_RANKS.firstOrNull { it.level > currentLevel }
  }

  fun isGearUnlocked(gearId: String, currentStreak: Int): Boolean {
    val rankWithGear = ALL_RANKS.firstOrNull { it.rewardGearId == gearId } ?: return true
    return currentStreak >= rankWithGear.requiredDays
  }
}
