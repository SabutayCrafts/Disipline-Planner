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

package com.example.util

import android.app.LocaleManager
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.Locale

data class SupportedLanguage(
  val code: String,
  val nativeName: String,
  val flagEmoji: String
)

private class LocalizedContextWrapper(
  base: Context,
  private val localizedResourcesContext: Context
) : ContextWrapper(base) {
  override fun getResources(): Resources = localizedResourcesContext.resources
  override fun getAssets(): AssetManager = localizedResourcesContext.assets
}

object LocaleHelper {
  val SUPPORTED_LANGUAGES = listOf(
    SupportedLanguage("az", "Azərbaycan dili", "🇦🇿"),
    SupportedLanguage("en", "English", "🇬🇧")
  )

  fun createLocalizedContext(base: Context, languageCode: String): Context {
    val locale = Locale.forLanguageTag(languageCode)
    Locale.setDefault(locale)
    val config = Configuration(base.resources.configuration)
    config.setLocale(locale)
    config.setLocales(LocaleList(locale))
    val configContext = base.createConfigurationContext(config)
    return LocalizedContextWrapper(base, configContext)
  }

  fun syncSystemAppLocale(context: Context, languageCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      try {
        val localeManager = context.getSystemService(LocaleManager::class.java)
        localeManager?.applicationLocales = LocaleList.forLanguageTags(languageCode)
      } catch (_: Exception) {
        // Ignore on devices without full LocaleManager support
      }
    }
  }
}
