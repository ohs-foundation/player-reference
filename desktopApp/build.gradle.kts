/*
 * Copyright 2026 Open Health Stack Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.composeMultiplatform)
  alias(libs.plugins.composeCompiler)
  alias(libs.plugins.kotlinSerialization)
  id("spotless-conventions")
}

kotlin {
  jvm()

  sourceSets {
    jvmMain.dependencies {
      implementation(project(":ohs-player-library"))
      implementation(compose.desktop.currentOs)
      implementation(libs.kotlinx.coroutinesSwing)
      implementation(libs.ktor.client.cio)
    }
    jvmTest.dependencies {
      implementation(project(":ohs-player-library"))
      implementation(compose.desktop.currentOs)
      implementation(libs.koin.test)
    }
  }
}

// Desktop installer version. WiX/MSI (and jpackage) require a strict numeric
// MAJOR.MINOR.PATCH, so any Semantic Version pre-release suffix is stripped here.
val composePackageVersion: String =
  providers
    .environmentVariable("VERSION_NAME")
    .map { it.removePrefix("v").substringBefore('-') }
    .filter { it.matches(Regex("""\d+\.\d+\.\d+""")) }
    .getOrElse("1.0.0")

compose.desktop {
  application {
    mainClass = "dev.ohs.player.reference.app.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
      packageName = "PlayerReference"
      packageVersion = composePackageVersion

      val iconsDir = project.layout.projectDirectory.dir("../ohs-player-library/desktop-icons")
      macOS { iconFile.set(iconsDir.file("app-icon.icns")) }
      windows { iconFile.set(iconsDir.file("app-icon.ico")) }
      linux { iconFile.set(iconsDir.file("app-icon.png")) }
    }
  }
}

// Targets skipped on CI until their test setups are sorted out.
val isCi = providers.environmentVariable("CI").map(String::toBoolean).getOrElse(false)

if (isCi) {
  // No additional CI skips needed for desktop app module
}
