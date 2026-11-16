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

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.DisciplineViewModel
import com.example.ui.MainScreen
import com.example.ui.theme.BackgroundNavy
import com.example.ui.theme.MyApplicationTheme
import com.example.util.LocaleHelper

class MainActivity : ComponentActivity() {
  private val viewModel: DisciplineViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
      val baseContext = LocalContext.current
      val localizedContext = remember(appSettings.languageCode) {
        LocaleHelper.createLocalizedContext(baseContext, appSettings.languageCode)
      }

      CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration
      ) {
        MyApplicationTheme {
          Surface(
            modifier = Modifier
              .fillMaxSize()
              .testTag("discipline_planner_main_screen"),
            color = BackgroundNavy
          ) {
            MainScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}
