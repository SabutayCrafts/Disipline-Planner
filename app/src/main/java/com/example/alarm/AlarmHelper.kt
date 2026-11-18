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

package com.example.alarm

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.provider.AlarmClock
import com.example.data.local.entity.AlarmEntity
import java.util.Calendar
import java.util.Locale

enum class AlarmRepeatMode {
  ONCE,
  EVERY_DAY,
  WEEKDAYS;

  fun toCalendarDays(): ArrayList<Int> = when (this) {
    ONCE -> arrayListOf()
    EVERY_DAY -> arrayListOf(
      Calendar.SUNDAY,
      Calendar.MONDAY,
      Calendar.TUESDAY,
      Calendar.WEDNESDAY,
      Calendar.THURSDAY,
      Calendar.FRIDAY,
      Calendar.SATURDAY
    )
    WEEKDAYS -> arrayListOf(
      Calendar.MONDAY,
      Calendar.TUESDAY,
      Calendar.WEDNESDAY,
      Calendar.THURSDAY,
      Calendar.FRIDAY
    )
  }

  companion object {
    fun fromStorage(raw: String): AlarmRepeatMode {
      return entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: ONCE
    }
  }
}

object AlarmHelper {

  val TIME_PRESETS: List<Pair<Int, Int>> = listOf(
    6 to 30,
    7 to 0,
    8 to 0,
    9 to 0,
    14 to 0,
    21 to 0
  )

  private val KNOWN_CLOCK_PACKAGES = listOf(
    "com.google.android.deskclock",
    "com.android.deskclock",
    "com.sec.android.app.clockpackage",
    "com.miui.clock",
    "com.oneplus.deskclock",
    "com.coloros.alarmclock",
    "com.oppo.alarmclock",
    "com.huawei.deskclock",
    "com.asus.deskclock"
  )

