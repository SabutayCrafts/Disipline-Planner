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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TimerScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.BackgroundNavy
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

sealed class NavigationTab(
  val index: Int,
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
) {
  object Today : NavigationTab(0, "Bugün", Icons.Filled.Today, Icons.Outlined.Today, "tab_today")
  object Timer : NavigationTab(1, "Fokus", Icons.Filled.HourglassBottom, Icons.Outlined.HourglassBottom, "tab_timer")
  object History : NavigationTab(2, "Statistika", Icons.Filled.BarChart, Icons.Outlined.BarChart, "tab_history")
  object Profile : NavigationTab(3, "Profil", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
}

@Composable
fun MainScreen(
  viewModel: DisciplineViewModel,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

  val habits by viewModel.habitsUiState.collectAsStateWithLifecycle()
  val activeStreak by viewModel.activeStreak.collectAsStateWithLifecycle()
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
  val currentRank by viewModel.currentRank.collectAsStateWithLifecycle()
  val nextRank by viewModel.nextRank.collectAsStateWithLifecycle()
  val focusSessions by viewModel.focusSessions.collectAsStateWithLifecycle()
  val totalFocusMinutes by viewModel.totalFocusMinutes.collectAsStateWithLifecycle()
  val totalCompletions by viewModel.totalCompletions.collectAsStateWithLifecycle()
  val completedDates by viewModel.completedDates.collectAsStateWithLifecycle()

  val tabs = listOf(
    NavigationTab.Today,
    NavigationTab.Timer,
    NavigationTab.History,
    NavigationTab.Profile
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = BackgroundNavy,
    bottomBar = {
      NavigationBar(
        containerColor = SurfaceSlate,
        tonalElevation = 8.dp
      ) {
        tabs.forEach { tab ->
          val isSelected = selectedTabIndex == tab.index
          NavigationBarItem(
            selected = isSelected,
            onClick = { selectedTabIndex = tab.index },
            icon = {
              Icon(
                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                contentDescription = tab.title
              )
            },
            label = {
              Text(
                text = tab.title,
                fontSize = 11.sp,
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
          activeStreak = activeStreak,
          onToggleHabit = { habitId -> viewModel.toggleHabit(habitId) },
          onAddHabit = { title, desc, cat, targetDays -> viewModel.addHabit(title, desc, cat, targetDays) },
          onUpdateHabit = { habit -> viewModel.updateHabit(habit) },
          onDeleteHabit = { habitId -> viewModel.deleteHabit(habitId) }
        )
        1 -> TimerScreen(
          onSessionComplete = { minutes -> viewModel.saveFocusSession(minutes) }
        )
        2 -> HistoryScreen(
          activeStreak = activeStreak,
          totalCompletions = totalCompletions,
          totalFocusMinutes = totalFocusMinutes,
          completedDates = completedDates,
          focusSessions = focusSessions
        )
        3 -> ProfileScreen(
          userProfile = userProfile,
          activeStreak = activeStreak,
          currentRank = currentRank,
          nextRank = nextRank,
          onSaveProfile = { profile -> viewModel.saveUserProfile(profile) }
        )
      }
    }
  }
}
