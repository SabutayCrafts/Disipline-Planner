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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.AppSettings
import com.example.data.local.SettingsDataStore
import com.example.data.local.entity.FocusSessionEntity
import com.example.util.LocaleHelper
import com.example.util.SoundHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FocusBlockerService : Service() {

  companion object {
    const val ACTION_START = "com.example.focus.ACTION_START"
    const val ACTION_PAUSE = "com.example.focus.ACTION_PAUSE"
    const val ACTION_RESUME = "com.example.focus.ACTION_RESUME"
    const val ACTION_STOP = "com.example.focus.ACTION_STOP"

    private const val CHANNEL_ID = "focus_shield_channel"
    private const val NOTIFICATION_ID = 4001

    fun startFocus(context: Context) {
      val intent = Intent(context, FocusBlockerService::class.java).apply {
        action = ACTION_START
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
      } else {
        context.startService(intent)
      }
    }

    fun pauseFocus(context: Context) {
      val intent = Intent(context, FocusBlockerService::class.java).apply {
        action = ACTION_PAUSE
      }
      context.startService(intent)
    }

    fun resumeFocus(context: Context) {
      val intent = Intent(context, FocusBlockerService::class.java).apply {
        action = ACTION_RESUME
      }
      context.startService(intent)
    }

    fun stopFocus(context: Context) {
      val intent = Intent(context, FocusBlockerService::class.java).apply {
        action = ACTION_STOP
      }
      context.startService(intent)
    }
  }

  private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
  private var tickerJob: Job? = null
  private var monitorJob: Job? = null

  private lateinit var settingsDataStore: SettingsDataStore
  private var currentSettings: AppSettings = AppSettings()

  private var previousDndFilter: Int? = null
  private var overlayView: View? = null
  private var overlayTimerText: TextView? = null
  private var currentOverlayPkg: String? = null
  private var lastKnownForegroundPkg: String? = null

  override fun onCreate() {
    super.onCreate()
    settingsDataStore = SettingsDataStore(applicationContext)
    createNotificationChannel()
    serviceScope.launch {
      settingsDataStore.settingsFlow.collect { settings ->
        currentSettings = settings
      }
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    when (intent?.action) {
      ACTION_START -> {
        serviceScope.launch {
          currentSettings = settingsDataStore.settingsFlow.first()
          beginForegroundSession()
        }
      }
      ACTION_PAUSE -> {
        FocusStateHolder.pauseSession()
        removeShieldOverlay()
        updateNotification()
      }
      ACTION_RESUME -> {
        FocusStateHolder.resumeSession()
        updateNotification()
      }
      ACTION_STOP -> {
        stopAndCleanup(reset = true)
      }
    }
    return START_NOT_STICKY
  }

  private fun beginForegroundSession() {
    val dndActivated = activateDndIfPermitted()
    FocusStateHolder.startSession(dndActive = dndActivated)

    val notification = buildNotification()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      ServiceCompat.startForeground(
        this,
        NOTIFICATION_ID,
        notification,
        ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
      )
    } else {
      startForeground(NOTIFICATION_ID, notification)
    }

    startCountdownTicker()
    startAppMonitorLoop()
  }

  private fun startCountdownTicker() {
    tickerJob?.cancel()
    tickerJob = serviceScope.launch {
      while (true) {
        delay(1000L)
        val state = FocusStateHolder.state.value
        if (!state.isRunning) break
        if (!state.isPaused) {
          if (state.remainingSeconds > 1) {
            FocusStateHolder.tickSecond()
            updateNotification()
            updateOverlayCountdown()
          } else {
            onFocusSessionFinished()
            break
          }
        }
      }
    }
  }

  private fun startAppMonitorLoop() {
    monitorJob?.cancel()
    monitorJob = serviceScope.launch {
      while (true) {
        delay(650L)
        val state = FocusStateHolder.state.value
        if (!state.isRunning) break
        if (state.isPaused) {
          removeShieldOverlay()
          continue
        }

        val blocked = currentSettings.blockedPackages
        if (blocked.isEmpty() || !FocusPermissionHelper.hasUsageStatsPermission(this@FocusBlockerService)) {
          continue
        }

        val fgPkg = withContext(Dispatchers.IO) {
          detectForegroundPackage()
        }

        if (fgPkg != null && fgPkg != packageName) {
          if (blocked.contains(fgPkg)) {
            val appName = resolveAppName(fgPkg)
            FocusStateHolder.markInterceptedApp(appName)
            if (Settings.canDrawOverlays(this@FocusBlockerService)) {
              if (currentOverlayPkg != fgPkg || overlayView == null) {
                showShieldOverlay(fgPkg, appName)
              }
            } else {
              bringAppToFront()
            }
          } else if (fgPkg == packageName) {
            removeShieldOverlay()
          }
        } else if (fgPkg == packageName) {
          removeShieldOverlay()
        }
      }
    }
  }

  private fun detectForegroundPackage(): String? {
    val usm = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager ?: return null
    val now = System.currentTimeMillis()
    val events = usm.queryEvents(now - 5000L, now)
    val event = UsageEvents.Event()
    var latestPkg: String? = null
    var latestTime = 0L

    while (events.hasNextEvent()) {
      events.getNextEvent(event)
      val isForegroundEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        event.eventType == UsageEvents.Event.ACTIVITY_RESUMED
      } else {
        @Suppress("DEPRECATION")
        event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
      }
      if (isForegroundEvent && event.timeStamp >= latestTime) {
        latestTime = event.timeStamp
        latestPkg = event.packageName
      }
    }

    if (latestPkg != null) {
      lastKnownForegroundPkg = latestPkg
    }
    return lastKnownForegroundPkg
  }

  private fun resolveAppName(pkg: String): String {
    return try {
      val info = packageManager.getApplicationInfo(pkg, 0)
      packageManager.getApplicationLabel(info).toString()
    } catch (_: Exception) {
      pkg
    }
  }

  private fun showShieldOverlay(pkg: String, appName: String) {
    removeShieldOverlay()
    val wm = getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return
    val locCtx = LocaleHelper.createLocalizedContext(this, currentSettings.languageCode)

    val container = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      gravity = Gravity.CENTER
      setBackgroundColor(Color.parseColor("#F50B1120"))
      setPadding(dp(28), dp(48), dp(28), dp(48))
    }

    val card = LinearLayout(this).apply {
      orientation = LinearLayout.VERTICAL
      gravity = Gravity.CENTER_HORIZONTAL
      setPadding(dp(24), dp(28), dp(24), dp(28))
      background = GradientDrawable().apply {
        setColor(Color.parseColor("#1E293B"))
        cornerRadius = dp(24).toFloat()
        setStroke(dp(2), Color.parseColor("#EF4444"))
      }
    }

    val iconText = TextView(this).apply {
      text = "🛡️"
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f)
      gravity = Gravity.CENTER
    }

    val titleView = TextView(this).apply {
      text = locCtx.getString(R.string.shield_title)
      setTextColor(Color.parseColor("#F8FAFC"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
      setTypeface(typeface, android.graphics.Typeface.BOLD)
      gravity = Gravity.CENTER
      setPadding(0, dp(12), 0, dp(8))
    }

    val msgView = TextView(this).apply {
      text = locCtx.getString(R.string.shield_message, appName)
      setTextColor(Color.parseColor("#CBD5E1"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
      gravity = Gravity.CENTER
      setPadding(0, 0, 0, dp(16))
    }

    val timerView = TextView(this).apply {
      text = locCtx.getString(R.string.shield_remaining, formatRemaining())
      setTextColor(Color.parseColor("#60A5FA"))
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
      setTypeface(typeface, android.graphics.Typeface.BOLD)
      gravity = Gravity.CENTER
      setPadding(0, 0, 0, dp(22))
    }
    overlayTimerText = timerView

    val returnBtn = Button(this).apply {
      text = locCtx.getString(R.string.shield_return_btn)
      setTextColor(Color.WHITE)
      setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
      setTypeface(typeface, android.graphics.Typeface.BOLD)
      setPadding(dp(24), dp(14), dp(24), dp(14))
      background = GradientDrawable().apply {
        setColor(Color.parseColor("#2563EB"))
        cornerRadius = dp(14).toFloat()
      }
      setOnClickListener {
        removeShieldOverlay()
        bringAppToFront()
      }
    }

    card.addView(iconText)
    card.addView(titleView)
    card.addView(msgView)
    card.addView(timerView)
    card.addView(returnBtn)
    container.addView(card)

    val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    } else {
      @Suppress("DEPRECATION")
      WindowManager.LayoutParams.TYPE_PHONE
    }

    val params = WindowManager.LayoutParams(
      WindowManager.LayoutParams.MATCH_PARENT,
      WindowManager.LayoutParams.MATCH_PARENT,
      layoutType,
      WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
      PixelFormat.TRANSLUCENT
    ).apply {
      gravity = Gravity.CENTER
    }

    runCatching {
      wm.addView(container, params)
      overlayView = container
      currentOverlayPkg = pkg
    }
  }

  private fun updateOverlayCountdown() {
    val locCtx = LocaleHelper.createLocalizedContext(this, currentSettings.languageCode)
    overlayTimerText?.text = locCtx.getString(R.string.shield_remaining, formatRemaining())
  }

  private fun removeShieldOverlay() {
    val view = overlayView ?: return
    val wm = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
    runCatching { wm?.removeView(view) }
    overlayView = null
    overlayTimerText = null
    currentOverlayPkg = null
  }

  private fun bringAppToFront() {
    val intent = Intent(this, MainActivity::class.java).apply {
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
    }
    runCatching { startActivity(intent) }
  }

  private suspend fun onFocusSessionFinished() {
    val minutes = FocusStateHolder.state.value.selectedMinutes
    withContext(Dispatchers.IO) {
      val db = AppDatabase.getDatabase(applicationContext)
      val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
      db.focusSessionDao().insertSession(
        FocusSessionEntity(
          durationMinutes = minutes,
          sessionType = "Focus",
          dateString = today
        )
      )
    }
    if (currentSettings.soundEnabled) {
      SoundHelper.playFocusFinishedChime()
    }
    FocusStateHolder.completeSession()
    stopAndCleanup(reset = false)
  }

  private fun stopAndCleanup(reset: Boolean) {
    tickerJob?.cancel()
    monitorJob?.cancel()
    removeShieldOverlay()
    restoreDndIfChanged()
    if (reset) {
      FocusStateHolder.resetSession()
    }
    ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    stopSelf()
  }

  private fun activateDndIfPermitted(): Boolean {
    if (!currentSettings.dndEnabled) return false
    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return false
    if (!nm.isNotificationPolicyAccessGranted) return false
    return try {
      previousDndFilter = nm.currentInterruptionFilter
      nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
      true
    } catch (_: Exception) {
      false
    }
  }

  private fun restoreDndIfChanged() {
    val prev = previousDndFilter ?: return
    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    if (nm.isNotificationPolicyAccessGranted) {
      runCatching { nm.setInterruptionFilter(prev) }
    }
    previousDndFilter = null
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val locCtx = LocaleHelper.createLocalizedContext(this, currentSettings.languageCode)
      val channel = NotificationChannel(
        CHANNEL_ID,
        locCtx.getString(R.string.fgs_channel_name),
        NotificationManager.IMPORTANCE_LOW
      ).apply {
        description = locCtx.getString(R.string.fgs_channel_desc)
        setShowBadge(false)
      }
      val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      nm?.createNotificationChannel(channel)
    }
  }

  private fun buildNotification(): Notification {
    val locCtx = LocaleHelper.createLocalizedContext(this, currentSettings.languageCode)
    val openIntent = PendingIntent.getActivity(
      this,
      0,
      Intent(this, MainActivity::class.java),
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val stopIntent = PendingIntent.getService(
      this,
      1,
      Intent(this, FocusBlockerService::class.java).apply { action = ACTION_STOP },
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return NotificationCompat.Builder(this, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
      .setContentTitle(locCtx.getString(R.string.fgs_notif_title, formatRemaining()))
      .setContentText(locCtx.getString(R.string.fgs_notif_body, currentSettings.blockedPackages.size))
      .setOngoing(true)
      .setOnlyAlertOnce(true)
      .setContentIntent(openIntent)
      .addAction(
        android.R.drawable.ic_menu_close_clear_cancel,
        locCtx.getString(R.string.fgs_notif_stop),
        stopIntent
      )
      .build()
  }

  private fun updateNotification() {
    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
    nm.notify(NOTIFICATION_ID, buildNotification())
  }

  private fun formatRemaining(): String {
    val sec = FocusStateHolder.state.value.remainingSeconds
    return String.format(Locale.US, "%02d:%02d", sec / 60, sec % 60)
  }

  private fun dp(value: Int): Int {
    return TypedValue.applyDimension(
      TypedValue.COMPLEX_UNIT_DIP,
      value.toFloat(),
      resources.displayMetrics
    ).toInt()
  }

  override fun onDestroy() {
    tickerJob?.cancel()
    monitorJob?.cancel()
    removeShieldOverlay()
    restoreDndIfChanged()
    serviceScope.cancel()
    super.onDestroy()
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
