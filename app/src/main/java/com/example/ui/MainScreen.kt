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

package com.example.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.screens.AppBlockerPickerDialog
import com.example.ui.screens.FocusPermissionsDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsBottomSheet
import com.example.ui.screens.TimerScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundNavy
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.launch

sealed class NavigationTab(
  val index: Int,
  @StringRes val titleResId: Int,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  object Today : NavigationTab(0, R.string.nav_today, Icons.Filled.Today, Icons.Outlined.Today, "tab_today")
  object Focus : NavigationTab(1, R.string.nav_focus, Icons.Filled.HourglassBottom, Icons.Outlined.HourglassBottom, "tab_timer")
  object Statistics : NavigationTab(2, R.string.nav_statistics, Icons.Filled.BarChart, Icons.Outlined.BarChart, "tab_history")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  viewModel: DisciplineViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
  var showSettingsSheet by remember { mutableStateOf(false) }
  var showAppPickerDialog by remember { mutableStateOf(false) }
  var showPermissionsDialog by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  val habits by viewModel.habitsUiState.collectAsStateWithLifecycle()
  val activeStreak by viewModel.activeStreak.collectAsStateWithLifecycle()
  val currentRank by viewModel.currentRank.collectAsStateWithLifecycle()
  val nextRank by viewModel.nextRank.collectAsStateWithLifecycle()
  val focusSessions by viewModel.focusSessions.collectAsStateWithLifecycle()
  val totalFocusMinutes by viewModel.totalFocusMinutes.collectAsStateWithLifecycle()
  val totalCompletions by viewModel.totalCompletions.collectAsStateWithLifecycle()
  val completedDates by viewModel.completedDates.collectAsStateWithLifecycle()
  val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
  val focusState by viewModel.focusState.collectAsStateWithLifecycle()
  val permissionsStatus by viewModel.permissionsStatus.collectAsStateWithLifecycle()
  val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
  val isLoadingApps by viewModel.isLoadingApps.collectAsStateWithLifecycle()

  // Refresh Focus Blocker permissions automatically when returning from Android Settings
  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.refreshPermissions()
  }

  val tabs = listOf(
    NavigationTab.Today,
    NavigationTab.Focus,
    NavigationTab.Statistics
  )

  fun showMessage(msg: String) {
    scope.launch {
      snackbarHostState.currentSnackbarData?.dismiss()
      snackbarHostState.showSnackbar(msg)
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BackgroundNavy,
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = BackgroundNavy,
          titleContentColor = TextPrimary
        ),
        title = {
          Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
        },
        actions = {
          // Streak pill
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(AccentGold.copy(alpha = 0.16f))
              .padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = AccentGold,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = stringResource(R.string.days_count, activeStreak),
              color = AccentGold,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Settings Gear Button
          IconButton(
            onClick = { showSettingsSheet = true },
            modifier = Modifier.testTag("top_settings_button")
          ) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = stringResource(R.string.settings_title),
              tint = TextPrimary
            )
          }
        }
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = SurfaceSlate,
        tonalElevation = 8.dp
      ) {
        tabs.forEach { tab ->
          val isSelected = selectedTabIndex == tab.index
          val title = stringResource(tab.titleResId)
          NavigationBarItem(
            selected = isSelected,
            onClick = { selectedTabIndex = tab.index },
            icon = {
              Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = title
              )
            },
            label = {
              Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else TextMuted
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Color.White,
              unselectedIconColor = TextMuted,
              indicatorColor = PrimaryBlue
            ),
            modifier = Modifier.testTag(tab.testTag)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTabIndex) {
        0 -> TodayScreen(
          habits = habits,
          languageCode = appSettings.languageCode,
          hapticsEnabled = appSettings.hapticsEnabled,
          onToggleHabit = { habitId -> viewModel.toggleHabit(habitId) },
          onAddHabit = { title, desc, cat, hour, minute, alarmEnabled ->
            viewModel.addHabit(title, desc, cat, hour, minute, alarmEnabled)
          },
          onUpdateHabit = { habit -> viewModel.updateHabit(habit) },
          onDeleteHabit = { habitId -> viewModel.deleteHabit(habitId) },
          onShowMessage = ::showMessage
        )

        1 -> TimerScreen(
          focusState = focusState,
          appSettings = appSettings,
          permissionsStatus = permissionsStatus,
          onSelectDuration = { viewModel.selectFocusMinutes(it) },
          onStartOrToggle = { viewModel.startOrToggleFocus() },
          onStopFocus = { viewModel.stopFocusSession() },
          onToggleDnd = { viewModel.setDndEnabled(it) },
          onOpenAppPicker = { showAppPickerDialog = true },
          onOpenPermissions = { showPermissionsDialog = true },
          onAcknowledgeCompletion = { viewModel.acknowledgeFocusCompletion() },
          onDismissInterceptedApp = { viewModel.clearInterceptedAppAlert() }
        )

        2 -> HistoryScreen(
          activeStreak = activeStreak,
          totalCompletions = totalCompletions,
          totalFocusMinutes = totalFocusMinutes,
          completedDates = completedDates,
          focusSessions = focusSessions,
          currentRank = currentRank,
          nextRank = nextRank,
          languageCode = appSettings.languageCode
        )
      }
    }
  }

  if (showSettingsSheet) {
    SettingsBottomSheet(
      settings = appSettings,
      onSelectLanguage = { code -> viewModel.setLanguage(code) },
      onToggleSound = { viewModel.setSoundEnabled(it) },
      onToggleHaptics = { viewModel.setHapticsEnabled(it) },
      onOpenBlockedApps = { showAppPickerDialog = true },
      onOpenPermissions = { showPermissionsDialog = true },
      onDismiss = { showSettingsSheet = false }
    )
  }

  if (showAppPickerDialog) {
    AppBlockerPickerDialog(
      installedApps = installedApps,
      isLoading = isLoadingApps,
      blockedPackages = appSettings.blockedPackages,
      onLoadApps = { viewModel.loadInstalledApps() },
      onToggleApp = { pkg -> viewModel.toggleBlockedApp(pkg) },
      onDismiss = { showAppPickerDialog = false }
    )
  }

  if (showPermissionsDialog) {
    FocusPermissionsDialog(
      status = permissionsStatus,
      onRefresh = { viewModel.refreshPermissions() },
      onDismiss = { showPermissionsDialog = false }
    )
  }
}
