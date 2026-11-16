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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.alarm.AlarmHelper
import com.example.data.local.entity.HabitEntity
import com.example.model.HabitItemUiState
import com.example.ui.components.DisciplineCard
import com.example.ui.components.QuickAddButton
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.BackgroundNavy
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
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
  languageCode: String,
  hapticsEnabled: Boolean,
  onToggleHabit: (Long) -> Unit,
  onAddHabit: (
    title: String,
    desc: String,
    category: String,
    reminderHour: Int?,
    reminderMinute: Int?,
    alarmEnabled: Boolean
  ) -> Unit,
  onUpdateHabit: (HabitEntity) -> Unit,
  onDeleteHabit: (Long) -> Unit,
  onShowMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptics = LocalHapticFeedback.current
  var showAddDialog by remember { mutableStateOf(false) }
  var habitToEdit by remember { mutableStateOf<HabitEntity?>(null) }

  val completedCount = habits.count { it.isCompletedToday }
  val totalCount = habits.size
  val rawProgress = if (totalCount > 0) (completedCount.toFloat() / totalCount.toFloat()) else 0f
  val animatedProgress by animateFloatAsState(
    targetValue = rawProgress,
    animationSpec = spring(dampingRatio = 0.75f, stiffness = 240f),
    label = "daily_progress_anim"
  )
  val progressPercent = (rawProgress * 100).toInt()
  val isAllCompleted = totalCount > 0 && completedCount == totalCount

  val todayFormatted = remember(languageCode) {
    val locale = Locale.forLanguageTag(languageCode)
    SimpleDateFormat("EEEE, d MMMM", locale).format(Date())
      .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
  }

  val msgCompleted = stringResource(R.string.habit_completed_toast)
  val msgSaved = stringResource(R.string.habit_saved_toast)
  val msgDeleted = stringResource(R.string.habit_deleted_toast)
  val msgAlarmMissing = stringResource(R.string.alarm_not_supported_toast)

  fun triggerClockAlarm(habitTitle: String, hour: Int, minute: Int) {
    val launched = AlarmHelper.scheduleSystemAlarm(context, habitTitle, hour, minute)
    if (launched) {
      onShowMessage(context.getString(R.string.alarm_launched_toast, hour, minute))
    } else {
      onShowMessage(msgAlarmMissing)
    }
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
      Spacer(modifier = Modifier.height(12.dp))

      // Header Subtitle
      Column {
        Text(
          text = stringResource(R.string.today_title),
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

      Spacer(modifier = Modifier.height(14.dp))

      // Daily Progress Card with Animated Circular Ring
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        border = BorderStroke(
          1.dp,
          if (isAllCompleted) AccentEmerald.copy(alpha = 0.6f) else SurfaceBorder
        )
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Circular Ring
          Box(
            modifier = Modifier.size(64.dp),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.size(64.dp)) {
              val stroke = 6.dp.toPx()
              drawCircle(
                color = SurfaceCard,
                style = Stroke(stroke)
              )
              drawArc(
                color = if (isAllCompleted) AccentEmerald else PrimaryBlueLight,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(stroke, cap = StrokeCap.Round)
              )
            }
            Text(
              text = "$progressPercent%",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = if (isAllCompleted) AccentEmerald else TextPrimary
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = stringResource(R.string.daily_progress),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = stringResource(R.string.completed_fraction, completedCount, totalCount),
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold,
              color = if (isAllCompleted) AccentEmerald else PrimaryBlueLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = stringResource(
                when {
                  isAllCompleted -> R.string.progress_complete_msg
                  completedCount > 0 -> R.string.progress_active_msg
                  else -> R.string.progress_start_msg
                }
              ),
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Habits List or Empty State
      AnimatedVisibility(
        visible = habits.isEmpty(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceSlate),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = PrimaryBlueLight,
                modifier = Modifier.size(36.dp)
              )
            }
            Text(
              text = stringResource(R.string.empty_habits_title),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Text(
              text = stringResource(R.string.empty_habits_subtitle),
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted,
              modifier = Modifier.padding(horizontal = 28.dp),
              textAlign = TextAlign.Center
            )
          }
        }
      }

      if (habits.isNotEmpty()) {
        LazyColumn(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          contentPadding = PaddingValues(bottom = 86.dp)
        ) {
          items(habits, key = { it.habit.id }) { item ->
            DisciplineCard(
              item = item,
              onToggleComplete = {
                if (hapticsEnabled) {
                  haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                if (!item.isCompletedToday) {
                  onShowMessage(msgCompleted)
                }
                onToggleHabit(item.habit.id)
              },
              onSetAlarm = {
                val hour = item.habit.reminderHour ?: 7
                val minute = item.habit.reminderMinute ?: 30
                triggerClockAlarm(item.habit.title, hour, minute)
              },
              onEdit = { habitToEdit = item.habit },
              onDelete = {
                onDeleteHabit(item.habit.id)
                onShowMessage(msgDeleted)
              }
            )
          }
        }
      }
    }

    // Broader Quick-Add Button (56dp x 52dp, 12dp corner radius)
    Box(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 20.dp, bottom = 20.dp)
    ) {
      QuickAddButton(
        onClick = {
          if (hapticsEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
          }
          showAddDialog = true
        }
      )
    }

    if (showAddDialog) {
      AddEditHabitDialog(
        onDismiss = { showAddDialog = false },
        onSave = { title, desc, cat, hour, minute, alarmEnabled ->
          onAddHabit(title, desc, cat, hour, minute, alarmEnabled)
          showAddDialog = false
          if (alarmEnabled && hour != null && minute != null) {
            triggerClockAlarm(title, hour, minute)
          } else {
            onShowMessage(msgSaved)
          }
        }
      )
    }

    habitToEdit?.let { habit ->
      AddEditHabitDialog(
        initialHabit = habit,
        onDismiss = { habitToEdit = null },
        onSave = { title, desc, cat, hour, minute, alarmEnabled ->
          onUpdateHabit(
            habit.copy(
              title = title,
              description = desc,
              category = cat,
              reminderHour = hour,
              reminderMinute = minute,
              alarmEnabled = alarmEnabled
            )
          )
          habitToEdit = null
          if (alarmEnabled && hour != null && minute != null) {
            triggerClockAlarm(title, hour, minute)
          } else {
            onShowMessage(msgSaved)
          }
        }
      )
    }
  }
}
