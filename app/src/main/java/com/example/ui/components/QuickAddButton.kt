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

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight

@Composable
fun QuickAddButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Squarish card-shaped button: width 56dp, height 52dp, border-radius 12dp
  Box(
    modifier = modifier
      .size(width = 56.dp, height = 52.dp)
      .shadow(8.dp, shape = RoundedCornerShape(12.dp), spotColor = PrimaryBlue)
      .clip(RoundedCornerShape(12.dp))
      .background(PrimaryBlue)
      .clickable(onClick = onClick)
      .testTag("quick_add_discipline_button"),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = Icons.Default.Add,
      contentDescription = "Yeni Vərdiş Əlavə Et",
      tint = Color.White,
      modifier = Modifier.size(28.dp)
    )
  }
}
