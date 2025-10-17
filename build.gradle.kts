// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // 'kotlin.compose' is deprecated and can be removed.
    // The Compose Compiler is handled by the Android Gradle Plugin.
}

// ❌ DO NOT apply the config script here.
// apply(from = "config.gradle.kts")  <-- REMOVE THIS LINE
