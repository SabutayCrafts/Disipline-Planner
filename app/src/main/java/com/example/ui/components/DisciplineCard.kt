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

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HabitItemUiState
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DisciplineCard(
  item: HabitItemUiState,
  onToggleComplete: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showMenu by remember { mutableStateOf(false) }

  val checkBgColor by animateColorAsState(
    targetValue = if (item.isCompletedToday) AccentEmerald else Color.Transparent,
    animationSpec = spring(),
    label = "check_bg"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("discipline_card_${item.habit.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (item.isCompletedToday) SurfaceSlate.copy(alpha = 0.85f) else SurfaceCard
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (item.isCompletedToday) AccentEmerald.copy(alpha = 0.5f) else SurfaceBorder
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Completion Toggle Button
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .border(
            width = 2.dp,
            color = if (item.isCompletedToday) AccentEmerald else SurfaceBorder,
            shape = CircleShape
          )
          .background(checkBgColor)
          .clickable(onClick = onToggleComplete)
          .testTag("toggle_complete_${item.habit.id}"),
        contentAlignment = Alignment.Center
      ) {
        if (item.isCompletedToday) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Tamamlandı",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      // 2. Title and metadata
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = item.habit.title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (item.isCompletedToday) TextDecoration.LineThrough else TextDecoration.None
          ),
          color = if (item.isCompletedToday) TextSecondary else TextPrimary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        if (item.habit.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = item.habit.description,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Category tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(PrimaryBlue.copy(alpha = 0.18f))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = item.habit.category,
              color = PrimaryBlue,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Streak badge
          if (item.currentStreak > 0) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(AccentGold.copy(alpha = 0.16f))
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = "🔥 ${item.currentStreak} gün",
                color = AccentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // 3. Overflow menu
      Box {
        IconButton(
          onClick = { showMenu = true },
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "Seçimlər",
            tint = TextSecondary
          )
        }

        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false },
          modifier = Modifier.background(SurfaceSlate)
        ) {
          DropdownMenuItem(
            text = { Text("Düzəliş et", color = TextPrimary) },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary) },
            onClick = {
              showMenu = false
              onEdit()
            }
          )
          DropdownMenuItem(
            text = { Text("Sil", color = Color(0xFFEF4444)) },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444)) },
            onClick = {
              showMenu = false
              onDelete()
            }
          )
        }
      }
    }
  }
}
