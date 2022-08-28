package dev.roshana.buildsrc

object BuildPlugins {
    const val androidGradle = "com.android.tools.build:gradle:${Versions.gradle}"
    const val kotlinGradlePlugin =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val hiltPlugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
}