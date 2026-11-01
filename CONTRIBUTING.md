# Contributing to Discipline Planner

Thank you for your interest in contributing to **Discipline Planner**! We welcome contributions from developers, designers, and discipline enthusiasts worldwide.

This document provides a set of guidelines and instructions for contributing to this project.

---

## 📋 Table of Contents

- [Code of Conduct](#-code-of-conduct)
- [How Can I Contribute?](#-how-can-i-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Submitting Pull Requests](#submitting-pull-requests)
- [Development Setup](#-development-setup)
- [Coding Guidelines](#-coding-guidelines)
  - [Kotlin & Android](#kotlin--android)
  - [Web Frontend (HTML/JS/Tailwind)](#web-frontend-htmljstailwind)
- [Commit Message Guidelines](#-commit-message-guidelines)
- [License Agreement](#-license-agreement)

---

## 📜 Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). Please treat all community members with respect, kindness, and professionalism.

---

## 💡 How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates. When creating a bug report, include as much detail as possible:

* **Clear, descriptive title**
* **Steps to reproduce the issue**
* **Expected vs. actual behavior**
* **Device details** (Android version, device model, or browser name/version)
* **Screenshots or screen recordings**, if applicable

### Suggesting Enhancements

Feature requests are very welcome! When opening a suggestion:

* Explain the **problem** you are trying to solve or the **value** the feature provides.
* Describe your suggested solution or UI mockup.
* Keep in mind our design philosophy: **minimalist, distraction-free, high-contrast, and fast**.

### Submitting Pull Requests

1. **Fork the repo** and create your branch from `main`:
   ```bash
   git checkout -b feature/my-new-feature
   ```
2. **Implement your changes** cleanly and test thoroughly.
3. **Run local tests** to ensure no regressions:
   ```bash
   gradle :app:testDebugUnitTest
   ```
4. **Commit your changes** following the [Commit Message Guidelines](#-commit-message-guidelines).
5. **Push to your fork**:
   ```bash
   git push origin feature/my-new-feature
   ```
6. **Open a Pull Request** against the `main` branch of [SabutayCrafts/discipline-planner](https://github.com/SabutayCrafts/discipline-planner).

---

## 🛠️ Development Setup

### Requirements
* **JDK 17** or higher
* **Android Studio** (Ladybug / Hedgehog or newer)
* **Android SDK** API 24 (minimum) to API 36 (target/compile)
* **Gradle 8.x**

### Building Locally
```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/discipline-planner.git
cd discipline-planner

# Compile and verify build
gradle assembleDebug

# Run unit and Robolectric tests
gradle :app:testDebugUnitTest
```

---

## 📐 Coding Guidelines

### Kotlin & Android
* Adhere to [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
* Maintain edge-to-edge support with `enableEdgeToEdge()` and proper insets handling.
* Do not introduce heavy external dependencies without prior discussion in an issue.
* Keep package identifiers aligned with `com.sabutaycrafts.disciplineplanner`.

### Web Frontend (HTML/JS/Tailwind)
* Follow the unified color palette:
  * **Canvas background**: `#0B1120`
  * **Cards**: `#1E293B` (with `#334155` border)
  * **Primary accents**: Royal Blue `#2563EB`
  * **Text**: `#F8FAFC` (primary), `#94A3B8` (secondary)
* Keep vector avatars generated via procedural SVG with consistent aspect ratios and idle micro-animations.
* Ensure all persistent choices save to `localStorage` safely with fallback handling.

---

## 💬 Commit Message Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/):

* `feat: Add Knight Helmet unlockable gear`
* `fix: Resolve task alarm interval triggering twice`
* `docs: Update README with building instructions`
* `style: Clean up card padding in Profile tab`
* `refactor: Optimize SVG avatar generator function`
* `test: Add Robolectric test for 25-tier catalog`

---

## ⚖️ License Agreement

All code submitted to **Discipline Planner** must be compatible with the **GNU Affero General Public License Version 3.0 (AGPLv3)**. By contributing, you agree that your contributions will be licensed under the project's [AGPLv3 License](LICENSE).
