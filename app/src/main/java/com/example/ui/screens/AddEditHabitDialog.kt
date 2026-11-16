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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.alarm.AlarmHelper
import com.example.data.local.entity.HabitEntity
import com.example.model.HabitCategories
import com.example.ui.theme.AccentCrimson
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitDialog(
  initialHabit: HabitEntity? = null,
  onDismiss: () -> Unit,
  onSave: (
    title: String,
    description: String,
    category: String,
    reminderHour: Int?,
    reminderMinute: Int?,
    alarmEnabled: Boolean
  ) -> Unit
) {
  var title by remember { mutableStateOf(initialHabit?.title ?: "") }
  var description by remember { mutableStateOf(initialHabit?.description ?: "") }
  var selectedCategoryKey by remember {
    mutableStateOf(
      HabitCategories.ALL.firstOrNull {
        it.key.equals(initialHabit?.category, ignoreCase = true)
      }?.key ?: "health"
    )
  }
  var reminderHour by remember { mutableStateOf(initialHabit?.reminderHour) }
  var reminderMinute by remember { mutableStateOf(initialHabit?.reminderMinute) }
  var alarmEnabled by remember { mutableStateOf(initialHabit?.alarmEnabled ?: false) }
  var titleError by remember { mutableStateOf(false) }
  var showTimePicker by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceSlate,
    title = {
      Text(
        text = stringResource(
          if (initialHabit == null) R.string.dialog_new_habit else R.string.dialog_edit_habit
        ),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = {
            title = it
            if (it.isNotBlank()) titleError = false
          },
          label = { Text(stringResource(R.string.label_habit_title)) },
          isError = titleError,
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_title_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )
        if (titleError) {
          Text(
            text = stringResource(R.string.error_habit_title_empty),
            color = AccentCrimson,
            fontSize = 12.sp
          )
        }

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text(stringResource(R.string.label_habit_desc)) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_desc_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        Text(
          text = stringResource(R.string.label_category),
          style = MaterialTheme.typography.labelMedium,
          color = TextSecondary,
          fontWeight = FontWeight.SemiBold
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          HabitCategories.ALL.forEach { cat ->
            val isSelected = cat.key == selectedCategoryKey
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) PrimaryBlue else SurfaceCard)
                .border(
                  BorderStroke(1.dp, if (isSelected) PrimaryBlueLight else SurfaceBorder),
                  RoundedCornerShape(8.dp)
                )
                .clickable { selectedCategoryKey = cat.key }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = stringResource(cat.labelResId),
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextSecondary
              )
            }
          }
        }

        // Daily Alarm Reminder Configuration
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Alarm,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = stringResource(R.string.label_reminder_time),
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
              )
            }

            OutlinedButton(
              onClick = { showTimePicker = true },
              border = BorderStroke(1.dp, PrimaryBlueLight.copy(alpha = 0.6f)),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = if (reminderHour != null && reminderMinute != null) {
                  AlarmHelper.formatTime(reminderHour!!, reminderMinute!!)
                } else {
                  stringResource(R.string.label_pick_time)
                },
                color = PrimaryBlueLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (reminderHour != null && reminderMinute != null) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = stringResource(R.string.label_enable_alarm),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
              )
              Switch(
                checked = alarmEnabled,
                onCheckedChange = { alarmEnabled = it },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = AccentGold
                )
              )
            }
          } else {
            Text(
              text = stringResource(R.string.label_time_not_set),
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isBlank()) {
            titleError = true
          } else {
            onSave(
              title.trim(),
              description.trim(),
              selectedCategoryKey,
              reminderHour,
              reminderMinute,
              alarmEnabled && reminderHour != null
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier.testTag("save_habit_button")
      ) {
        Text(
          text = stringResource(
            if (alarmEnabled && reminderHour != null) R.string.label_sync_clock_now else R.string.save
          ),
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
      initialHour = reminderHour ?: 7,
      initialMinute = reminderMinute ?: 30,
      is24Hour = true
    )
    AlertDialog(
      onDismissRequest = { showTimePicker = false },
      containerColor = SurfaceSlate,
      title = {
        Text(
          text = stringResource(R.string.label_pick_time),
          color = TextPrimary,
          fontWeight = FontWeight.Bold
        )
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
            reminderHour = pickerState.hour
            reminderMinute = pickerState.minute
            alarmEnabled = true
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
