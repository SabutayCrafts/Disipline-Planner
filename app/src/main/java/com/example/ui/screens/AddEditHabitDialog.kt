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

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.HabitEntity
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitDialog(
  initialHabit: HabitEntity? = null,
  onDismiss: () -> Unit,
  onSave: (title: String, description: String, category: String, targetDays: Int) -> Unit
) {
  var title by remember { mutableStateOf(initialHabit?.title ?: "") }
  var description by remember { mutableStateOf(initialHabit?.description ?: "") }
  var selectedCategory by remember { mutableStateOf(initialHabit?.category ?: "Sağlamlıq") }
  var targetDays by remember { mutableIntStateOf(initialHabit?.targetDaysPerWeek ?: 7) }
  var titleError by remember { mutableStateOf(false) }

  val categories = listOf("Sağlamlıq", "Zehin & Fokus", "Məhsuldarlıq", "İntizam", "İdman", "Öyrənmə")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceSlate,
    title = {
      Text(
        text = if (initialHabit == null) "Yeni İntizam Vərdişi" else "Vərdişə Düzəliş Et",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = {
            title = it
            if (it.isNotBlank()) titleError = false
          },
          label = { Text("Vərdişin Adı (məs: Səhər İdmanı)") },
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
          Text("Vərdiş adı boş ola bilməz", color = Color(0xFFEF4444), fontSize = 12.sp)
        }

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Məqsəd və ya Qeyd (İxtiyari)") },
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
          text = "Kateqoriya",
          style = MaterialTheme.typography.labelMedium,
          color = TextSecondary,
          fontWeight = FontWeight.SemiBold
        )

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = cat == selectedCategory
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) PrimaryBlue else SurfaceCard)
                .clickable { selectedCategory = cat }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = cat,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else TextSecondary
              )
            }
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
            onSave(title.trim(), description.trim(), selectedCategory, targetDays)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier.testTag("save_habit_button")
      ) {
        Text("Yadda Saxla", color = Color.White, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Ləğv Et", color = TextMuted)
      }
    }
  )
}
