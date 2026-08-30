/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

/**
 * `composearch.android.compose` — turns on Jetpack Compose for a module. Applies the
 * Compose compiler plugin and flips `buildFeatures.compose`. Works on top of either
 * the library or the application convention (apply this one second). Each module
 * still declares the specific Compose artifacts it needs.
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
        val extension: CommonExtension<*, *, *, *, *, *> =
            extensions.findByType(ApplicationExtension::class.java)
                ?: extensions.getByType<LibraryExtension>()
        extension.buildFeatures.compose = true

        // Opt-in Compose compiler diagnostics. Build with `-PcomposeCompilerReports=true`
        // to emit per-module stability metrics + reports (which composables are
        // skippable/restartable, which parameters are unstable) under
        // <module>/build/compose_compiler/. Off by default so ordinary builds pay nothing.
        extensions.configure<ComposeCompilerGradlePluginExtension> {
            if (providers.gradleProperty("composeCompilerReports").isPresent) {
                val dir = layout.buildDirectory.dir("compose_compiler")
                reportsDestination.set(dir)
                metricsDestination.set(dir)
            }
        }
    }
}
