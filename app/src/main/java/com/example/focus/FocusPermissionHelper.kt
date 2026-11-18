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

package com.example.focus

import android.Manifest
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledAppInfo(
  val packageName: String,
  val appName: String,
  val icon: Drawable?
)

data class FocusPermissionsStatus(
  val hasDnd: Boolean = false,
  val hasUsageStats: Boolean = false,
  val hasOverlay: Boolean = false,
  val hasNotifications: Boolean = true
) {
  val allBlockerPermissionsGranted: Boolean
    get() = hasDnd && hasUsageStats && hasOverlay

  fun hasMissingPermissionsForFocus(
    dndEnabled: Boolean,
    hasBlockedApps: Boolean
  ): Boolean {
    if (!hasNotifications) return true
    if (dndEnabled && !hasDnd) return true
    if (hasBlockedApps && (!hasUsageStats || !hasOverlay)) return true
    // Also prompt on initial Focus setup when core shield permissions haven't been granted yet
    if (!hasDnd && !hasUsageStats && !hasOverlay) return true
    return false
  }
}

object FocusPermissionHelper {

  fun checkPermissions(context: Context): FocusPermissionsStatus {
    return FocusPermissionsStatus(
      hasDnd = hasDndPermission(context),
      hasUsageStats = hasUsageStatsPermission(context),
      hasOverlay = hasOverlayPermission(context),
      hasNotifications = hasNotificationPermission(context)
    )
  }

  fun hasDndPermission(context: Context): Boolean = runCatching {
    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    nm?.isNotificationPolicyAccessGranted == true
  }.getOrDefault(false)

  @Suppress("DEPRECATION")
  fun hasUsageStatsPermission(context: Context): Boolean = runCatching {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      appOps.unsafeCheckOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
      )
    } else {
      appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
      )
    }
    mode == AppOpsManager.MODE_ALLOWED
  }.getOrDefault(false)

  fun hasOverlayPermission(context: Context): Boolean = runCatching {
    Settings.canDrawOverlays(context)
  }.getOrDefault(false)

  fun hasNotificationPermission(context: Context): Boolean = runCatching {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }.getOrDefault(true)

  fun openDndSettings(context: Context): Boolean {
    return launchSettingsWithFallback(
      context,
      Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS),
      appDetailsIntent(context),
      Intent(Settings.ACTION_SETTINGS)
    )
  }

  fun openUsageStatsSettings(context: Context): Boolean {
    val withPkg = Intent(
      Settings.ACTION_USAGE_ACCESS_SETTINGS,
      Uri.fromParts("package", context.packageName, null)
    )
    return launchSettingsWithFallback(
      context,
      withPkg,
      Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
      appDetailsIntent(context),
      Intent(Settings.ACTION_SETTINGS)
    )
  }

  fun openOverlaySettings(context: Context): Boolean {
    val withPkg = Intent(
      Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
      Uri.parse("package:${context.packageName}")
    )
    return launchSettingsWithFallback(
      context,
      withPkg,
      Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION),
      appDetailsIntent(context),
      Intent(Settings.ACTION_SETTINGS)
    )
  }

  private fun appDetailsIntent(context: Context): Intent {
    return Intent(
      Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
      Uri.fromParts("package", context.packageName, null)
    )
  }

  private fun launchSettingsWithFallback(context: Context, vararg intents: Intent): Boolean {
    for (candidate in intents) {
      val prepared = Intent(candidate).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      val started = runCatching {
        context.startActivity(prepared)
        true
      }.getOrDefault(false)
      if (started) return true
    }
    return false
  }

  suspend fun getLaunchableApps(context: Context): List<InstalledAppInfo> = withContext(Dispatchers.IO) {
    val pm = context.packageManager
    val launcherIntent = Intent(Intent.ACTION_MAIN, null).apply {
      addCategory(Intent.CATEGORY_LAUNCHER)
    }

    val resolveList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      pm.queryIntentActivities(launcherIntent, PackageManager.ResolveInfoFlags.of(0L))
    } else {
      @Suppress("DEPRECATION")
      pm.queryIntentActivities(launcherIntent, 0)
    }

    val selfPkg = context.packageName
    resolveList
      .asSequence()
      .mapNotNull { info ->
        val pkg = info.activityInfo?.packageName ?: return@mapNotNull null
        if (pkg == selfPkg) return@mapNotNull null
        val label = info.loadLabel(pm)?.toString()?.takeIf { it.isNotBlank() } ?: pkg
        val icon = runCatching { info.loadIcon(pm) }.getOrNull()
        InstalledAppInfo(packageName = pkg, appName = label, icon = icon)
      }
      .distinctBy { it.packageName }
      .sortedBy { it.appName.lowercase() }
      .toList()
  }
}
