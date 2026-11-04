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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.FocusSessionEntity
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
  activeStreak: Int,
  totalCompletions: Int,
  totalFocusMinutes: Int,
  completedDates: List<String>,
  focusSessions: List<FocusSessionEntity>,
  modifier: Modifier = Modifier
) {
  val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
  val dayDisplayFormat = remember { SimpleDateFormat("EEE", Locale("az")) }

  // Compute last 7 days statuses
  val last7Days = remember(completedDates) {
    val list = mutableListOf<Pair<String, Boolean>>()
    val cal = Calendar.getInstance()
    for (i in 0..6) {
      val dateStr = dateFormat.format(cal.time)
      val dayLabel = dayDisplayFormat.format(cal.time).uppercase()
      val isDone = completedDates.contains(dateStr)
      list.add(Pair(dayLabel, isDone))
      cal.add(Calendar.DAY_OF_YEAR, -1)
    }
    list.reversed()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundNavy)
      .testTag("history_screen")
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Text(
          text = "İntizam Statistikası",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = "Gündəlik səy və daimi ardıcıllığınızın analitikası",
          style = MaterialTheme.typography.bodyMedium,
          color = TextSecondary
        )
      }

      // 3 KPI Metric Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricCard(
            title = "Aktiv Ardıcıllıq",
            value = "$activeStreak Gün",
            icon = Icons.Default.LocalFireDepartment,
            accentColor = AccentGold,
            modifier = Modifier.weight(1f)
          )
          MetricCard(
            title = "Ümumi İcra",
            value = "$totalCompletions Dəfə",
            icon = Icons.Default.CheckCircle,
            accentColor = AccentEmerald,
            modifier = Modifier.weight(1f)
          )
          MetricCard(
            title = "Dərin Fokus",
            value = "$totalFocusMinutes Dəq",
            icon = Icons.Default.HourglassTop,
            accentColor = PrimaryBlueLight,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Last 7 Days Activity Heat Tracker
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
          border = BorderStroke(1.dp, SurfaceBorder)
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
                text = "Son 7 Günlük Ardıcıllıq",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "Daimi Vərdiş",
                style = MaterialTheme.typography.labelSmall,
                color = AccentEmerald,
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              last7Days.forEach { (day, isDone) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Box(
                    modifier = Modifier
                      .size(38.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(if (isDone) AccentEmerald else SurfaceCard)
                      .border(
                        BorderStroke(
                          1.dp,
                          if (isDone) AccentEmerald else SurfaceBorder
                        ),
                        RoundedCornerShape(8.dp)
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isDone) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                      )
                    }
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = day,
                    fontSize = 11.sp,
                    color = if (isDone) TextPrimary else TextMuted,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // Focus Sessions History
      item {
        Text(
          text = "Son Fokus Seansları",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
      }

      if (focusSessions.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Hələ heç bir fokus seansı qeydə alınmayıb. Fokus Taymerindən istifadə edərək ilk seansı tamamlayın.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      } else {
        items(focusSessions.take(10), key = { it.id }) { session ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, SurfaceBorder)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                  Text(
                    text = "${session.durationMinutes} Dəqiqəlik Fokus",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                  )
                  Text(
                    text = session.dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(AccentEmerald.copy(alpha = 0.15f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "Tamamlandı",
                  color = AccentEmerald,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MetricCard(
  title: String,
  value: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
    border = BorderStroke(1.dp, SurfaceBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(accentColor.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(16.dp)
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        maxLines = 1
      )
    }
  }
}
