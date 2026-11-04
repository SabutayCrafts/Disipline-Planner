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

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.HabitEntity
import com.example.model.HabitItemUiState
import com.example.ui.components.DisciplineCard
import com.example.ui.components.QuickAddButton
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundNavy
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
  habits: List<HabitItemUiState>,
  activeStreak: Int,
  onToggleHabit: (Long) -> Unit,
  onAddHabit: (title: String, desc: String, category: String, targetDays: Int) -> Unit,
  onUpdateHabit: (HabitEntity) -> Unit,
  onDeleteHabit: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddDialog by remember { mutableStateOf(false) }
  var habitToEdit by remember { mutableStateOf<HabitEntity?>(null) }

  val completedCount = habits.count { it.isCompletedToday }
  val totalCount = habits.size
  val progressPercent = if (totalCount > 0) (completedCount.toFloat() / totalCount.toFloat()) else 0f

  val todayFormatted = remember {
    SimpleDateFormat("EEEE, d MMMM", Locale("az")).format(Date())
      .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("az")) else it.toString() }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundNavy)
      .testTag("today_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      // Header Row with Date and Streak Flame Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Bugünkü İntizam",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = todayFormatted,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
          )
        }

        // Active Streak Pill
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AccentGold.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = AccentGold,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = " $activeStreak gün",
              color = AccentGold,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Today Progress Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Günün İrəliləyişi",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.SemiBold,
              color = TextPrimary
            )
            Text(
              text = "$completedCount / $totalCount tamamlandı",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold,
              color = if (totalCount > 0 && completedCount == totalCount) AccentEmerald else PrimaryBlue
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          LinearProgressIndicator(
            progress = { progressPercent },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = if (totalCount > 0 && completedCount == totalCount) AccentEmerald else PrimaryBlue,
            trackColor = SurfaceCard
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Habits List or Empty State
      if (habits.isEmpty()) {
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(SurfaceSlate),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(36.dp)
              )
            }
            Text(
              text = "Hələ ki heç bir vərdiş əlavə edilməyib",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = "Aşağıdakı '+' düyməsinə klikləyərək ilk gündəlik intizam vərdişinizi yaradın.",
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted,
              modifier = Modifier.padding(horizontal = 24.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 80.dp)
        ) {
          items(habits, key = { it.habit.id }) { item ->
            DisciplineCard(
              item = item,
              onToggleComplete = { onToggleHabit(item.habit.id) },
              onEdit = { habitToEdit = item.habit },
              onDelete = { onDeleteHabit(item.habit.id) }
            )
          }
        }
      }
    }

    // Broader Quick-Add Button (Card-shaped: 56dp x 52dp, rounded 12dp) at the bottom-end
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 20.dp)
    ) {
      QuickAddButton(
        onClick = { showAddDialog = true }
      )
    }

    // Add Habit Dialog
    if (showAddDialog) {
      AddEditHabitDialog(
        onDismiss = { showAddDialog = false },
        onSave = { title, desc, cat, targetDays ->
          onAddHabit(title, desc, cat, targetDays)
          showAddDialog = false
        }
      )
    }

    // Edit Habit Dialog
    habitToEdit?.let { habit ->
      AddEditHabitDialog(
        initialHabit = habit,
        onDismiss = { habitToEdit = null },
        onSave = { title, desc, cat, targetDays ->
          onUpdateHabit(habit.copy(title = title, description = desc, category = cat, targetDaysPerWeek = targetDays))
          habitToEdit = null
        }
      )
    }
  }
}