  fun getCurrentHourMinute(): Pair<Int, Int> {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.HOUR_OF_DAY) to cal.get(Calendar.MINUTE)
  }

  fun computeNextTriggerMillis(
    hour: Int,
    minute: Int,
    repeatMode: AlarmRepeatMode,
    nowMillis: Long = System.currentTimeMillis()
  ): Long {
    val cal = Calendar.getInstance().apply {
      timeInMillis = nowMillis
      set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
      set(Calendar.MINUTE, minute.coerceIn(0, 59))
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }

    if (cal.timeInMillis <= nowMillis) {
      cal.add(Calendar.DAY_OF_YEAR, 1)
    }

    if (repeatMode == AlarmRepeatMode.WEEKDAYS) {
      while (
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
      ) {
        cal.add(Calendar.DAY_OF_YEAR, 1)
      }
    }

    return cal.timeInMillis
  }

  fun scheduleInAppAlarm(context: Context, alarm: AlarmEntity) {
    if (!alarm.isEnabled) {
      cancelInAppAlarm(context, alarm.id)
      return
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val repeatMode = AlarmRepeatMode.fromStorage(alarm.repeatMode)
    val triggerAt = computeNextTriggerMillis(alarm.hour, alarm.minute, repeatMode)

    val pendingIntent = buildAlarmPendingIntent(context, alarm)
    runCatching {
      val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        alarmManager.canScheduleExactAlarms()
      } else {
        true
      }
      if (canExact) {
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
      } else {
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
      }
    }
  }

  fun cancelInAppAlarm(context: Context, alarmId: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, DisciplineAlarmReceiver::class.java).apply {
      action = DisciplineAlarmReceiver.ACTION_TRIGGER_ALARM
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      alarmId.toInt(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    runCatching { alarmManager.cancel(pendingIntent) }
  }

  private fun buildAlarmPendingIntent(context: Context, alarm: AlarmEntity): PendingIntent {
    val intent = Intent(context, DisciplineAlarmReceiver::class.java).apply {
      action = DisciplineAlarmReceiver.ACTION_TRIGGER_ALARM
      putExtra(DisciplineAlarmReceiver.EXTRA_ALARM_ID, alarm.id)
      putExtra(DisciplineAlarmReceiver.EXTRA_ALARM_TITLE, alarm.title)
      putExtra(DisciplineAlarmReceiver.EXTRA_ALARM_HOUR, alarm.hour)
      putExtra(DisciplineAlarmReceiver.EXTRA_ALARM_MINUTE, alarm.minute)
      putExtra(DisciplineAlarmReceiver.EXTRA_REPEAT_MODE, alarm.repeatMode)
    }
    return PendingIntent.getBroadcast(
      context,
      alarm.id.toInt(),
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
  }

  /**
   * Launches the device's built-in Clock application pre-filled with the scheduled
   * time, label, and optional repeat days. Unwraps Activity if present and retries
   * cleanly without EXTRA_DAYS for OEM Clock compatibility.
   */
  fun scheduleSystemAlarm(
    context: Context,
    habitTitle: String,
    hour: Int,
    minute: Int,
    repeatMode: AlarmRepeatMode = AlarmRepeatMode.ONCE
  ): Boolean {
    val launchCtx = unwrapActivity(context) ?: context
    val days = repeatMode.toCalendarDays()

    val primaryIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
      putExtra(AlarmClock.EXTRA_HOUR, hour.coerceIn(0, 23))
      putExtra(AlarmClock.EXTRA_MINUTES, minute.coerceIn(0, 59))
      if (habitTitle.isNotBlank()) {
        putExtra(AlarmClock.EXTRA_MESSAGE, habitTitle)
      }
      if (days.isNotEmpty()) {
        putIntegerArrayListExtra(AlarmClock.EXTRA_DAYS, days)
      }
      putExtra(AlarmClock.EXTRA_SKIP_UI, false)
      if (launchCtx !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    if (tryStartActivity(launchCtx, primaryIntent)) return true

    // Retry without EXTRA_DAYS in case OEM Clock app rejects it
    val basicIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
      putExtra(AlarmClock.EXTRA_HOUR, hour.coerceIn(0, 23))
      putExtra(AlarmClock.EXTRA_MINUTES, minute.coerceIn(0, 59))
      if (habitTitle.isNotBlank()) {
        putExtra(AlarmClock.EXTRA_MESSAGE, habitTitle)
      }
      putExtra(AlarmClock.EXTRA_SKIP_UI, false)
      if (launchCtx !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return tryStartActivity(launchCtx, basicIntent)
  }

  /**
   * Opens the device Clock app alarms screen directly, falling back across OEM Clock packages.
   */
  fun openSystemClockAlarms(context: Context): Boolean {
    val launchCtx = unwrapActivity(context) ?: context
    val showAlarms = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
      if (launchCtx !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    if (tryStartActivity(launchCtx, showAlarms)) return true

    val pm = context.packageManager
    for (pkg in KNOWN_CLOCK_PACKAGES) {
      val launchIntent = runCatching { pm.getLaunchIntentForPackage(pkg) }.getOrNull()
      if (launchIntent != null) {
        if (launchCtx !is Activity) launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (tryStartActivity(launchCtx, launchIntent)) return true
      }
    }

    val fallbackSet = Intent(AlarmClock.ACTION_SET_ALARM).apply {
      if (launchCtx !is Activity) addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    return tryStartActivity(launchCtx, fallbackSet)
  }

  private fun tryStartActivity(context: Context, intent: Intent): Boolean {
    return runCatching {
      context.startActivity(intent)
      true
    }.getOrDefault(false)
  }

  private fun unwrapActivity(context: Context): Activity? {
    var current: Context? = context
    while (current is ContextWrapper) {
      if (current is Activity) return current
      current = current.baseContext
    }
    return null
  }

  fun formatTime(hour: Int, minute: Int): String {
    return String.format(Locale.US, "%02d:%02d", hour, minute)
  }
}
