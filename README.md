<div align="center">

# 🏛️ Discipline Planner

**Daily Habit Forge & Self-Mastery Platform**

[![License: AGPL v3](https://img.shields.io/badge/License-AGPLv3-2563EB.svg?style=for-the-badge&logo=opensourceinitiative&logoColor=white)](LICENSE)
[![Release](https://img.shields.io/badge/Release-v0.1.0-2563EB.svg?style=for-the-badge&logo=github)](https://github.com/SabutayCrafts/discipline-planner/releases)
[![Platform: Android](https://img.shields.io/badge/Platform-Android_7.0+-3DDC84.svg?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Language: Kotlin](https://img.shields.io/badge/Language-Kotlin_2.0-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Author: SabutayCrafts](https://img.shields.io/badge/Developer-SabutayCrafts-0B1120.svg?style=for-the-badge&logo=github&logoColor=white)](https://github.com/SabutayCrafts)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-emerald.svg?style=for-the-badge)](https://github.com/SabutayCrafts/discipline-planner/pulls)

<p align="center">
  <i>"Discipline is choosing between what you want now, and what you want most."</i>
</p>

</div>

---

## 🌟 Overview

**Discipline Planner** is a 100% native Kotlin & Jetpack Compose self-mastery Android application engineered to build relentless consistency, block phone distractions during deep work, and sync daily habit reminders with your device's built-in Clock alarm system.

Featuring a **3-tab streamlined layout (`Today`, `Focus`, `Statistics`)**, an active **Focus Distraction Blocker (Do Not Disturb + Selected App Interception)**, **System Clock Alarm integration**, **bilingual Azerbaijani/English i18n**, and **25-tier streak milestones**, the app turns daily accountability into a high-focus, distraction-free experience with zero account requirements or paywalls.

Developed and maintained by **[SabutayCrafts](https://github.com/SabutayCrafts)**.

---

## 🎯 Key Features

### 1. ✅ Today Execution & Native Clock Alarms
- **Daily Habit Forge**: Create, categorize, edit, and complete daily habits with animated circular progress rings, tactile haptic feedback, and optional completion chimes.
- **Native Phone Alarm Integration**: Attach optional daily reminder times to any habit and send them directly to your phone's built-in **Clock** app (`AlarmClock.ACTION_SET_ALARM`) for rock-solid system alarms.
- **Broader Squarish Quick-Add Button**: Ergonomic card-shaped primary action button (`56dp × 52dp`, `12dp` corner radius).

### 2. 🛡️ Deep Focus Mode & Real Distraction Blocker
- **Active App Interception**: Pick distracting apps installed on your device. While a Focus countdown is active, a compliant Foreground Service monitors app switches via `UsageStatsManager` and displays a full-screen **Focus Shield** overlay whenever a blocked app is opened.
- **Auto Do Not Disturb (DND)**: Automatically silences incoming notifications during Focus sessions while keeping system alarms audible, and restores your previous DND state when the session ends.
- **Flexible Durations**: 15, 25, 45, 60-minute presets plus custom duration support (`1–180 min`) with live notification countdown and stoic quotes.

### 3. 📊 Statistics & 25-Tier Streak Milestones
- **Real Consistency Analytics**: Tracks Active Streak, Total Completions, Total Deep Focus minutes, a 7-day consistency heat strip, and Focus session history.
- **25-Tier Discipline Milestones (Day 1 to 3+ Years)**: Progressive streak tiers from *Level 1 — Novice* up to *Level 25 — The Supreme Emperor (1095+ Days)* with visual progress bars and unlock filters (*All*, *Unlocked*, *Locked*).

### 4. 🌐 Multi-Language Support (i18n) & Visual Polish
- **Instant In-App Language Switching**: Full **Azerbaijani (`az`)** and **English (`en`)** localization backed by Android string resources and `Preferences DataStore`, plus Android 13+ Per-App Language support (`generateLocaleConfig`).
- **Tactical Dark Minimalism**: Deep Navy canvas (`#0B1120`), Dark Slate cards (`#1E293B`), Royal Blue accents (`#2563EB`), Emerald completions (`#10B981`), and bundled `Outfit` + `Inter` typography.

---

## 🏗️ Tech Stack & Architecture

- **Operating System / Target**: Android 7.0 (API 24) up to Android 16 (API 36)
- **Application ID**: `com.sabutaycrafts.disciplineplanner`
- **Architecture**: Clean MVVM (`Room SQLite` + `Preferences DataStore` -> `Repository` -> `StateFlow ViewModel` -> `Jetpack Compose M3`)
- **System Integrations**:
  - `ForegroundService` (`specialUse`) + `UsageStatsManager` + `WindowManager` Focus Shield overlay
  - `NotificationManager` Do Not Disturb policy control
  - `AlarmClock.ACTION_SET_ALARM` system Clock alarm integration
  - `ToneGenerator` & `LocalHapticFeedback` tactile responses
  - Bundled local Google Fonts (`Outfit` & `Inter`)

---

## 📂 Project Structure

```
discipline-planner/
├── .github/                      # CI & automated release workflows
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt        # Compose host with reactive Locale provider
│   │   │   │   ├── alarm/                 # System Clock alarm integration
│   │   │   │   ├── data/                  # Room database, DAOs, DataStore & Repository
│   │   │   │   ├── focus/                 # FocusBlockerService, StateHolder & Permissions
│   │   │   │   ├── model/                 # 25-tier Milestones & Habit Categories
│   │   │   │   ├── ui/                    # Screens, Components, ViewModel & M3 Theme
│   │   │   │   └── util/                  # LocaleHelper & SoundHelper
│   │   │   └── res/
│   │   │       ├── font/                  # Bundled Outfit & Inter TTF fonts
│   │   │       ├── values/                # English strings & colors
│   │   │       └── values-az/             # Azerbaijani localized strings
│   │   └── test/java/com/example/         # Unit & Robolectric tests
│   └── build.gradle.kts                   # Module Gradle config
├── gradle/libs.versions.toml              # Centralized Version Catalog
├── build.gradle.kts                       # Root Gradle build script
├── settings.gradle.kts                    # Project settings: "Discipline Planner"
├── metadata.json                          # Project platform metadata
├── LICENSE                                # GNU Affero General Public License v3.0 (AGPLv3)
└── README.md                              # Project documentation
```

---

## 🚀 Building & Running

### Prerequisites
1. **JDK 17** or newer installed (`java -version`)
2. **Android SDK** (API 24 minimum, target API 36)
3. **Gradle 8.x+**

### 1. Clone the Repository
```bash
git clone https://github.com/SabutayCrafts/discipline-planner.git
cd discipline-planner
```

### 2. Run Local Unit Tests
```bash
gradle :app:testDebugUnitTest
```

### 3. Build the Debug APK
```bash
gradle :app:assembleDebug
```
The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📦 Releases & Versioning (SemVer)

Discipline Planner follows **[Semantic Versioning 2.0.0](https://semver.org/)** (`MAJOR.MINOR.PATCH`):
- **Current Version**: `v0.1.0`
- **`MAJOR`**: Incompatible breaking structural changes or complete platform redesigns.
- **`MINOR`**: Backwards-compatible new features, new avatar gear, new tiers, or major enhancements.
- **`PATCH`**: Backwards-compatible bug fixes, UI adjustments, and performance improvements.

### 🚀 Automated GitHub Releases System

Publishing releases is fully automated via GitHub Actions (`.github/workflows/release.yml`), identical to the workflow architecture of YoungLionOrganization projects:

#### Option 1: Trigger via Git Tag (Terminal)
Tag your commit and push it to GitHub:
```bash
git tag v0.1.0
git push origin v0.1.0
```

#### Option 2: One-Click Trigger via GitHub UI (`workflow_dispatch`)
1. Go to the **Actions** tab in your GitHub repository.
2. Select **Release Application** in the left sidebar.
3. Click the **Run workflow** dropdown button.
4. Input the version tag (e.g. `v0.1.0`), choose draft/pre-release if desired, and click **Run workflow**.

#### Automated Pipeline Steps:
1. Runs JVM & Robolectric unit tests (`gradle :app:testDebugUnitTest`).
2. Builds the Android APK artifact (`gradle :app:assembleDebug`).
3. Renames and packages the release as `DisciplinePlanner-${VERSION}.apk`.
4. Calculates SHA-256 verification checksums (`DisciplinePlanner-${VERSION}.apk.sha256`).
5. Generates automatic GitHub Release notes with direct download links.

---

## 🤝 Contributing & Community

Contributions, feature requests, and discussions are warmly welcome!

- 📘 Please read our [**Contributing Guide**](CONTRIBUTING.md) for pull request workflows, local build setups, and coding conventions.
- 📜 By participating, you agree to uphold our [**Code of Conduct**](CODE_OF_CONDUCT.md).
- 🛡️ To report security concerns, please refer to our [**Security Policy**](SECURITY.md).

---

## 📜 License

This project is licensed under the **GNU Affero General Public License Version 3.0 (AGPLv3)**.

```
Discipline Planner - Daily Habit & Self-Mastery Platform
Copyright (C) 2026 Discipline Planner Contributors & SabutayCrafts

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published
by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

See the [LICENSE](LICENSE) file for complete terms and conditions.

---

<div align="center">
  <sub>Crafted with discipline by <a href="https://github.com/SabutayCrafts">SabutayCrafts</a>. Keep the promise to yourself.</sub>
</div>
