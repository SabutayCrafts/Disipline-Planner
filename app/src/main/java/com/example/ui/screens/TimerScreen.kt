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

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.AppSettings
import com.example.focus.FocusPermissionsStatus
import com.example.focus.FocusTimerState
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

@Composable
fun TimerScreen(
  focusState: FocusTimerState,
  appSettings: AppSettings,
  permissionsStatus: FocusPermissionsStatus,
  onSelectDuration: (Int) -> Unit,
  onStartOrToggle: () -> Unit,
  onStopFocus: () -> Unit,
  onToggleDnd: (Boolean) -> Unit,
  onOpenAppPicker: () -> Unit,
  onOpenPermissions: () -> Unit,
  onAcknowledgeCompletion: () -> Unit,
  onDismissInterceptedApp: () -> Unit,
  modifier: Modifier = Modifier
) {
  val haptics = LocalHapticFeedback.current
  var showCustomDurationDialog by remember { mutableStateOf(false) }

  val presets = listOf(15, 25, 45, 60)
  val quotes = stringArrayResource(R.array.stoic_quotes)
  var currentQuoteIndex by remember { mutableIntStateOf(0) }

  val rawProgress = if (focusState.totalSeconds > 0) {
    focusState.remainingSeconds.toFloat() / focusState.totalSeconds.toFloat()
  } else 0f
  val animatedProgress by animateFloatAsState(
    targetValue = rawProgress,
    animationSpec = spring(stiffness = 300f),
    label = "focus_arc"
  )

  val infiniteTransition = rememberInfiniteTransition(label = "focus_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (focusState.isRunning && !focusState.isPaused) 1.035f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val minutes = focusState.remainingSeconds / 60
  val seconds = focusState.remainingSeconds % 60
  val timeDisplay = String.format("%02d:%02d", minutes, seconds)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundNavy)
      .testTag("timer_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Title
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = stringResource(R.string.focus_title),
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = stringResource(R.string.focus_subtitle),
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          textAlign = TextAlign.Center
        )
      }

      // 2. Preset Chips + Custom Duration
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        presets.forEach { min ->
          val isSelected = focusState.selectedMinutes == min
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) PrimaryBlue else SurfaceCard)
              .border(
                BorderStroke(1.dp, if (isSelected) PrimaryBlueLight else SurfaceBorder),
                RoundedCornerShape(10.dp)
              )
              .clickable(enabled = !focusState.isRunning) {
                onSelectDuration(min)
              }
              .padding(horizontal = 12.dp, vertical = 8.dp)
          ) {
            Text(
              text = stringResource(R.string.minutes_short, min),
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else TextSecondary
            )
          }
        }

        val isCustom = !presets.contains(focusState.selectedMinutes)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isCustom) PrimaryBlue else SurfaceCard)
            .border(
              BorderStroke(1.dp, if (isCustom) PrimaryBlueLight else SurfaceBorder),
              RoundedCornerShape(10.dp)
            )
            .clickable(enabled = !focusState.isRunning) {
              showCustomDurationDialog = true
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(
            text = if (isCustom) stringResource(R.string.minutes_short, focusState.selectedMinutes)
            else stringResource(R.string.custom_time),
            fontSize = 12.sp,
            fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Medium,
            color = if (isCustom) Color.White else TextSecondary
          )
        }
      }

      // 3. Circular Timer Display with Pulsing Focus Shield Ring
      Box(
        modifier = Modifier
          .size(220.dp)
          .scale(pulseScale),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(220.dp)) {
          val strokeWidth = 12.dp.toPx()
          drawCircle(
            color = SurfaceSlate,
            style = Stroke(strokeWidth)
          )
          if (focusState.isRunning && !focusState.isPaused) {
            drawCircle(
              color = PrimaryBlue.copy(alpha = 0.14f),
              radius = size.minDimension / 2f - strokeWidth
            )
          }
          drawArc(
            color = when {
              focusState.isRunning && !focusState.isPaused -> AccentEmerald
              focusState.isPaused -> AccentGold
              else -> PrimaryBlueLight
            },
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = timeDisplay,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            if (focusState.isRunning && !focusState.isPaused) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = AccentEmerald,
                modifier = Modifier.size(14.dp)
              )
            }
            Text(
              text = stringResource(
                when {
                  focusState.isRunning && !focusState.isPaused -> R.string.focus_status_active
                  focusState.isPaused -> R.string.focus_status_paused
                  else -> R.string.focus_status_ready
                }
              ),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = when {
                focusState.isRunning && !focusState.isPaused -> AccentEmerald
                focusState.isPaused -> AccentGold
                else -> TextMuted
              }
            )
          }
        }
      }

      // 4. Play / Pause / Stop Controls
      Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (focusState.isRunning) {
          IconButton(
            onClick = {
              if (appSettings.hapticsEnabled) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
              }
              onStopFocus()
            },
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(AccentCrimson.copy(alpha = 0.2f))
              .border(1.dp, AccentCrimson, CircleShape)
              .testTag("timer_stop_button")
          ) {
            Icon(
              imageVector = Icons.Default.Stop,
              contentDescription = stringResource(R.string.cd_reset_focus),
              tint = AccentCrimson,
              modifier = Modifier.size(24.dp)
            )
          }
        }

        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
              if (focusState.isRunning && !focusState.isPaused) AccentGold else PrimaryBlue
            )
            .clickable {
              if (appSettings.hapticsEnabled) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
              }
              onStartOrToggle()
            }
            .testTag("timer_toggle_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (focusState.isRunning && !focusState.isPaused) Icons.Default.Pause
            else Icons.Default.PlayArrow,
            contentDescription = stringResource(
              if (focusState.isRunning && !focusState.isPaused) R.string.cd_pause_focus
              else R.string.cd_start_focus
            ),
            tint = Color.White,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      // 5. Real Distraction Blocker Control Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        border = BorderStroke(1.dp, SurfaceBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = PrimaryBlueLight,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = stringResource(R.string.blocker_panel_title),
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = if (appSettings.blockedPackages.isEmpty()) {
                    stringResource(R.string.blocker_no_apps_selected)
                  } else {
                    stringResource(R.string.blocker_apps_selected, appSettings.blockedPackages.size)
                  },
                  style = MaterialTheme.typography.labelSmall,
                  color = if (appSettings.blockedPackages.isEmpty()) TextMuted else PrimaryBlueLight
                )
              }
            }

            Icon(
              imageVector = if (permissionsStatus.allBlockerPermissionsGranted) Icons.Default.CheckCircle
              else Icons.Default.Warning,
              contentDescription = null,
              tint = if (permissionsStatus.allBlockerPermissionsGranted) AccentEmerald else AccentGold,
              modifier = Modifier.size(20.dp)
            )
          }

          // DND Toggle Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(SurfaceCard)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = stringResource(R.string.dnd_toggle_label),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
              )
              Text(
                text = stringResource(R.string.dnd_toggle_desc),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
              )
            }
            Switch(
              checked = appSettings.dndEnabled,
              onCheckedChange = onToggleDnd,
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryBlue
              )
            )
          }

          // Missing Permissions Banner (tappable for instant grant)
          val missingAnyPerm = permissionsStatus.hasMissingPermissionsForFocus(
            dndEnabled = appSettings.dndEnabled,
            hasBlockedApps = appSettings.blockedPackages.isNotEmpty()
          )
          if (missingAnyPerm) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AccentGold.copy(alpha = 0.14f))
                .border(1.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .clickable(onClick = onOpenPermissions)
                .padding(horizontal = 10.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AccentGold,
                modifier = Modifier.size(16.dp)
              )
              Text(
                text = stringResource(R.string.perms_missing_banner),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = AccentGold,
                modifier = Modifier.weight(1f)
              )
            }
          }

          // Action Buttons: Choose Apps & Permissions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onOpenAppPicker,
              modifier = Modifier.weight(1f),
              border = BorderStroke(1.dp, PrimaryBlueLight.copy(alpha = 0.6f)),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Apps, contentDescription = null, tint = PrimaryBlueLight, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(stringResource(R.string.btn_choose_apps), color = PrimaryBlueLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = onOpenPermissions,
              modifier = Modifier.weight(1f),
              border = BorderStroke(
                1.dp,
                if (permissionsStatus.allBlockerPermissionsGranted) AccentEmerald.copy(alpha = 0.5f)
                else AccentGold.copy(alpha = 0.7f)
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = if (permissionsStatus.allBlockerPermissionsGranted) AccentEmerald else AccentGold,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                stringResource(R.string.btn_permissions),
                color = if (permissionsStatus.allBlockerPermissionsGranted) AccentEmerald else AccentGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      // 6. Stoic Quote Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
          },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SurfaceBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "“${quotes[currentQuoteIndex % quotes.size]}”",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "${stringResource(R.string.tap_to_change_quote)} 🔄",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
          )
        }
      }
    }

    if (showCustomDurationDialog) {
      CustomFocusDurationDialog(
        initialMinutes = focusState.selectedMinutes,
        onConfirm = { mins ->
          onSelectDuration(mins)
          showCustomDurationDialog = false
        },
        onDismiss = { showCustomDurationDialog = false }
      )
    }

    // Intercepted Blocked App Shield Alert (fallback when returning to MainActivity)
    focusState.interceptedAppName?.let { blockedApp ->
      AlertDialog(
        onDismissRequest = onDismissInterceptedApp,
        containerColor = SurfaceSlate,
        title = {
          Text(
            text = "🛡️ ${stringResource(R.string.shield_title)}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AccentCrimson
          )
        },
        text = {
          Text(
            text = stringResource(R.string.shield_message, blockedApp),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
          )
        },
        confirmButton = {
          Button(
            onClick = onDismissInterceptedApp,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
          ) {
            Text(stringResource(R.string.shield_return_btn), color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      )
    }

    // Session Completed Dialog
    focusState.completedSessionMinutes?.let { completedMins ->
      AlertDialog(
        onDismissRequest = onAcknowledgeCompletion,
        containerColor = SurfaceSlate,
        title = {
          Text(
            text = "🏆 ${stringResource(R.string.focus_complete_title)}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AccentEmerald
          )
        },
        text = {
          Text(
            text = stringResource(R.string.focus_complete_body, completedMins),
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
          )
        },
        confirmButton = {
          Button(
            onClick = onAcknowledgeCompletion,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
          ) {
            Text(stringResource(R.string.btn_continue), color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      )
    }
  }
}
