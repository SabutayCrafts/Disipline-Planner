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

package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "discipline_settings")

data class AppSettings(
  val languageCode: String = "az",
  val blockedPackages: Set<String> = emptySet(),
  val dndEnabled: Boolean = true,
  val soundEnabled: Boolean = true,
  val hapticsEnabled: Boolean = true
)

class SettingsDataStore(private val context: Context) {

  private object Keys {
    val LANGUAGE_CODE = stringPreferencesKey("language_code")
    val BLOCKED_PACKAGES = stringSetPreferencesKey("blocked_packages")
    val DND_ENABLED = booleanPreferencesKey("dnd_enabled")
    val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
  }

  val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
    AppSettings(
      languageCode = prefs[Keys.LANGUAGE_CODE] ?: "az",
      blockedPackages = prefs[Keys.BLOCKED_PACKAGES] ?: emptySet(),
      dndEnabled = prefs[Keys.DND_ENABLED] ?: true,
      soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
      hapticsEnabled = prefs[Keys.HAPTICS_ENABLED] ?: true
    )
  }

  suspend fun setLanguageCode(code: String) {
    context.dataStore.edit { it[Keys.LANGUAGE_CODE] = code }
  }

  suspend fun setBlockedPackages(packages: Set<String>) {
    context.dataStore.edit { it[Keys.BLOCKED_PACKAGES] = packages }
  }

  suspend fun toggleBlockedPackage(packageName: String) {
    context.dataStore.edit { prefs ->
      val current = prefs[Keys.BLOCKED_PACKAGES] ?: emptySet()
      prefs[Keys.BLOCKED_PACKAGES] = if (current.contains(packageName)) {
        current - packageName
      } else {
        current + packageName
      }
    }
  }

  suspend fun setDndEnabled(enabled: Boolean) {
    context.dataStore.edit { it[Keys.DND_ENABLED] = enabled }
  }

  suspend fun setSoundEnabled(enabled: Boolean) {
    context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
  }

  suspend fun setHapticsEnabled(enabled: Boolean) {
    context.dataStore.edit { it[Keys.HAPTICS_ENABLED] = enabled }
  }
}
