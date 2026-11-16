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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoNotDisturbOn
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.R
import com.example.focus.FocusPermissionHelper
import com.example.focus.FocusPermissionsStatus
import com.example.focus.InstalledAppInfo
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentGold
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AppBlockerPickerDialog(
  installedApps: List<InstalledAppInfo>,
  isLoading: Boolean,
  blockedPackages: Set<String>,
  onLoadApps: () -> Unit,
  onToggleApp: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    onLoadApps()
  }

  val filteredApps = remember(installedApps, searchQuery) {
    if (searchQuery.isBlank()) installedApps
    else installedApps.filter {
      it.appName.contains(searchQuery, ignoreCase = true) ||
        it.packageName.contains(searchQuery, ignoreCase = true)
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceSlate,
    title = {
      Column {
        Text(
          text = stringResource(R.string.app_picker_title),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = TextPrimary
        )
        Text(
          text = stringResource(R.string.blocker_apps_selected, blockedPackages.size),
          style = MaterialTheme.typography.bodySmall,
          color = PrimaryBlueLight
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 260.dp, max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text(stringResource(R.string.app_picker_search), color = TextMuted) },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          )
        )

        if (isLoading) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(color = PrimaryBlue)
              Spacer(modifier = Modifier.height(10.dp))
              Text(stringResource(R.string.app_picker_loading), color = TextSecondary, fontSize = 12.sp)
            }
          }
        } else if (filteredApps.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Text(stringResource(R.string.app_picker_empty), color = TextMuted)
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            items(filteredApps, key = { it.packageName }) { app ->
              val isBlocked = blockedPackages.contains(app.packageName)
              val iconBitmap = remember(app.packageName) {
                runCatching { app.icon?.toBitmap(72, 72)?.asImageBitmap() }.getOrNull()
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isBlocked) PrimaryBlue.copy(alpha = 0.16f) else SurfaceCard)
                  .border(
                    BorderStroke(1.dp, if (isBlocked) PrimaryBlue else SurfaceBorder),
                    RoundedCornerShape(12.dp)
                  )
                  .clickable { onToggleApp(app.packageName) }
                  .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                if (iconBitmap != null) {
                  Image(
                    bitmap = iconBitmap,
                    contentDescription = app.appName,
                    modifier = Modifier
                      .size(34.dp)
                      .clip(RoundedCornerShape(8.dp))
                  )
                } else {
                  Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = app.appName,
                    tint = TextSecondary,
                    modifier = Modifier.size(34.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = app.appName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }

                Checkbox(
                  checked = isBlocked,
                  onCheckedChange = { onToggleApp(app.packageName) },
                  colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    uncheckedColor = TextMuted
                  )
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
      ) {
        Text(stringResource(R.string.done), color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  )
}

@Composable
fun FocusPermissionsDialog(
  status: FocusPermissionsStatus,
  onRefresh: () -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val notifLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) {
    onRefresh()
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceSlate,
    title = {
      Text(
        text = stringResource(R.string.perms_dialog_title),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = TextPrimary
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = stringResource(R.string.perms_dialog_subtitle),
          style = MaterialTheme.typography.bodySmall,
          color = TextSecondary
        )

        PermissionRowItem(
          icon = Icons.Default.DoNotDisturbOn,
          title = stringResource(R.string.perm_dnd_title),
          description = stringResource(R.string.perm_dnd_desc),
          isGranted = status.hasDnd,
          onGrant = { FocusPermissionHelper.openDndSettings(context) }
        )

        PermissionRowItem(
          icon = Icons.Default.QueryStats,
          title = stringResource(R.string.perm_usage_title),
          description = stringResource(R.string.perm_usage_desc),
          isGranted = status.hasUsageStats,
          onGrant = { FocusPermissionHelper.openUsageStatsSettings(context) }
        )

        PermissionRowItem(
          icon = Icons.Default.Layers,
          title = stringResource(R.string.perm_overlay_title),
          description = stringResource(R.string.perm_overlay_desc),
          isGranted = status.hasOverlay,
          onGrant = { FocusPermissionHelper.openOverlaySettings(context) }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          PermissionRowItem(
            icon = Icons.Default.NotificationsActive,
            title = stringResource(R.string.perm_notif_title),
            description = stringResource(R.string.perm_notif_desc),
            isGranted = status.hasNotifications,
            onGrant = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onRefresh()
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
      ) {
        Text(stringResource(R.string.done), color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  )
}

@Composable
private fun PermissionRowItem(
  icon: ImageVector,
  title: String,
  description: String,
  isGranted: Boolean,
  onGrant: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .background(SurfaceCard)
      .border(
        BorderStroke(1.dp, if (isGranted) AccentEmerald.copy(alpha = 0.5f) else SurfaceBorder),
        RoundedCornerShape(12.dp)
      )
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background((if (isGranted) AccentEmerald else AccentGold).copy(alpha = 0.16f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isGranted) AccentEmerald else AccentGold,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
      Text(description, style = MaterialTheme.typography.labelSmall, color = TextMuted)
    }

    Spacer(modifier = Modifier.width(8.dp))

    if (isGranted) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentEmerald, modifier = Modifier.size(16.dp))
        Text(stringResource(R.string.perm_granted), color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    } else {
      OutlinedButton(
        onClick = onGrant,
        border = BorderStroke(1.dp, PrimaryBlueLight),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text(stringResource(R.string.perm_grant_btn), color = PrimaryBlueLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}

@Composable
fun CustomFocusDurationDialog(
  initialMinutes: Int,
  onConfirm: (Int) -> Unit,
  onDismiss: () -> Unit
) {
  var text by remember { mutableStateOf(initialMinutes.toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceSlate,
    title = {
      Text(
        text = stringResource(R.string.custom_minutes_dialog_title),
        color = TextPrimary,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      OutlinedTextField(
        value = text,
        onValueChange = { input -> text = input.filter { it.isDigit() }.take(3) },
        label = { Text(stringResource(R.string.custom_minutes_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = PrimaryBlue,
          unfocusedBorderColor = SurfaceBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        )
      )
    },
    confirmButton = {
      Button(
        onClick = {
          val parsed = text.toIntOrNull()?.coerceIn(1, 180) ?: initialMinutes
          onConfirm(parsed)
        },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
      ) {
        Text(stringResource(R.string.save), color = Color.White, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel), color = TextMuted)
      }
    }
  )
}
