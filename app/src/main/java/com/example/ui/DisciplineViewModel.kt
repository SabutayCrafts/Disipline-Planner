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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alarm.AlarmHelper
import com.example.alarm.AlarmRepeatMode
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettings
import com.example.data.local.SettingsDataStore
import com.example.data.local.entity.AlarmEntity
import com.example.data.local.entity.FocusSessionEntity
import com.example.data.local.entity.HabitEntity
import com.example.data.repository.DisciplineRepository
import com.example.focus.FocusBlockerService
import com.example.focus.FocusPermissionHelper
import com.example.focus.FocusPermissionsStatus
import com.example.focus.FocusStateHolder
import com.example.focus.FocusTimerState
import com.example.focus.InstalledAppInfo
import com.example.model.DisciplineRank
import com.example.model.DisciplineRankCatalog
import com.example.model.HabitItemUiState
import com.example.util.LocaleHelper
import com.example.util.SoundHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DisciplineViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  private val settingsDataStore = SettingsDataStore(application)
  private val repository = DisciplineRepository(
    database.habitDao(),
    database.habitLogDao(),
    database.focusSessionDao(),
    database.alarmDao()
  )

  val todayDateString = repository.getTodayDateString()

  val scheduledAlarms: StateFlow<List<AlarmEntity>> = repository.getAllAlarms()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // App Settings (Language, Blocked Apps, DND, Audio & Haptics)
  val appSettings: StateFlow<AppSettings> = settingsDataStore.settingsFlow
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = AppSettings()
    )

  // Focus State synchronized with FocusBlockerService
  val focusState: StateFlow<FocusTimerState> = FocusStateHolder.state

  // Focus Permissions status
  private val _permissionsStatus = MutableStateFlow(FocusPermissionHelper.checkPermissions(application))
  val permissionsStatus: StateFlow<FocusPermissionsStatus> = _permissionsStatus.asStateFlow()

  // Installed apps list for App Blocker picker
  private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
  val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

  private val _isLoadingApps = MutableStateFlow(false)
  val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

  // Distinct completed dates for streak calculation
  val completedDates: StateFlow<List<String>> = repository.getDistinctCompletedDates()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  // Overall active streak
  val activeStreak: StateFlow<Int> = completedDates
    .map { dates -> repository.calculateStreak(dates) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  // Current Rank and Next Rank in 25-tier Streak Milestones
  val currentRank: StateFlow<DisciplineRank> = activeStreak
    .map { streak -> DisciplineRankCatalog.getCurrentRank(streak) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = DisciplineRankCatalog.ALL_RANKS.first()
    )

  val nextRank: StateFlow<DisciplineRank?> = currentRank
    .map { rank -> DisciplineRankCatalog.getNextRank(rank.level) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = DisciplineRankCatalog.ALL_RANKS[1]
    )

  // Active habits list combined with today's completion logs and individual streaks
  val habitsUiState: StateFlow<List<HabitItemUiState>> = combine(
    repository.getAllActiveHabits(),
    repository.getLogsForDate(todayDateString),
    repository.getAllLogs()
  ) { habits, todayLogs, allLogs ->
    val completedHabitIdsToday = todayLogs.map { it.habitId }.toSet()

    habits.map { habit ->
      val habitLogs = allLogs.filter { it.habitId == habit.id }
      val habitDates = habitLogs.map { it.dateString }
      val streak = repository.calculateStreak(habitDates)
      val isCompleted = completedHabitIdsToday.contains(habit.id)

      HabitItemUiState(
        habit = habit,
        isCompletedToday = isCompleted,
        currentStreak = streak,
        totalCompletions = habitLogs.size
      )
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  // Focus Sessions
  val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.getAllFocusSessions()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val totalFocusMinutes: StateFlow<Int> = repository.getTotalFocusMinutes()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  val totalCompletions: StateFlow<Int> = repository.getTotalCompletionsCount()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  fun toggleHabit(habitId: Long, onResult: (Boolean) -> Unit = {}) {
    viewModelScope.launch {
      val nowCompleted = repository.toggleHabitCompletion(habitId, todayDateString)
      if (nowCompleted && appSettings.value.soundEnabled) {
        SoundHelper.playCompletionChime()
      }
      onResult(nowCompleted)
    }
  }

  fun addHabit(
    title: String,
    description: String,
    category: String,
    reminderHour: Int?,
    reminderMinute: Int?,
    alarmEnabled: Boolean
  ) {
    viewModelScope.launch {
      val habitId = repository.insertHabit(
        HabitEntity(
          title = title,
          description = description,
          category = category,
          reminderHour = reminderHour,
          reminderMinute = reminderMinute,
          alarmEnabled = alarmEnabled
        )
      )
      if (alarmEnabled && reminderHour != null && reminderMinute != null) {
        persistAndScheduleAlarmInternal(
          title = title,
          hour = reminderHour,
          minute = reminderMinute,
          repeatMode = AlarmRepeatMode.EVERY_DAY,
          linkedHabitId = habitId
        )
      }
    }
  }

  fun updateHabit(habit: HabitEntity) {
    viewModelScope.launch {
      repository.updateHabit(habit)
      if (habit.alarmEnabled && habit.reminderHour != null && habit.reminderMinute != null) {
        persistAndScheduleAlarmInternal(
          title = habit.title,
          hour = habit.reminderHour,
          minute = habit.reminderMinute,
          repeatMode = AlarmRepeatMode.EVERY_DAY,
          linkedHabitId = habit.id
        )
      }
    }
  }

  fun deleteHabit(habitId: Long) {
    viewModelScope.launch {
      val existingAlarm = repository.getAlarmByHabitId(habitId)
      if (existingAlarm != null) {
        AlarmHelper.cancelInAppAlarm(getApplication(), existingAlarm.id)
      }
      repository.deleteHabit(habitId)
    }
  }

  fun saveAndScheduleAlarm(
    title: String,
    hour: Int,
    minute: Int,
    repeatMode: AlarmRepeatMode,
    linkedHabitId: Long? = null
  ) {
    viewModelScope.launch {
      persistAndScheduleAlarmInternal(title, hour, minute, repeatMode, linkedHabitId)
    }
  }

  fun toggleAlarmEnabled(alarm: AlarmEntity, enabled: Boolean) {
    viewModelScope.launch {
      val updated = alarm.copy(isEnabled = enabled)
      repository.updateAlarm(updated)
      if (enabled) {
        AlarmHelper.scheduleInAppAlarm(getApplication(), updated)
      } else {
        AlarmHelper.cancelInAppAlarm(getApplication(), updated.id)
      }
    }
  }

  fun deleteAlarm(alarm: AlarmEntity) {
    viewModelScope.launch {
      AlarmHelper.cancelInAppAlarm(getApplication(), alarm.id)
      repository.deleteAlarm(alarm.id)
    }
  }

  private suspend fun persistAndScheduleAlarmInternal(
    title: String,
    hour: Int,
    minute: Int,
    repeatMode: AlarmRepeatMode,
    linkedHabitId: Long?
  ) {
    val existing = linkedHabitId?.let { repository.getAlarmByHabitId(it) }
    val alarm = if (existing != null) {
      existing.copy(
        title = title,
        hour = hour,
        minute = minute,
        repeatMode = repeatMode.name,
        isEnabled = true
      )
    } else {
      AlarmEntity(
        title = title,
        hour = hour,
        minute = minute,
        repeatMode = repeatMode.name,
        isEnabled = true,
        linkedHabitId = linkedHabitId
      )
    }
    val rowId = repository.insertOrUpdateAlarm(alarm)
    val saved = alarm.copy(id = if (alarm.id > 0L) alarm.id else rowId)
    AlarmHelper.scheduleInAppAlarm(getApplication(), saved)
  }

  // Focus Blocker & Timer Controls
  fun selectFocusMinutes(minutes: Int) {
    FocusStateHolder.selectDuration(minutes)
  }

  fun startOrToggleFocus() {
    val ctx = getApplication<Application>()
    val current = focusState.value
    if (!current.isRunning) {
      FocusBlockerService.startFocus(ctx)
    } else if (current.isPaused) {
      FocusBlockerService.resumeFocus(ctx)
    } else {
      FocusBlockerService.pauseFocus(ctx)
    }
  }

  fun stopFocusSession() {
    FocusBlockerService.stopFocus(getApplication())
  }

  fun acknowledgeFocusCompletion() {
    FocusStateHolder.acknowledgeCompletion()
  }

  fun clearInterceptedAppAlert() {
    FocusStateHolder.markInterceptedApp(null)
  }

  fun refreshPermissions(): FocusPermissionsStatus {
    val updated = FocusPermissionHelper.checkPermissions(getApplication())
    _permissionsStatus.value = updated
    return updated
  }

  fun loadInstalledApps() {
    if (_installedApps.value.isNotEmpty() || _isLoadingApps.value) return
    viewModelScope.launch {
      _isLoadingApps.value = true
      _installedApps.value = FocusPermissionHelper.getLaunchableApps(getApplication())
      _isLoadingApps.value = false
    }
  }

  fun toggleBlockedApp(packageName: String) {
    viewModelScope.launch {
      settingsDataStore.toggleBlockedPackage(packageName)
    }
  }

  // Settings & i18n
  fun setLanguage(code: String) {
    viewModelScope.launch {
      settingsDataStore.setLanguageCode(code)
      LocaleHelper.syncSystemAppLocale(getApplication(), code)
    }
  }

  fun setDndEnabled(enabled: Boolean) {
    viewModelScope.launch {
      settingsDataStore.setDndEnabled(enabled)
    }
  }

  fun setSoundEnabled(enabled: Boolean) {
    viewModelScope.launch {
      settingsDataStore.setSoundEnabled(enabled)
    }
  }

  fun setHapticsEnabled(enabled: Boolean) {
    viewModelScope.launch {
      settingsDataStore.setHapticsEnabled(enabled)
    }
  }
}
