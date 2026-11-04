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

package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Discipline Planner Core Palette (High-contrast, distraction-free dark theme)
val BackgroundNavy = Color(0xFF0B1120)
val SurfaceSlate = Color(0xFF1E293B)
val SurfaceCard = Color(0xFF161F30)
val SurfaceBorder = Color(0xFF334155)

val PrimaryBlue = Color(0xFF2563EB)
val PrimaryBlueLight = Color(0xFF3B82F6)
val PrimaryBlueGlow = Color(0xFF1D4ED8)

val AccentGold = Color(0xFFF59E0B)
val AccentEmerald = Color(0xFF10B981)
val AccentCrimson = Color(0xFFEF4444)
val AccentPurple = Color(0xFF8B5CF6)
val AccentCyan = Color(0xFF06B6D4)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// M3 Color Scheme compatibility
val DarkPrimary = PrimaryBlueLight
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkPrimaryContainer = PrimaryBlue
val DarkOnPrimaryContainer = Color(0xFFDBEAFE)

val DarkSecondary = AccentCyan
val DarkOnSecondary = Color(0xFF0F172A)
val DarkSecondaryContainer = SurfaceCard
val DarkOnSecondaryContainer = TextPrimary

val DarkBackground = BackgroundNavy
val DarkOnBackground = TextPrimary
val DarkSurface = SurfaceSlate
val DarkOnSurface = TextPrimary
val DarkOutline = SurfaceBorder
