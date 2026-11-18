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

import android.content.Context
import android.content.ContextWrapper
import androidx.test.core.app.ApplicationProvider
import com.example.util.LocaleHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Discipline Planner", appName)
  }

  @Test
  fun `localized context preserves base wrapper chain and resolves az strings`() {
    val base = ApplicationProvider.getApplicationContext<Context>()
    val azCtx = LocaleHelper.createLocalizedContext(base, "az")
    assertTrue(azCtx is ContextWrapper)
    assertSame(base, (azCtx as ContextWrapper).baseContext)
    assertEquals("Bugün", azCtx.getString(R.string.nav_today))
    assertEquals("Hələ heç bir tapşırıq əlavə edilmiyib", azCtx.getString(R.string.empty_habits_title))

    val enCtx = LocaleHelper.createLocalizedContext(base, "en")
    assertEquals("Today", enCtx.getString(R.string.nav_today))
  }
}
