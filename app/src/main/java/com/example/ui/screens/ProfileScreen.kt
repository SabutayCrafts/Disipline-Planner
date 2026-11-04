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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserProfileEntity
import com.example.model.DisciplineRank
import com.example.model.DisciplineRankCatalog
import com.example.ui.components.AvatarCanvas
import com.example.ui.components.RankCard
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

@Composable
fun ProfileScreen(
  userProfile: UserProfileEntity,
  activeStreak: Int,
  currentRank: DisciplineRank,
  nextRank: DisciplineRank?,
  onSaveProfile: (UserProfileEntity) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCustomizerDialog by remember { mutableStateOf(false) }
  var rankFilterIndex by remember { mutableIntStateOf(0) } // 0: Bütün, 1: Açılmış, 2: Qıfıllı

  val filteredRanks = remember(rankFilterIndex, activeStreak) {
    when (rankFilterIndex) {
      1 -> DisciplineRankCatalog.ALL_RANKS.filter { activeStreak >= it.requiredDays }
      2 -> DisciplineRankCatalog.ALL_RANKS.filter { activeStreak < it.requiredDays }
      else -> DisciplineRankCatalog.ALL_RANKS
    }
  }

  val nextRankProgress = remember(activeStreak, currentRank, nextRank) {
    if (nextRank != null) {
      val span = (nextRank.requiredDays - currentRank.requiredDays).coerceAtLeast(1)
      val currentInSpan = (activeStreak - currentRank.requiredDays).coerceAtLeast(0)
      (currentInSpan.toFloat() / span.toFloat()).coerceIn(0f, 1f)
    } else 1f
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BackgroundNavy)
      .testTag("profile_screen")
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. RPG Character Profile Card with Animated 3D Vector Avatar
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceSlate),
          border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.4f))
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Status Tag Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PrimaryBlue.copy(alpha = 0.18f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = userProfile.statusTag,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3D Animated Vector Avatar
            AvatarCanvas(
              profile = userProfile,
              sizeDp = 150.dp,
              enableAnimation = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Name
            Text(
              text = userProfile.name,
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )

            // Purpose / Oath
            Text(
              text = "“${userProfile.purpose}”",
              style = MaterialTheme.typography.bodySmall,
              color = TextSecondary,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Customize Avatar Button
            Button(
              onClick = { showCustomizerDialog = true },
              colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("edit_avatar_button")
            ) {
              Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Avatarı və Kimliyi Fərdiləşdir", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // 2. Active Rank & Next Milestone
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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(currentRank.badgeIcon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Səviyyə ${currentRank.level} — ${currentRank.titleAz}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold
                  )
                  Text(
                    text = "Aktiv Ardıcıllıq: $activeStreak Gün",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                  )
                }
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(AccentEmerald.copy(alpha = 0.2f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "Aktiv Rütbə",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = AccentEmerald
                )
              }
            }

            if (nextRank != null) {
              Spacer(modifier = Modifier.height(14.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Növbəti: Səviyyə ${nextRank.level} (${nextRank.titleAz})",
                  style = MaterialTheme.typography.labelSmall,
                  color = TextMuted
                )
                Text(
                  text = "${(nextRank.requiredDays - activeStreak).coerceAtLeast(0)} gün qaldı",
                  style = MaterialTheme.typography.labelSmall,
                  color = PrimaryBlue,
                  fontWeight = FontWeight.Bold
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              LinearProgressIndicator(
                progress = { nextRankProgress },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = PrimaryBlue,
                trackColor = SurfaceCard
              )
            }
          }
        }
      }

      // 3. 25-Tier Discipline Rank Catalog Header & Filter Pills
      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "25 Səviyyəli İntizam Kataloqu (1 Gündən 3+ İlədək)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val filterOptions = listOf(
              "Bütün (25)",
              "Açılmış (${DisciplineRankCatalog.ALL_RANKS.count { activeStreak >= it.requiredDays }})",
              "Qıfıllı (${DisciplineRankCatalog.ALL_RANKS.count { activeStreak < it.requiredDays }})"
            )
            filterOptions.forEachIndexed { index, title ->
              val isSelected = rankFilterIndex == index
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) PrimaryBlue else SurfaceCard)
                  .clickable { rankFilterIndex = index }
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = title,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  color = if (isSelected) Color.White else TextSecondary
                )
              }
            }
          }
        }
      }

      // Catalog List items
      items(filteredRanks, key = { it.level }) { rank ->
        RankCard(
          rank = rank,
          userStreak = activeStreak
        )
      }
    }

    // Avatar Customizer Dialog
    if (showCustomizerDialog) {
      AvatarCustomizerDialog(
        currentProfile = userProfile,
        currentStreak = activeStreak,
        onDismiss = { showCustomizerDialog = false },
        onSave = { updatedProfile ->
          onSaveProfile(updatedProfile)
          showCustomizerDialog = false
        }
      )
    }
  }
}
