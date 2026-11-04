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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DisciplineRank
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
fun RankCard(
  rank: DisciplineRank,
  userStreak: Int,
  modifier: Modifier = Modifier
) {
  val isUnlocked = userStreak >= rank.requiredDays
  val progress = (userStreak.toFloat() / rank.requiredDays.toFloat()).coerceIn(0f, 1f)

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isUnlocked) SurfaceSlate else SurfaceCard.copy(alpha = 0.7f)
    ),
    border = BorderStroke(
      width = 1.dp,
      color = if (isUnlocked) AccentGold.copy(alpha = 0.6f) else SurfaceBorder
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Icon badge
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              if (isUnlocked) AccentGold.copy(alpha = 0.18f) else SurfaceBorder.copy(alpha = 0.3f)
            ),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = if (isUnlocked) rank.badgeIcon else "🔒",
            fontSize = 22.sp
          )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Səviyyə ${rank.level}",
              style = MaterialTheme.typography.labelSmall,
              color = if (isUnlocked) AccentGold else TextMuted,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "• ${rank.requiredDays} Gün",
              style = MaterialTheme.typography.labelSmall,
              color = TextSecondary
            )
          }

          Text(
            text = rank.titleAz,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) TextPrimary else TextSecondary
          )
        }

        // Status badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
              if (isUnlocked) AccentEmerald.copy(alpha = 0.2f) else SurfaceBorder.copy(alpha = 0.5f)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = if (isUnlocked) "Açıldı" else "${(rank.requiredDays - userStreak).coerceAtLeast(0)} gün qaldı",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isUnlocked) AccentEmerald else TextMuted
          )
        }
      }

      // Progress bar if locked
      if (!isUnlocked) {
        Spacer(modifier = Modifier.height(12.dp))
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = PrimaryBlue,
          trackColor = SurfaceBorder
        )
      }

      // Reward callout if this rank unlocks equipment
      if (rank.unlockedRewardName != null) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryBlue.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = "🎁 Təchizat Mükafatı: ${rank.unlockedRewardName}",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryBlue,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
