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

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.alarm.AlarmHelper
import com.example.alarm.AlarmRepeatMode
import com.example.data.local.entity.AlarmEntity
import com.example.data.local.entity.HabitEntity
import com.example.focus.FocusPermissionHelper
import com.example.model.HabitItemUiState
import com.example.ui.components.DisciplineCard
import com.example.ui.components.QuickAddButton
import com.example.ui.theme.AccentCrimson
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundNavy
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
  habits: List<HabitItemUiState>,
  scheduledAlarms: List<AlarmEntity>,
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
  onSaveAlarm: (
    title: String,
    hour: Int,
    minute: Int,
    repeatMode: AlarmRepeatMode,
    linkedHabitId: Long?
  ) -> Unit,
  onToggleAlarm: (AlarmEntity, Boolean) -> Unit,
  onDeleteAlarm: (AlarmEntity) -> Unit,
  onShowMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val haptics = LocalHapticFeedback.current
  var showAddDialog by remember { mutableStateOf(false) }
  var habitToEdit by remember { mutableStateOf<HabitEntity?>(null) }
  var showQuickAlarmDialog by remember { mutableStateOf(false) }
  var quickAlarmTargetHabit by remember { mutableStateOf<HabitEntity?>(null) }

  var currentClockTime by remember { mutableStateOf(AlarmHelper.getCurrentHourMinute()) }
  LaunchedEffect(Unit) {
    while (true) {
      currentClockTime = AlarmHelper.getCurrentHourMinute()
      delay(15_000L)
    }
  }

  val notifPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { }

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
  val msgAlarmDisabled = stringResource(R.string.alarm_disabled_toast)
  val msgAlarmDeleted = stringResource(R.string.alarm_deleted_toast)

  fun ensureNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
      !FocusPermissionHelper.hasNotificationPermission(context)
    ) {
      runCatching {
        notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  fun scheduleDualAlarm(
    title: String,
    hour: Int,
    minute: Int,
    repeatMode: AlarmRepeatMode = AlarmRepeatMode.EVERY_DAY,
    linkedHabitId: Long? = null,
    alsoPersistAlarmRow: Boolean = true
  ) {
    ensureNotificationPermission()
    if (alsoPersistAlarmRow) {
      onSaveAlarm(title, hour, minute, repeatMode, linkedHabitId)
    }
    val clockLaunched = AlarmHelper.scheduleSystemAlarm(context, title, hour, minute, repeatMode)
    if (clockLaunched) {
      onShowMessage(context.getString(R.string.alarm_saved_and_clock_toast, hour, minute))
    } else {
      onShowMessage(context.getString(R.string.alarm_saved_inapp_toast, hour, minute))
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundNavy)
      .testTag("today_screen")
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 92.dp)
    ) {
      // 1. Header Subtitle
      item(key = "header") {
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
      }

      // 2. Daily Progress Card with Animated Circular Ring
      item(key = "progress_card") {
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
      }

      // 3. Dedicated & Functional Phone Alarms & Reminders Card
      item(key = "phone_alarms_card") {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("phone_alarms_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
          border = BorderStroke(1.dp, AccentGold.copy(alpha = 0.45f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentGold.copy(alpha = 0.16f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = AccentGold,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = stringResource(R.string.alarms_card_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                  )
                  Text(
                    text = if (scheduledAlarms.isNotEmpty()) {
                      stringResource(R.string.alarms_active_count, scheduledAlarms.count { it.isEnabled })
                    } else {
                      stringResource(R.string.alarms_none_set)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (scheduledAlarms.any { it.isEnabled }) AccentGold else TextMuted
                  )
                }
              }

              // Live current time pill right on the alarm card
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(SurfaceCard)
                  .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = AlarmHelper.formatTime(currentClockTime.first, currentClockTime.second),
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold,
                  color = PrimaryBlueLight
                )
              }
            }

            Text(
              text = stringResource(R.string.alarms_card_subtitle),
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary
            )

            // Persisted Scheduled Alarms List with On/Off Switch & Delete
            if (scheduledAlarms.isNotEmpty()) {
              Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                scheduledAlarms.forEach { alarm ->
                  val repeatLabelRes = when (AlarmRepeatMode.fromStorage(alarm.repeatMode)) {
                    AlarmRepeatMode.ONCE -> R.string.repeat_once
                    AlarmRepeatMode.EVERY_DAY -> R.string.repeat_daily
                    AlarmRepeatMode.WEEKDAYS -> R.string.repeat_weekdays
                  }
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .clip(RoundedCornerShape(12.dp))
                      .background(SurfaceCard)
                      .border(
                        1.dp,
                        if (alarm.isEnabled) AccentGold.copy(alpha = 0.45f) else SurfaceBorder,
                        RoundedCornerShape(12.dp)
                      )
                      .clickable {
                        val linked = habits.firstOrNull { it.habit.id == alarm.linkedHabitId }?.habit
                        quickAlarmTargetHabit = linked ?: HabitEntity(
                          title = alarm.title,
                          description = "",
                          category = "discipline",
                          reminderHour = alarm.hour,
                          reminderMinute = alarm.minute,
                          alarmEnabled = alarm.isEnabled
                        )
                        showQuickAlarmDialog = true
                      }
                      .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Text(
                          text = AlarmHelper.formatTime(alarm.hour, alarm.minute),
                          style = MaterialTheme.typography.titleMedium,
                          fontWeight = FontWeight.Bold,
                          color = if (alarm.isEnabled) AccentGold else TextMuted
                        )
                        Box(
                          modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryBlue.copy(alpha = 0.16f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                          Text(
                            text = stringResource(repeatLabelRes),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryBlueLight
                          )
                        }
                      }
                      Text(
                        text = alarm.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (alarm.isEnabled) TextPrimary else TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                    }

                    Switch(
                      checked = alarm.isEnabled,
                      onCheckedChange = { enabled ->
                        if (enabled) ensureNotificationPermission()
                        onToggleAlarm(alarm, enabled)
                        if (enabled) {
                          onShowMessage(
                            context.getString(R.string.alarm_saved_inapp_toast, alarm.hour, alarm.minute)
                          )
                        } else {
                          onShowMessage(msgAlarmDisabled)
                        }
                      },
                      colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentGold
                      )
                    )

                    IconButton(
                      onClick = {
                        onDeleteAlarm(alarm)
                        onShowMessage(msgAlarmDeleted)
                      },
                      modifier = Modifier.size(36.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = stringResource(R.string.delete),
                        tint = AccentCrimson.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  }
                }
              }
            }

            // Clear Action Buttons: "+ Set Alarm" and "Open Clock"
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = {
                  quickAlarmTargetHabit = null
                  showQuickAlarmDialog = true
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_set_alarm_now"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AlarmAdd,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = stringResource(R.string.btn_set_alarm_now),
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              OutlinedButton(
                onClick = {
                  if (!AlarmHelper.openSystemClockAlarms(context)) {
                    onShowMessage(msgAlarmMissing)
                  }
                },
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_open_clock"),
                border = BorderStroke(1.dp, SurfaceBorder),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Schedule,
                  contentDescription = null,
                  tint = TextSecondary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = stringResource(R.string.btn_open_clock),
                  color = TextPrimary,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      // 4. Habits / Tasks List OR Clean Centered Empty State
      if (habits.isEmpty()) {
        item(key = "empty_habits") {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 28.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(SurfaceSlate),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = PrimaryBlueLight,
                modifier = Modifier.size(34.dp)
              )
            }
            Text(
              text = stringResource(R.string.empty_habits_title),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = TextPrimary,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth()
            )
            Text(
              text = stringResource(R.string.empty_habits_subtitle),
              style = MaterialTheme.typography.bodySmall,
              color = TextMuted,
              modifier = Modifier.padding(horizontal = 16.dp),
              textAlign = TextAlign.Center
            )
          }
        }
      } else {
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
              quickAlarmTargetHabit = item.habit
              showQuickAlarmDialog = true
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

    if (showQuickAlarmDialog) {
      QuickAlarmDialog(
        habits = habits.map { it.habit },
        initialHabit = quickAlarmTargetHabit,
        onScheduleAlarm = { linkedHabit, alarmTitle, hour, minute, repeatMode ->
          val linkedId = linkedHabit?.id?.takeIf { it > 0L }
          if (linkedHabit != null && linkedId != null) {
            onUpdateHabit(
              linkedHabit.copy(
                reminderHour = hour,
                reminderMinute = minute,
                alarmEnabled = true
              )
            )
          }
          showQuickAlarmDialog = false
          quickAlarmTargetHabit = null
          scheduleDualAlarm(
            title = alarmTitle,
            hour = hour,
            minute = minute,
            repeatMode = repeatMode,
            linkedHabitId = linkedId,
            alsoPersistAlarmRow = linkedId == null
          )
        },
        onDismiss = {
          showQuickAlarmDialog = false
          quickAlarmTargetHabit = null
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
            scheduleDualAlarm(
              title = title,
              hour = hour,
              minute = minute,
              repeatMode = AlarmRepeatMode.EVERY_DAY,
              alsoPersistAlarmRow = false
            )
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
            scheduleDualAlarm(
              title = title,
              hour = hour,
              minute = minute,
              repeatMode = AlarmRepeatMode.EVERY_DAY,
              linkedHabitId = habit.id,
              alsoPersistAlarmRow = false
            )
          } else {
            onShowMessage(msgSaved)
          }
        }
      )
    }
  }
}
