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

data class AvatarOption(
  val id: String,
  val name: String,
  val iconEmoji: String,
  val requiredDays: Int = 0
)

data class SkinToneOption(
  val hex: String,
  val name: String
)

data class AuraOption(
  val id: String,
  val name: String,
  val startColorHex: String,
  val endColorHex: String
)

object AvatarPresets {
  val HAIR_STYLES = listOf(
    AvatarOption("quiff", "Müasir Quiff", "💈"),
    AvatarOption("afro", "Qıvrım Afro", "🌀"),
    AvatarOption("topknot", "Spartalı Düyün", "🗡️"),
    AvatarOption("side_part", "Klassik Yan Ayrım", "✂️"),
    AvatarOption("fade", "Fade Kəsim", "📐"),
    AvatarOption("clean", "Təmiz Qırxılmış", "✨")
  )

  val FACIAL_HAIR = listOf(
    AvatarOption("clean", "Təmiz Üz", "🪒"),
    AvatarOption("stubble", "Yüngül Saqqal", "🧔"),
    AvatarOption("full_beard", "Qalın Döyüşçü Saqqalı", "🧔‍♂️"),
    AvatarOption("mustache", "Klassik Bığ", "🥸"),
    AvatarOption("goatee", "Keçi Saqqal", "🐐")
  )

  val EYEWEAR = listOf(
    AvatarOption("none", "Yoxdur", "👁️"),
    AvatarOption("round_specs", "Dairəvi Eynək", "👓"),
    AvatarOption("bold_frames", "Müasir Çərçivə", "🕶️"),
    AvatarOption("aviator", "Aviator Eynək", "🕶️"),
    AvatarOption("cyber_visor", "Kiber HUD Vizor", "🥽")
  )

  val OUTFITS = listOf(
    AvatarOption("armor", "Spartalı Zirehi", "🛡️"),
    AvatarOption("hoodie", "İdmançı Hudisi", "🥋"),
    AvatarOption("tee", "Minimalist Köynək", "👕"),
    AvatarOption("blazer", "Rəsmi Pencək", "👔"),
    AvatarOption("martial_gi", "Döyüş Sənəti Kimonosu", "🥋")
  )

  val HEADWEAR = listOf(
    AvatarOption("none", "Yoxdur", "🚫", 0),
    AvatarOption("laurel", "Dəfnə Çələngi", "🌿", 7),
    AvatarOption("knight_helm", "Cəngavər Dəbilqəsi", "⚔️", 14),
    AvatarOption("spartan_helmet", "Spartalı Zireh Dəbilqəsi", "🛡️", 30),
    AvatarOption("gold_crown", "Qızıl Tac", "👑", 60),
    AvatarOption("mystic_halo", "Mistik Hələqə", "🌟", 75),
    AvatarOption("emperor_crown", "Ali İmperator Tacı", "🏛️", 365)
  )

  val SKIN_TONES = listOf(
    SkinToneOption("#FDDFD0", "Açıq Ağ"),
    SkinToneOption("#F5D0A9", "Təbii Bej"),
    SkinToneOption("#E0AC69", "İsti Qumlu"),
    SkinToneOption("#C68642", "Zeytuni"),
    SkinToneOption("#8D5524", "Şabalıdı"),
    SkinToneOption("#4A2C11", "Tünd Espresso"),
    SkinToneOption("#4E878C", "Titan Yaşılı"),
    SkinToneOption("#6D597A", "Mistik Bənövşəyi")
  )

  val AURAS = listOf(
    AuraOption("royal_navy", "Kral Donanması", "#1E3A8A", "#0B1120"),
    AuraOption("spartan_fire", "Sparta Alovu", "#DC2626", "#450A0A"),
    AuraOption("cosmic_violet", "Kosmik Bənövşə", "#7C3AED", "#1E1B4B"),
    AuraOption("emerald", "Zümrüd Qalxan", "#059669", "#064E3B"),
    AuraOption("gold_dawn", "Qızıl Şəfəq", "#D97706", "#78350F")
  )

  val STATUS_TAGS = listOf(
    "⚡ Dərin Fokus",
    "🔥 Odlu İntizam",
    "🛡️ Polad İradə",
    "🧘 Zen Sükunəti",
    "⚔️ Döyüş Rejimində",
    "🚀 Dayandırıla Bilməz"
  )
}
