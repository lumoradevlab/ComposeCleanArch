/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package com.composearch.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/** The `libs` version catalog, reachable from inside convention plugins. */
internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

/** Shared Android config: one place that owns compileSdk/minSdk/Java/jvmTarget. */
internal fun Project.configureKotlinAndroid(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        compileSdk = 34
        defaultConfig {
            minSdk = 24
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        // Android Lint = the "junk" finder for resources/manifest/API misuse
        // (unused resources, obsolete APIs, …). Report-first: it never breaks the
        // build yet (abortOnError = false); flip that on once findings are triaged.
        // `checkDependencies` makes the app's lint also scan the modules it pulls
        // in, so a single `:app:lintDevDebug` covers the whole graph. No baseline
        // is set on purpose — pointing at a missing baseline makes lint create it
        // and fail the first run; add one later with `updateLintBaseline`.
        lint {
            abortOnError = false
            warningsAsErrors = false
            checkDependencies = true
            checkReleaseBuilds = false
            sarifReport = true
            htmlReport = true
        }
    }
    configureKotlin()
}

/** Same Java/jvmTarget contract for the pure-Kotlin (non-Android) modules. */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    configureKotlin()
}

private fun Project.configureKotlin() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
}
