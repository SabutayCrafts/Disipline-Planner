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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.SettingsDataStore
import com.example.util.LocaleHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DisciplineAlarmReceiver : BroadcastReceiver() {

  companion object {
    const val ACTION_TRIGGER_ALARM = "com.example.alarm.ACTION_TRIGGER_ALARM"
    const val ACTION_DISMISS_ALARM = "com.example.alarm.ACTION_DISMISS_ALARM"

    const val EXTRA_ALARM_ID = "extra_alarm_id"
    const val EXTRA_ALARM_TITLE = "extra_alarm_title"
    const val EXTRA_ALARM_HOUR = "extra_alarm_hour"
    const val EXTRA_ALARM_MINUTE = "extra_alarm_minute"
    const val EXTRA_REPEAT_MODE = "extra_repeat_mode"

    private const val ALARM_CHANNEL_ID = "discipline_alarm_channel"
    private var activeRingtone: Ringtone? = null

    fun stopActiveRingtone() {
      runCatching { activeRingtone?.stop() }
      activeRingtone = null
    }
  }

  override fun onReceive(context: Context, intent: Intent?) {
    val action = intent?.action ?: return
    val alarmId = intent.getLongExtra(EXTRA_ALARM_ID, 0L)

    if (action == ACTION_DISMISS_ALARM) {
      stopActiveRingtone()
      val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      nm?.cancel(notificationIdFor(alarmId))
      return
    }

    if (action == ACTION_TRIGGER_ALARM) {
      val title = intent.getStringExtra(EXTRA_ALARM_TITLE).orEmpty()
      val hour = intent.getIntExtra(EXTRA_ALARM_HOUR, 7)
      val minute = intent.getIntExtra(EXTRA_ALARM_MINUTE, 0)
      val repeatStr = intent.getStringExtra(EXTRA_REPEAT_MODE) ?: AlarmRepeatMode.ONCE.name
      val repeatMode = AlarmRepeatMode.fromStorage(repeatStr)

      val pendingResult = goAsync()
      CoroutineScope(Dispatchers.IO).launch {
        try {
          val lang = runCatching {
            SettingsDataStore(context).settingsFlow.first().languageCode
          }.getOrDefault("az")
          val locCtx = LocaleHelper.createLocalizedContext(context.applicationContext, lang)
          val resolvedTitle = title.ifBlank { locCtx.getString(R.string.app_name) }

          showAlarmNotification(locCtx, alarmId, resolvedTitle, hour, minute)
          playAlarmSound(context.applicationContext)
          vibrateAlarm(context.applicationContext)

          // Reschedule repeating alarms or mark one-shot alarm inactive in DB
          if (alarmId > 0L) {
            val db = AppDatabase.getDatabase(context.applicationContext)
            val saved = db.alarmDao().getAlarmById(alarmId)
            if (saved != null) {
              if (repeatMode == AlarmRepeatMode.ONCE) {
                db.alarmDao().updateAlarm(saved.copy(isEnabled = false))
              } else {
                AlarmHelper.scheduleInAppAlarm(context.applicationContext, saved)
              }
            }
          }
        } finally {
          pendingResult.finish()
        }
      }
    }
  }

  private fun showAlarmNotification(
    locCtx: Context,
    alarmId: Long,
    title: String,
    hour: Int,
    minute: Int
  ) {
    val nm = locCtx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      val channel = NotificationChannel(
        ALARM_CHANNEL_ID,
        locCtx.getString(R.string.alarm_channel_name),
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = locCtx.getString(R.string.alarm_channel_desc)
        enableVibration(true)
        setSound(
          alarmUri,
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
      }
      nm.createNotificationChannel(channel)
    }

    val openIntent = PendingIntent.getActivity(
      locCtx,
      notificationIdFor(alarmId),
      Intent(locCtx, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val dismissIntent = PendingIntent.getBroadcast(
      locCtx,
      notificationIdFor(alarmId) + 10_000,
      Intent(locCtx, DisciplineAlarmReceiver::class.java).apply {
        this.action = ACTION_DISMISS_ALARM
        putExtra(EXTRA_ALARM_ID, alarmId)
      },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(locCtx, ALARM_CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
      .setContentTitle(title)
      .setContentText(locCtx.getString(R.string.alarm_notif_subtitle, hour, minute))
      .setCategory(NotificationCompat.CATEGORY_ALARM)
      .setPriority(NotificationCompat.PRIORITY_MAX)
      .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
      .setAutoCancel(true)
      .setContentIntent(openIntent)
      .addAction(
        android.R.drawable.ic_menu_close_clear_cancel,
        locCtx.getString(R.string.alarm_dismiss_action),
        dismissIntent
      )
      .build()

    nm.notify(notificationIdFor(alarmId), notification)
  }

  private fun playAlarmSound(context: Context) {
    runCatching {
      stopActiveRingtone()
      val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      val ringtone = RingtoneManager.getRingtone(context, uri)
      if (ringtone != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
          ringtone.isLooping = false
        }
        ringtone.audioAttributes = AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_ALARM)
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .build()
        activeRingtone = ringtone
        ringtone.play()
      }
    }
  }

  private fun vibrateAlarm(context: Context) {
    runCatching {
      val pattern = longArrayOf(0, 450, 250, 450, 250, 450)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
          @Suppress("DEPRECATION")
          vibrator?.vibrate(pattern, -1)
        }
      }
    }
  }

  private fun notificationIdFor(alarmId: Long): Int {
    return 7000 + (alarmId.toInt() and 0x0FFF)
  }
}
