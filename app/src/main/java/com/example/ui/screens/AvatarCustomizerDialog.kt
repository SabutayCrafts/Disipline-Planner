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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.model.AvatarPresets
import com.example.ui.components.AvatarCanvas
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceBorder
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarCustomizerDialog(
  currentProfile: UserProfileEntity,
  currentStreak: Int,
  onDismiss: () -> Unit,
  onSave: (UserProfileEntity) -> Unit
) {
  var name by remember { mutableStateOf(currentProfile.name) }
  var purpose by remember { mutableStateOf(currentProfile.purpose) }
  var statusTag by remember { mutableStateOf(currentProfile.statusTag) }
  var hairStyle by remember { mutableStateOf(currentProfile.hairStyle) }
  var facialHair by remember { mutableStateOf(currentProfile.facialHair) }
  var eyewear by remember { mutableStateOf(currentProfile.eyewear) }
  var outfit by remember { mutableStateOf(currentProfile.outfit) }
  var skinToneHex by remember { mutableStateOf(currentProfile.skinToneHex) }
  var auraTheme by remember { mutableStateOf(currentProfile.auraTheme) }
  var equippedHeadwear by remember { mutableStateOf(currentProfile.equippedHeadwear) }

  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("Profil", "Təchizat & Başlıq", "Saç & Üz", "Geyim", "Dəri & Aura")

  val previewProfile = remember(name, purpose, statusTag, hairStyle, facialHair, eyewear, outfit, skinToneHex, auraTheme, equippedHeadwear) {
    UserProfileEntity(
      id = 1,
      name = name,
      purpose = purpose,
      statusTag = statusTag,
      hairStyle = hairStyle,
      facialHair = facialHair,
      eyewear = eyewear,
      outfit = outfit,
      skinToneHex = skinToneHex,
      auraTheme = auraTheme,
      equippedHeadwear = equippedHeadwear
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceSlate,
    title = {
      Text(
        text = "Avatar & Kimlik Studiyası",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 500.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // 1. Live 3D Floating Avatar Preview
        AvatarCanvas(
          profile = previewProfile,
          sizeDp = 130.dp,
          enableAnimation = true
        )

        // Status Tag Selector
        FlowRow(
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxWidth()
        ) {
          AvatarPresets.STATUS_TAGS.take(3).forEach { tag ->
            val isSelected = tag == statusTag
            Box(
              modifier = Modifier
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) PrimaryBlue else SurfaceCard)
                .clickable { statusTag = tag }
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(tag, fontSize = 11.sp, color = if (isSelected) Color.White else TextSecondary)
            }
          }
        }

        // Category Tabs
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          containerColor = SurfaceCard,
          contentColor = PrimaryBlue,
          edgePadding = 8.dp,
          indicator = { tabPositions ->
            SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = PrimaryBlue
            )
          }
        ) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = {
                Text(
                  title,
                  fontSize = 12.sp,
                  fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                  color = if (selectedTab == index) Color.White else TextMuted
                )
              }
            )
          }
        }

        // Tab Content with Visual Grid Cards (NO TEXT DROPDOWNS)
        when (selectedTab) {
          0 -> {
            // Profile Info (Name & Purpose)
            Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Döyüşçü Adınız") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = PrimaryBlue,
                  unfocusedBorderColor = SurfaceBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary
                )
              )
              OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it },
                label = { Text("Şəxsi Məqsədiniz / İntizam Andınız") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = PrimaryBlue,
                  unfocusedBorderColor = SurfaceBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary
                )
              )
            }
          }

          1 -> {
            // Streak-Unlockable Headwear & Helmets
            Text(
              text = "Zireh Dəbilqələri və Taclar (Ardıcıl Günlərlə Açılır)",
              style = MaterialTheme.typography.labelMedium,
              color = TextSecondary,
              textAlign = TextAlign.Center
            )
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AvatarPresets.HEADWEAR.forEach { item ->
                val isUnlocked = currentStreak >= item.requiredDays
                val isSelected = equippedHeadwear == item.id

                Box(
                  modifier = Modifier
                    .size(80.dp, 75.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) PrimaryBlue.copy(alpha = 0.25f) else SurfaceCard)
                    .border(
                      BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) PrimaryBlue else SurfaceBorder
                      ),
                      shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = isUnlocked) {
                      equippedHeadwear = item.id
                    }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                    Text(
                      text = if (isUnlocked) item.iconEmoji else "🔒",
                      fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = item.name,
                      fontSize = 9.sp,
                      color = if (isUnlocked) TextPrimary else TextMuted,
                      maxLines = 1,
                      textAlign = TextAlign.Center
                    )
                    if (!isUnlocked) {
                      Text(
                        text = "${item.requiredDays} Gün",
                        fontSize = 8.sp,
                        color = AccentGold,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          }

          2 -> {
            // Hair & Facial Hair Visual Cards
            Text("Saç Düzümü", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AvatarPresets.HAIR_STYLES.forEach { item ->
                val isSelected = hairStyle == item.id
                Box(
                  modifier = Modifier
                    .size(72.dp, 60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryBlue else SurfaceCard)
                    .clickable { hairStyle = item.id }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(item.iconEmoji, fontSize = 20.sp)
                    Text(item.name, fontSize = 9.sp, color = if (isSelected) Color.White else TextSecondary, maxLines = 1)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Saqqal və Bığ", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AvatarPresets.FACIAL_HAIR.forEach { item ->
                val isSelected = facialHair == item.id
                Box(
                  modifier = Modifier
                    .size(72.dp, 60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryBlue else SurfaceCard)
                    .clickable { facialHair = item.id }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(item.iconEmoji, fontSize = 20.sp)
                    Text(item.name, fontSize = 9.sp, color = if (isSelected) Color.White else TextSecondary, maxLines = 1)
                  }
                }
              }
            }
          }

          3 -> {
            // Outfits & Eyewear
            Text("Geyim və Zireh", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AvatarPresets.OUTFITS.forEach { item ->
                val isSelected = outfit == item.id
                Box(
                  modifier = Modifier
                    .size(72.dp, 60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryBlue else SurfaceCard)
                    .clickable { outfit = item.id }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(item.iconEmoji, fontSize = 20.sp)
                    Text(item.name, fontSize = 9.sp, color = if (isSelected) Color.White else TextSecondary, maxLines = 1)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Eynək və Vizorlar", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AvatarPresets.EYEWEAR.forEach { item ->
                val isSelected = eyewear == item.id
                Box(
                  modifier = Modifier
                    .size(72.dp, 60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) PrimaryBlue else SurfaceCard)
                    .clickable { eyewear = item.id }
                    .padding(4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(item.iconEmoji, fontSize = 20.sp)
                    Text(item.name, fontSize = 9.sp, color = if (isSelected) Color.White else TextSecondary, maxLines = 1)
                  }
                }
              }
            }
          }

          4 -> {
            // Skin Tones & Aura Presets
            Text("Dəri Rəngi", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              AvatarPresets.SKIN_TONES.forEach { item ->
                val isSelected = skinToneHex == item.hex
                val color = Color(android.graphics.Color.parseColor(item.hex))
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                      BorderStroke(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) PrimaryBlue else SurfaceBorder
                      ),
                      CircleShape
                    )
                    .clickable { skinToneHex = item.hex }
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Arxa Fon Aura Mövzusu", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              AvatarPresets.AURAS.forEach { item ->
                val isSelected = auraTheme == item.id
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) PrimaryBlue else SurfaceCard)
                    .clickable { auraTheme = item.id }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(
                    item.name,
                    fontSize = 11.sp,
                    color = if (isSelected) Color.White else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSave(previewProfile)
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier.testTag("save_avatar_customizer_button")
      ) {
        Text("Təsdiq Et", color = Color.White, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Bağla", color = TextMuted)
      }
    }
  )
}
