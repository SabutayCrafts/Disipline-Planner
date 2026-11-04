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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.delay

@Composable
fun TimerScreen(
  onSessionComplete: (durationMinutes: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedMinutes by remember { mutableIntStateOf(25) }
  var totalSeconds by remember { mutableLongStateOf(25L * 60L) }
  var remainingSeconds by remember { mutableLongStateOf(25L * 60L) }
  var isRunning by remember { mutableStateOf(false) }
  var showCelebrationDialog by remember { mutableStateOf(false) }

  val presets = listOf(15, 25, 45, 60)

  val quotes = remember {
    listOf(
      "İntizam — indi istədiyinlə ən çox istədiyin arasında seçim etməkdir.",
      "Zəiflik bəhanə axtarır, iradə yol tapır.",
      "Qələbənin ilk və ən böyük addımı özünə qalib gəlməkdir.",
      "Fokusunu qoru, dünya səs-küylə doludur.",
      "Hər tamamlanan dəqiqə səni daha güclü döyüşçü edir."
    )
  }
  var currentQuoteIndex by remember { mutableIntStateOf(0) }

  // Timer Tick Coroutine
  LaunchedEffect(isRunning, remainingSeconds) {
    if (isRunning && remainingSeconds > 0) {
      delay(1000L)
      remainingSeconds--
    } else if (isRunning && remainingSeconds == 0L) {
      isRunning = false
      onSessionComplete(selectedMinutes)
      showCelebrationDialog = true
    }
  }

  val progress = if (totalSeconds > 0) (remainingSeconds.toFloat() / totalSeconds.toFloat()) else 0f
  val minutes = remainingSeconds / 60
  val seconds = remainingSeconds % 60
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
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Header
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "Dərin Fokus Taymeri",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Fikrini cəmlə və diqqət yayındıran hər şeyi kənara qoy",
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary,
          textAlign = TextAlign.Center
        )
      }

      // Presets Selector (when timer is not running)
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        presets.forEach { min ->
          val isSelected = selectedMinutes == min
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSelected) PrimaryBlue else SurfaceCard)
              .clickable(enabled = !isRunning) {
                selectedMinutes = min
                totalSeconds = min * 60L
                remainingSeconds = min * 60L
              }
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text(
              text = "$min dəq",
              fontSize = 13.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) Color.White else TextSecondary
            )
          }
        }
      }

      // Circular Timer Canvas
      Box(
        modifier = Modifier.size(240.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.size(240.dp)) {
          val strokeWidth = 12.dp.toPx()
          // Background Track
          drawCircle(
            color = SurfaceSlate,
            style = Stroke(strokeWidth)
          )
          // Progress Arc
          drawArc(
            color = if (isRunning) PrimaryBlueLight else PrimaryBlue,
            startAngle = -90f,
            sweepAngle = 360f * progress,
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
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (isRunning) "⚡ Fokusda" else "Hazır",
            style = MaterialTheme.typography.labelMedium,
            color = if (isRunning) PrimaryBlueLight else TextMuted
          )
        }
      }

      // Controls (Play / Pause, Reset)
      Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = {
            isRunning = false
            remainingSeconds = totalSeconds
          },
          modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(SurfaceCard)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Sıfırla",
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
          )
        }

        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(PrimaryBlue)
            .clickable {
              isRunning = !isRunning
            }
            .testTag("timer_toggle_button"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (isRunning) "Dondur" else "Başla",
            tint = Color.White,
            modifier = Modifier.size(36.dp)
          )
        }
      }

      // Stoic Quote Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clickable {
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
          },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "“${quotes[currentQuoteIndex]}”",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = TextSecondary,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Dəyişmək üçün toxun 🔄",
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 10.sp
          )
        }
      }
    }

    // Celebration Dialog when focus session finishes
    if (showCelebrationDialog) {
      AlertDialog(
        onDismissRequest = { showCelebrationDialog = false },
        containerColor = SurfaceSlate,
        title = {
          Text(
            text = "🏆 Əla İş! Fokus Seansı Tamamlandı",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AccentEmerald
          )
        },
        text = {
          Text(
            text = "$selectedMinutes dəqiqəlik dərin fokus seansını uğurla başa vurdunuz və intizam profilinizə qeyd olundu!",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
          )
        },
        confirmButton = {
          Button(
            onClick = {
              showCelebrationDialog = false
              remainingSeconds = totalSeconds
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
          ) {
            Text("Davam Et", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      )
    }
  }
}
