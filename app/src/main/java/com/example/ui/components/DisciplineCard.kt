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
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Alarm
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.alarm.AlarmHelper
import com.example.model.HabitCategories
import com.example.model.HabitItemUiState
import com.example.ui.theme.AccentCrimson
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
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
  onSetAlarm: () -> Unit,
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

  val checkScale by animateFloatAsState(
    targetValue = if (item.isCompletedToday) 1.08f else 1f,
    animationSpec = spring(dampingRatio = 0.55f, stiffness = 420f),
    label = "check_scale"
  )

  val borderColor by animateColorAsState(
    targetValue = if (item.isCompletedToday) AccentEmerald.copy(alpha = 0.55f) else SurfaceBorder,
    animationSpec = spring(),
    label = "card_border"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("discipline_card_${item.habit.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (item.isCompletedToday) SurfaceSlate.copy(alpha = 0.65f) else SurfaceSlate
    ),
    border = BorderStroke(width = 1.dp, color = borderColor)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // 1. Completion Toggle Button with spring feedback
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .clickable(onClick = onToggleComplete)
          .testTag("toggle_complete_${item.habit.id}"),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .scale(checkScale)
            .size(30.dp)
            .clip(CircleShape)
            .border(
              width = 2.dp,
              color = if (item.isCompletedToday) AccentEmerald else PrimaryBlueLight.copy(alpha = 0.6f),
              shape = CircleShape
            )
            .background(checkBgColor),
          contentAlignment = Alignment.Center
        ) {
          if (item.isCompletedToday) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = stringResource(R.string.cd_completed),
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(6.dp))

      // 2. Title, Description & Badges
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
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Localized Category tag
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(PrimaryBlue.copy(alpha = 0.18f))
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = stringResource(HabitCategories.getLabelRes(item.habit.category)),
              color = PrimaryBlueLight,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Scheduled Alarm Time Badge OR Clear "+ Alarm" Action Pill
          val hasScheduledTime = item.habit.reminderHour != null && item.habit.reminderMinute != null
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(
                if (hasScheduledTime && item.habit.alarmEnabled) AccentGold.copy(alpha = 0.16f)
                else SurfaceCard
              )
              .border(
                1.dp,
                if (hasScheduledTime && item.habit.alarmEnabled) AccentGold.copy(alpha = 0.55f)
                else SurfaceBorder,
                RoundedCornerShape(8.dp)
              )
              .clickable(onClick = onSetAlarm)
              .padding(horizontal = 8.dp, vertical = 3.dp)
              .testTag("alarm_habit_${item.habit.id}")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = stringResource(R.string.cd_set_alarm),
                tint = if (hasScheduledTime && item.habit.alarmEnabled) AccentGold else PrimaryBlueLight,
                modifier = Modifier.size(13.dp)
              )
              Text(
                text = if (hasScheduledTime) {
                  AlarmHelper.formatTime(item.habit.reminderHour!!, item.habit.reminderMinute!!)
                } else {
                  stringResource(R.string.btn_add_alarm_chip)
                },
                color = if (hasScheduledTime && item.habit.alarmEnabled) AccentGold else PrimaryBlueLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
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
                text = "🔥 ${stringResource(R.string.days_count, item.currentStreak)}",
                color = AccentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      Box {
        IconButton(
          onClick = { showMenu = true },
          modifier = Modifier.size(40.dp)
        ) {
          Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.options),
            tint = TextSecondary
          )
        }

        DropdownMenu(
          expanded = showMenu,
          onDismissRequest = { showMenu = false },
          modifier = Modifier.background(SurfaceSlate)
        ) {
          DropdownMenuItem(
            text = { Text(stringResource(R.string.edit), color = TextPrimary) },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = TextSecondary) },
            onClick = {
              showMenu = false
              onEdit()
            }
          )
          DropdownMenuItem(
            text = { Text(stringResource(R.string.delete), color = AccentCrimson) },
            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = AccentCrimson) },
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
