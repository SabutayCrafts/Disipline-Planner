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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.alarm.AlarmHelper
import com.example.alarm.AlarmRepeatMode
import com.example.data.local.entity.HabitEntity
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuickAlarmDialog(
  habits: List<HabitEntity>,
  initialHabit: HabitEntity? = null,
  onScheduleAlarm: (
    linkedHabit: HabitEntity?,
    title: String,
    hour: Int,
    minute: Int,
    repeatMode: AlarmRepeatMode
  ) -> Unit,
  onDismiss: () -> Unit
) {
  var currentTime by remember { mutableStateOf(AlarmHelper.getCurrentHourMinute()) }
  LaunchedEffect(Unit) {
    while (true) {
      currentTime = AlarmHelper.getCurrentHourMinute()
      delay(15_000L)
    }
  }

  var selectedHabit by remember { mutableStateOf(initialHabit) }
  var alarmTitle by remember { mutableStateOf(initialHabit?.title ?: "") }
  var hour by remember {
    mutableIntStateOf(initialHabit?.reminderHour ?: currentTime.first)
  }
  var minute by remember {
    mutableIntStateOf(initialHabit?.reminderMinute ?: currentTime.second)
  }
  var repeatMode by remember { mutableStateOf(AlarmRepeatMode.EVERY_DAY) }
  var showTimePicker by remember { mutableStateOf(false) }

  val fallbackTitle = stringResource(R.string.app_name)

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceSlate,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Alarm,
          contentDescription = null,
          tint = AccentGold,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = stringResource(R.string.quick_alarm_dialog_title),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Live Current Phone Time Pill (tap to snap to current time)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(PrimaryBlue.copy(alpha = 0.14f))
            .border(1.dp, PrimaryBlueLight.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable {
              val now = AlarmHelper.getCurrentHourMinute()
              currentTime = now
              hour = now.first
              minute = now.second
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("quick_alarm_current_time"),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AccessTime,
              contentDescription = null,
              tint = PrimaryBlueLight,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = stringResource(R.string.current_time_label, currentTime.first, currentTime.second),
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = PrimaryBlueLight
            )
          }
          Text(
            text = AlarmHelper.formatTime(currentTime.first, currentTime.second),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        }

        // Optional link to an existing habit/task
        if (habits.isNotEmpty()) {
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            habits.take(6).forEach { habit ->
              val isSelected = selectedHabit?.id == habit.id
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) PrimaryBlue else SurfaceCard)
                  .border(
                    BorderStroke(1.dp, if (isSelected) PrimaryBlueLight else SurfaceBorder),
                    RoundedCornerShape(8.dp)
                  )
                  .clickable {
                    selectedHabit = habit
                    alarmTitle = habit.title
                    if (habit.reminderHour != null && habit.reminderMinute != null) {
                      hour = habit.reminderHour
                      minute = habit.reminderMinute
                    }
                  }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = habit.title,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else TextSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }

        // Alarm Title Input
        OutlinedTextField(
          value = alarmTitle,
          onValueChange = { alarmTitle = it },
          label = { Text(stringResource(R.string.quick_alarm_label_hint)) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_alarm_title_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentGold,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        // Prominent Selected Time Card + Custom Picker Button
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.5.dp, AccentGold.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .clickable { showTimePicker = true }
            .padding(horizontal = 14.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Schedule,
              contentDescription = null,
              tint = AccentGold,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = AlarmHelper.formatTime(hour, minute),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
          }

          OutlinedButton(
            onClick = { showTimePicker = true },
            border = BorderStroke(1.dp, PrimaryBlueLight),
            shape = RoundedCornerShape(10.dp)
          ) {
            Text(
              text = stringResource(R.string.quick_alarm_choose_custom),
              color = PrimaryBlueLight,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // 1-Tap Quick Time Presets
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = stringResource(R.string.quick_alarm_presets),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
          )

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            AlarmHelper.TIME_PRESETS.forEach { (presetH, presetM) ->
              val isSelected = hour == presetH && minute == presetM
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(9.dp))
                  .background(if (isSelected) AccentGold else SurfaceCard)
                  .border(
                    BorderStroke(1.dp, if (isSelected) AccentGold else SurfaceBorder),
                    RoundedCornerShape(9.dp)
                  )
                  .clickable {
                    hour = presetH
                    minute = presetM
                  }
                  .padding(horizontal = 12.dp, vertical = 7.dp)
              ) {
                Text(
                  text = AlarmHelper.formatTime(presetH, presetM),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.Black else TextPrimary
                )
              }
            }
          }
        }

        // Repeat Days Selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = stringResource(R.string.quick_alarm_repeat_label),
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val options = listOf(
              AlarmRepeatMode.ONCE to R.string.repeat_once,
              AlarmRepeatMode.EVERY_DAY to R.string.repeat_daily,
              AlarmRepeatMode.WEEKDAYS to R.string.repeat_weekdays
            )
            options.forEach { (mode, labelRes) ->
              val isSelected = repeatMode == mode
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(9.dp))
                  .background(if (isSelected) PrimaryBlue else SurfaceCard)
                  .border(
                    BorderStroke(1.dp, if (isSelected) PrimaryBlueLight else SurfaceBorder),
                    RoundedCornerShape(9.dp)
                  )
                  .clickable { repeatMode = mode }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = stringResource(labelRes),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else TextSecondary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val resolvedTitle = alarmTitle.trim().ifBlank { fallbackTitle }
          onScheduleAlarm(selectedHabit, resolvedTitle, hour, minute, repeatMode)
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier.testTag("confirm_quick_alarm_button")
      ) {
        Text(
          text = stringResource(R.string.btn_create_clock_alarm, hour, minute),
          color = Color.White,
          fontWeight = FontWeight.Bold
        )
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel), color = TextMuted)
      }
    }
  )

  if (showTimePicker) {
    val pickerState = rememberTimePickerState(
      initialHour = hour,
      initialMinute = minute,
      is24Hour = true
    )
    AlertDialog(
      onDismissRequest = { showTimePicker = false },
      containerColor = SurfaceSlate,
      title = {
        Column {
          Text(
            text = stringResource(R.string.label_pick_time),
            color = TextPrimary,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = stringResource(R.string.current_time_label, currentTime.first, currentTime.second),
            style = MaterialTheme.typography.labelMedium,
            color = PrimaryBlueLight
          )
        }
      },
      text = {
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          TimePicker(
            state = pickerState,
            colors = TimePickerDefaults.colors(
              clockDialColor = SurfaceCard,
              selectorColor = PrimaryBlue,
              timeSelectorSelectedContainerColor = PrimaryBlue,
              timeSelectorUnselectedContainerColor = SurfaceCard,
              timeSelectorSelectedContentColor = Color.White,
              timeSelectorUnselectedContentColor = TextSecondary
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            hour = pickerState.hour
            minute = pickerState.minute
            showTimePicker = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
          Text(stringResource(R.string.done), color = Color.White, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showTimePicker = false }) {
          Text(stringResource(R.string.cancel), color = TextMuted)
        }
      }
    )
  }
}
